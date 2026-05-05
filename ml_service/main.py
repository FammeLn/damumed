"""
IntelliHeart ML Microservice
Микросервис для классификации интентов голоса на основе обученной нейросети
"""

import os
import logging
from typing import Optional
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import joblib
import numpy as np

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
        "version": "1.0.0"
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
    
    # Проверяем, загружена ли модель
    if model is None or vectorizer is None:
        logger.error("Модель не загружена")
        raise HTTPException(
            status_code=503,
            detail="Модель еще не загружена. Повторите попытку позже."
        )
    
    # Проверяем входной текст
    if not request.text or len(request.text.strip()) == 0:
        logger.warning("Получен пустой текст")
        raise HTTPException(
            status_code=400,
            detail="Текст не может быть пустым"
        )
    
    try:
        logger.info(f"Обработка текста: {request.text[:50]}...")
        
        # Векторизуем текст
        text_vector = vectorizer.transform([request.text])
        
        # Получаем предсказание от модели
        # Модель возвращает вероятности для каждого класса
        prediction = model.predict(text_vector)
        probabilities = model.predict_proba(text_vector)
        
        # Получаем имя предсказанного класса
        predicted_intent = prediction[0]
        confidence = np.max(probabilities[0])
        
        logger.info(f"Предсказан интент: {predicted_intent} (уверенность: {confidence:.2f})")
        
        # Получаем ответ из маппинга
        response = INTENT_RESPONSES.get(
            predicted_intent,
            INTENT_RESPONSES["UNKNOWN"]
        )
        
        return PredictResponse(**response)
        
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
    
    if model is None or vectorizer is None:
        raise HTTPException(
            status_code=503,
            detail="Модель еще не загружена"
        )
    
    results = []
    
    for request in requests:
        try:
            # Векторизуем текст
            text_vector = vectorizer.transform([request.text])
            
            # Предсказываем интент
            prediction = model.predict(text_vector)
            predicted_intent = prediction[0]
            
            # Получаем ответ из маппинга
            response = INTENT_RESPONSES.get(
                predicted_intent,
                INTENT_RESPONSES["UNKNOWN"]
            )
            
            results.append(PredictResponse(**response))
            
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
