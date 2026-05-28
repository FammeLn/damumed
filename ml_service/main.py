"""
IntelliHeart ML Microservice
Микросервис для классификации интентов голоса на основе обученной нейросети
"""

import os
import json
import logging
from typing import Optional
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
    description="Микросервис для классификации интентов голоса",
    version="1.0.0"
)

# Глобальные переменные для модели и векторизатора
model = None
vectorizer = None

# Конфигурация LLM (OpenAI-compatible)
LLM_ENABLED = os.getenv("LLM_ENABLED", "false").lower() in ("1", "true", "yes", "y")
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
    "You are a medical voice assistant for a mobile app. "
    "Determine the user's intent and respond in Kazakh. "
    "Return ONLY JSON with keys: text (string), action (string). "
    "Allowed actions: NAVIGATE_TO_APPOINTMENT, NAVIGATE_TO_RECORDS, "
    "CALL_DOCTOR, NAVIGATE_TO_PROFILE, NONE. "
    "Do not include any extra keys or explanations."
)


def is_llm_configured() -> bool:
    return LLM_ENABLED and bool(LLM_API_KEY) and bool(LLM_MODEL)


def parse_llm_json(content: str) -> Optional[dict]:
    try:
        return json.loads(content)
    except Exception:
        start = content.find("{")
        end = content.rfind("}")
        if start != -1 and end != -1 and end > start:
            return json.loads(content[start:end + 1])
    return None


async def call_llm(text: str) -> Optional["PredictResponse"]:
    if not is_llm_configured():
        return None

    payload = {
        "model": LLM_MODEL,
        "messages": [
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": text}
        ],
        "temperature": 0.2,
        "max_tokens": 200
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
            if not parsed:
                logger.warning("LLM вернул некорректный JSON: %s", content)
                return None

            action = parsed.get("action")
            text_out = parsed.get("text")
            if not isinstance(text_out, str) or not isinstance(action, str):
                return None
            if action not in ALLOWED_ACTIONS:
                logger.warning("LLM вернул неизвестный action: %s", action)
                return None
            return PredictResponse(text=text_out, action=action)
    except Exception as e:
        logger.error("Ошибка LLM запроса: %s", str(e))
        return None


def predict_with_classifier(text: str) -> PredictResponse:
    if model is None or vectorizer is None:
        logger.error("Модель не загружена")
        raise HTTPException(
            status_code=503,
            detail="Модель еще не загружена. Повторите попытку позже."
        )

    text_vector = vectorizer.transform([text])
    prediction = model.predict(text_vector)
    probabilities = model.predict_proba(text_vector)
    predicted_intent = prediction[0]
    confidence = np.max(probabilities[0])

    logger.info(f"Предсказан интент: {predicted_intent} (уверенность: {confidence:.2f})")

    response = INTENT_RESPONSES.get(
        predicted_intent,
        INTENT_RESPONSES["UNKNOWN"]
    )
    return PredictResponse(**response)

# Маппинг интентов на казахские ответы
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


class PredictRequest(BaseModel):
    """
    Запрос на предсказание интента
    """
    text: str


class PredictResponse(BaseModel):
    """
    Ответ с предсказанным интентом и действием
    """
    text: str
    action: str


@app.on_event("startup")
async def startup_event():
    """
    Событие запуска приложения
    Загружает модель и векторизатор из файлов
    """
    global model, vectorizer
    
    try:
        # Определяем пути к файлам моделей (ищем в текущей директории ml_service)
        current_dir = os.path.dirname(os.path.abspath(__file__))
        
        model_path = os.path.join(current_dir, "intent_model.pkl")
        vectorizer_path = os.path.join(current_dir, "vectorizer.pkl")
        
        logger.info(f"Загрузка модели из {model_path}")
        logger.info(f"Загрузка векторизатора из {vectorizer_path}")
        
        # Загружаем модель и векторизатор
        model = joblib.load(model_path)
        vectorizer = joblib.load(vectorizer_path)
        
        logger.info("✅ Модель и векторизатор успешно загружены")
        
    except Exception as e:
        if is_llm_configured():
            logger.warning(f"⚠️ Модель не загружена, но LLM включен: {str(e)}")
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
    Эндпоинт для предсказания интента на основе текста
    
    Args:
        request (PredictRequest): Запрос с текстом на казахском/русском языке
        
    Returns:
        PredictResponse: Ответ с интентом и действием
        
    Raises:
        HTTPException: Если модель не загружена или текст пуст
    """
    
    # Проверяем входной текст
    if not request.text or len(request.text.strip()) == 0:
        logger.warning("Получен пустой текст")
        raise HTTPException(
            status_code=400,
            detail="Текст не может быть пустым"
        )
    
    logger.info(f"Обработка текста: {request.text[:50]}...")

    if is_llm_configured():
        llm_response = await call_llm(request.text)
        if llm_response:
            return llm_response

    try:
        return predict_with_classifier(request.text)
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Ошибка при предсказании: {str(e)}")
        raise HTTPException(
            status_code=500,
            detail=f"Ошибка при обработке запроса: {str(e)}"
        )


@app.post("/batch_predict")
async def batch_predict(requests: list[PredictRequest]):
    """
    Эндпоинт для пакетного предсказания интентов
    
    Args:
        requests (list[PredictRequest]): Список текстов для обработки
        
    Returns:
        list[PredictResponse]: Список результатов
    """
    
    results = []
    
    for request in requests:
        try:
            if is_llm_configured():
                llm_response = await call_llm(request.text)
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
        "description": "Микросервис для классификации интентов голоса",
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
