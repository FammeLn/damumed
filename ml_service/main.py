"""
IntelliHeart ML Microservice
Микросервис для классификации интентов голоса на основе обученной нейросети и LLM диалога
"""

import os
import json
import logging
from typing import Optional, List
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import joblib
import numpy as np
import httpx

# Конфигурация логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Инициализация FastAPI приложения
app = FastAPI(
    title="IntelliHeart ML Service",
    description="Микросервис для классификации интентов голоса и ведения LLM-диалогов",
    version="1.0.0"
)

# Глобальные переменные для модели и векторизатора
model = None
vectorizer = None

# Конфигурация LLM (OpenAI-compatible)
LLM_ENABLED = os.getenv("LLM_ENABLED", "true").lower() in ("1", "true", "yes", "y")
LLM_API_URL = os.getenv("LLM_API_URL", "https://api.openai.com/v1").rstrip("/")
LLM_API_KEY = os.getenv("LLM_API_KEY")
LLM_MODEL = os.getenv("LLM_MODEL")

ALLOWED_ACTIONS = {
    "NAVIGATE_TO_APPOINTMENT",
    "NAVIGATE_TO_RECORDS",
    "CALL_DOCTOR",
    "NAVIGATE_TO_PROFILE",
    "NONE"
}

SYSTEM_PROMPT = (
    "You are a medical assistant for a mobile app. "
    "Determine the user's intent and respond in the same language as the user (Kazakh or Russian). "
    "Return ONLY JSON with keys: 'text' (string, your dialogue response to the user), 'action' (string, the navigation action). "
    "Allowed actions: NAVIGATE_TO_APPOINTMENT, NAVIGATE_TO_RECORDS, "
    "CALL_DOCTOR, NAVIGATE_TO_PROFILE, NONE. "
    "Do not include any extra keys, markdown formatting, or explanations outside the JSON."
)


# Маппинг интентов на казахские ответы (резервный локальный классификатор)
INTENT_RESPONSES = {
    "NAVIGATE_TO_APPOINTMENT": {
        "text": "Дәрігерге жазылу бөліміне өтудеміз.",
        "action": "NAVIGATE_TO_APPOINTMENT"
    },
    "NAVIGATE_TO_RECORDS": {
        "text": "Медициналық картаңызды ашып жатырмын.",
        "action": "NAVIGATE_TO_RECORDS"
    },
    "CALL_DOCTOR": {
        "text": "Дәрігерді үйге шақыру формасы.",
        "action": "CALL_DOCTOR"
    },
    "NAVIGATE_TO_PROFILE": {
        "text": "Сіздің жеке кабинетке өтіп барамын.",
        "action": "NAVIGATE_TO_PROFILE"
    },
    "UNKNOWN": {
        "text": "Кешіріңіз, мен сізді түсінбедім. Сұрағыңызды қайталаңызшы.",
        "action": "NONE"
    }
}


class ChatHistoryItem(BaseModel):
    """
    Элемент истории чата
    """
    sender: str
    text: str


class PredictRequest(BaseModel):
    """
    Запрос на предсказание интента с опциональной историей сообщений
    """
    text: str
    history: Optional[List[ChatHistoryItem]] = None


class PredictResponse(BaseModel):
    """
    Ответ с предсказанным интентом и действием
    """
    text: str
    action: str


class DuckChatClient:
    """
    Клиент для взаимодействия с бесплатным AI чатом DuckDuckGo (GPT-4o-mini)
    """
    def __init__(self):
        self.client = httpx.AsyncClient(
            headers={
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                "Accept": "*/*",
                "Accept-Language": "en-US,en;q=0.5",
                "Sec-Fetch-Dest": "empty",
                "Sec-Fetch-Mode": "cors",
                "Sec-Fetch-Site": "same-origin",
            },
            timeout=15.0
        )

    async def get_vqd(self) -> Optional[str]:
        try:
            response = await self.client.get(
                "https://duckduckgo.com/duckchat/v1/status",
                headers={"x-vqd-accept": "1"}
            )
            response.raise_for_status()
            return response.headers.get("x-vqd-4")
        except Exception as e:
            logger.error("Ошибка получения VQD токена от DuckChat: %s", str(e))
            return None

    async def chat(self, messages_list: list[dict], model: str = "gpt-4o-mini") -> Optional[str]:
        vqd = await self.get_vqd()
        if not vqd:
            return None

        payload = {
            "model": model,
            "messages": messages_list
        }

        headers = {
            "x-vqd-4": vqd,
            "Content-Type": "application/json",
            "Accept": "text/event-stream",
        }

        try:
            response = await self.client.post(
                "https://duckduckgo.com/duckchat/v1/chat",
                headers=headers,
                json=payload
            )
            response.raise_for_status()
            
            text_response = ""
            for line in response.text.split("\n"):
                if line.startswith("data: "):
                    data_str = line[6:].strip()
                    if data_str == "[DONE]":
                        break
                    try:
                        data_json = json.loads(data_str)
                        if "message" in data_json:
                            text_response += data_json["message"]
                    except Exception:
                        pass
            return text_response if text_response else None
        except Exception as e:
            logger.error("Ошибка при отправке сообщения в DuckChat: %s", str(e))
            return None


# Инициализируем глобальный DuckChat клиент
duck_chat_client = DuckChatClient()


def is_llm_configured() -> bool:
    # LLM всегда доступен, так как есть бесплатный встроенный DuckChat клиент
    return LLM_ENABLED


def parse_llm_json(content: str) -> Optional[dict]:
    try:
        return json.loads(content)
    except Exception:
        start = content.find("{")
        end = content.rfind("}")
        if start != -1 and end != -1 and end > start:
            try:
                return json.loads(content[start:end + 1])
            except Exception:
                pass
    return None


async def call_llm(text: str, history: Optional[List[ChatHistoryItem]] = None) -> Optional[PredictResponse]:
    if not LLM_ENABLED:
        return None

    # Формируем список сообщений
    messages = [{"role": "system", "content": SYSTEM_PROMPT}]
    if history:
        # Ограничиваем историю 10 сообщениями
        for msg in history[-10:]:
            role = "user" if msg.sender == "USER" else "assistant"
            messages.append({"role": role, "content": msg.text})
    
    # Добавляем текущее сообщение
    messages.append({"role": "user", "content": text})

    # 1. Если настроен официальный OpenAI-совместимый LLM
    if bool(LLM_API_KEY) and bool(LLM_MODEL):
        payload = {
            "model": LLM_MODEL,
            "messages": messages,
            "temperature": 0.2,
            "max_tokens": 250
        }

        headers = {
            "Authorization": f"Bearer {LLM_API_KEY}",
            "Content-Type": "application/json"
        }

        try:
            async with httpx.AsyncClient(timeout=12.0) as client:
                response = await client.post(
                    f"{LLM_API_URL}/chat/completions",
                    headers=headers,
                    json=payload
                )
                response.raise_for_status()
                data = response.json()
                content = data["choices"][0]["message"]["content"]
                parsed = parse_llm_json(content)
                if parsed:
                    action = parsed.get("action")
                    text_out = parsed.get("text")
                    if isinstance(text_out, str) and isinstance(action, str) and action in ALLOWED_ACTIONS:
                        return PredictResponse(text=text_out, action=action)
        except Exception as e:
            logger.error("Ошибка запроса к OpenAI/LLM API: %s. Переключаемся на резервный DuckChat.", str(e))

    # 2. Иначе используем DuckChat GPT-4o-mini
    logger.info("Используем встроенный LLM-клиент DuckChat (GPT-4o-mini)...")
    try:
        content = await duck_chat_client.chat(messages)
        if content:
            parsed = parse_llm_json(content)
            if parsed:
                action = parsed.get("action")
                text_out = parsed.get("text")
                if isinstance(text_out, str) and isinstance(action, str) and action in ALLOWED_ACTIONS:
                    return PredictResponse(text=text_out, action=action)
                else:
                    return PredictResponse(text=parsed.get("text", content), action="NONE")
            else:
                return PredictResponse(text=content, action="NONE")
    except Exception as e:
        logger.error("Не удалось получить ответ от DuckChat: %s", str(e))

    return None


def predict_with_classifier(text: str) -> PredictResponse:
    if model is None or vectorizer is None:
        logger.error("Модель классификатора не загружена")
        raise HTTPException(
            status_code=503,
            detail="Модель классификатора не загружена. Повторите попытку позже."
        )

    text_vector = vectorizer.transform([text])
    prediction = model.predict(text_vector)
    probabilities = model.predict_proba(text_vector)
    predicted_intent = prediction[0]
    confidence = np.max(probabilities[0])

    logger.info(f"Локальный классификатор предсказал интент: {predicted_intent} (уверенность: {confidence:.2f})")

    response = INTENT_RESPONSES.get(
        predicted_intent,
        INTENT_RESPONSES["UNKNOWN"]
    )
    return PredictResponse(**response)


@app.on_event("startup")
async def startup_event():
    """
    Событие запуска приложения
    Загружает модель и векторизатор из файлов
    """
    global model, vectorizer
    
    try:
        current_dir = os.path.dirname(os.path.abspath(__file__))
        
        model_path = os.path.join(current_dir, "intent_model.pkl")
        vectorizer_path = os.path.join(current_dir, "vectorizer.pkl")
        
        logger.info(f"Загрузка модели из {model_path}")
        logger.info(f"Загрузка векторизатора из {vectorizer_path}")
        
        model = joblib.load(model_path)
        vectorizer = joblib.load(vectorizer_path)
        
        logger.info("✅ Модель и векторизатор успешно загружены")
        
    except Exception as e:
        if is_llm_configured():
            logger.warning(f"⚠️ Модель классификатора не загружена, но LLM активен: {str(e)}")
        else:
            logger.error(f"❌ Ошибка при загрузке модели: {str(e)}")
            raise RuntimeError(f"Не удалось загрузить модель: {str(e)}")


@app.get("/health")
async def health_check():
    """
    Проверка здоровья сервиса
    """
    return {
        "status": "healthy",
        "service": "IntelliHeart ML Service",
        "version": "1.0.0",
        "llmEnabled": is_llm_configured()
    }


@app.post("/predict", response_model=PredictResponse)
async def predict(request: PredictRequest):
    """
    Эндпоинт для предсказания интента на основе текста с LLM диалогом
    """
    if not request.text or len(request.text.strip()) == 0:
        logger.warning("Получен пустой текст")
        raise HTTPException(
            status_code=400,
            detail="Текст не может быть пустым"
        )
    
    logger.info(f"Обработка текста: {request.text[:50]}...")

    if LLM_ENABLED:
        llm_response = await call_llm(request.text, request.history)
        if llm_response:
            return llm_response

    try:
        return predict_with_classifier(request.text)
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Ошибка при классификации: {str(e)}")
        raise HTTPException(
            status_code=500,
            detail=f"Ошибка при обработке запроса: {str(e)}"
        )


@app.post("/batch_predict")
async def batch_predict(requests: list[PredictRequest]):
    """
    Эндпоинт для пакетной обработки
    """
    results = []
    
    for request in requests:
        try:
            if LLM_ENABLED:
                llm_response = await call_llm(request.text, request.history)
                if llm_response:
                    results.append(llm_response)
                    continue

            results.append(predict_with_classifier(request.text))
        except Exception as e:
            logger.error(f"Ошибка при обработке текста '{request.text}': {str(e)}")
            results.append(PredictResponse(**INTENT_RESPONSES["UNKNOWN"]))
    
    return results


@app.get("/")
async def root():
    """
    Корневой эндпоинт с информацией о сервисе
    """
    return {
        "name": "IntelliHeart ML Service",
        "description": "Микросервис для классификации интентов голоса и ведения LLM-диалогов",
        "version": "1.0.0",
        "endpoints": {
            "health": "GET /health",
            "predict": "POST /predict",
            "batch_predict": "POST /batch_predict",
            "docs": "GET /docs"
        }
    }


if __name__ == "__main__":
    import uvicorn
    
    # Запускаем сервер на порту 8000
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=8000,
        log_level="info"
    )
