# Микросервисная архитектура IntelliHeart

## Обзор системы

IntelliHeart использует трёхуровневую архитектуру:

```
┌──────────────────────────────────────────────────────────────┐
│                    Android Frontend                           │
│            (Jetpack Compose, Kotlin)                         │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ • Голосовой ввод (SpeechRecognizer)               │    │
│  │ • UI экраны (4 основных + 2 дополнительных)      │    │
│  │ • Text-to-Speech (казахский язык)                 │    │
│  └─────────────────────────────────────────────────────┘    │
│                        ↓ (JSON)                              │
└──────────────────────────────────────────────────────────────┘
        Retrofit HTTP Client (http://localhost:8080)
                        ↓
┌──────────────────────────────────────────────────────────────┐
│                    Spring Boot Backend                        │
│            (REST API, Kotlin)                                │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ • AiAssistantService (интеграция с ML)            │    │
│  │ • REST контроллеры (7 эндпоинтов)                 │    │
│  │ • Entity/Repository слой (Patient, Doctor)        │    │
│  └─────────────────────────────────────────────────────┘    │
│                        ↓ (JSON)                              │
└──────────────────────────────────────────────────────────────┘
       RestTemplate HTTP Client (http://localhost:8000)
                        ↓
┌──────────────────────────────────────────────────────────────┐
│              Python ML Microservice                          │
│              (FastAPI, Python)                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ • intent_model.pkl (классификатор)                 │    │
│  │ • vectorizer.pkl (TfidfVectorizer)                 │    │
│  │ • /predict эндпоинт (NLP)                          │    │
│  └─────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────┘
```

## Компоненты системы

### 1. Frontend (Android)
- **Язык:** Kotlin + Jetpack Compose
- **Основной сервис:** RetrofitClient
- **API база:** http://localhost:8080
- **Экраны:** HomeScreen, AppointmentScreen, MedicalRecordScreen, ProfileScreen, CallDoctorHomeScreen
- **Голосовой модуль:** VoiceAssistantManager, TextToSpeechManager

### 2. Backend (Spring Boot)
- **Язык:** Kotlin + Spring Boot 3.x
- **Порт:** 8080
- **REST API:** 7 эндпоинтов
- **ML интеграция:** AiAssistantService (вызывает Python ML сервис)
- **Резервная логика:** Если ML недоступен, использует keyword-based logic

### 3. ML Microservice (Python FastAPI)
- **Язык:** Python 3.8+
- **Фреймворк:** FastAPI
- **Порт:** 8000
- **Модели:** intent_model.pkl, vectorizer.pkl
- **Эндпоинты:** /health, /predict, /batch_predict

## Запуск системы

### Шаг 1: Запустить ML Microservice

```bash
# Перейти в папку
cd /workspaces/damumed/ml_service

# Установить зависимости (один раз)
pip install -r requirements.txt

# Запустить сервер
python main.py
# или
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

**Проверка:**
```bash
curl http://localhost:8000/health
# Ответ: {"status":"healthy",...}
```

### Шаг 2: Запустить Spring Boot Backend

```bash
# Перейти в папку
cd /workspaces/damumed/backend

# Запустить сервер
./gradlew bootRun
# или IDE запуск (Run → Run with Spring Boot)
```

**Проверка:**
```bash
curl http://localhost:8080/api/doctors
# Ответ: список врачей (JSON array)
```

### Шаг 3: Запустить Android Frontend

```bash
# В Android Studio
# File → Open → /workspaces/damumed/frontend
# Run → Run 'app'
# (выбрать эмулятор или реальное устройство)
```

## Поток данных (Voice Assistant)

1. **Пользователь говорит** → SpeechRecognizer преобразует в текст (казахский)
2. **Текст отправляется** → Frontend (Android) → Backend (Spring Boot)
   ```
   POST http://localhost:8080/api/assistant/query
   Body: {"text": "Дәрігерге жазылу керек"}
   ```

3. **Backend обрабатывает** → AiAssistantService
   - Проверяет доступность ML сервиса
   - Вызывает: `POST http://localhost:8000/predict`
   ```
   Body: {"text": "Дәрігерге жазылу керек"}
   ```

4. **ML сервис предсказывает интент**
   ```
   Response: {
     "text": "Дәрігерге жазылу бөліміне өтудеміз.",
     "action": "NAVIGATE_TO_APPOINTMENT"
   }
   ```

5. **Backend возвращает ответ** → Frontend (Android)
   ```
   Response: {
     "text": "Дәрігерге жазылу бөліміне өтудеміз.",
     "action": "NAVIGATE_TO_APPOINTMENT"
   }
   ```

6. **Frontend обрабатывает**:
   - TextToSpeech озвучивает ответ на казахском
   - Если action == "NAVIGATE_TO_APPOINTMENT" → переход на экран записи
   - Диалог отображается в истории сообщений

## Конфигурация

### Backend (application.yml)
```yaml
ml:
  service:
    url: http://localhost:8000
```

### Frontend (RetrofitClient.kt)
```kotlin
const val BASE_URL = "http://192.168.1.100:8080"  // или localhost для эмулятора
```

## API Документация

### Spring Boot API (порт 8080)

```
GET  /api/doctors                              # Список врачей
POST /api/appointments/book                    # Запись к врачу
GET  /api/appointments/patient/{id}            # История записей
POST /api/assistant/query                      # Голосовой помощник
```

### Python ML API (порт 8000)

```
GET  /health                                   # Проверка здоровья
POST /predict                                  # Предсказание интента
     Body: {"text": "..."}
     Response: {"text": "...", "action": "..."}
POST /batch_predict                            # Пакетная обработка
GET  /docs                                     # Swagger документация
```

## Отладка

### Проверка связи ML ↔ Backend

```bash
# 1. Проверить ML сервис
curl -X POST http://localhost:8000/predict \
  -H "Content-Type: application/json" \
  -d '{"text": "жазылу"}'

# Ответ должен быть:
# {"text": "Дәрігерге жазылу бөліміне өтудеміз.", "action": "NAVIGATE_TO_APPOINTMENT"}

# 2. Проверить Backend → ML
curl -X POST http://localhost:8080/api/assistant/query \
  -H "Content-Type: application/json" \
  -d '{"text": "жазылу"}'

# Ответ должен быть такой же
```

### Логирование

- **Backend:** `backend/src/main/resources/application.yml` (уровень DEBUG для com.damumed)
- **ML сервис:** Логирование через Python logging (уровень INFO)

### Резервная логика

Если ML сервис недоступен (например, не запущен):
- Backend автоматически использует keyword-based logic
- Функция `getDefaultResponse()` в AiAssistantService
- Срабатывает при исключении RestClientException

## Безопасность в production

### Сейчас (Development):
- ✅ Валидация входных данных
- ✅ Обработка ошибок
- ❌ Аутентификация не требуется
- ❌ HTTPS не используется

### Требуется для Production:
- [ ] JWT токены аутентификация
- [ ] HTTPS/TLS шифрование
- [ ] API Rate Limiting
- [ ] CORS конфигурация
- [ ] Database шифрование
- [ ] ML сервис в приватной сети
- [ ] API Gateway (Kong, Nginx)

## Масштабирование

### Горизонтальное (Multiple Instances)

```
                    ┌─ Backend Instance 1
Load Balancer ─────┤─ Backend Instance 2
                    └─ Backend Instance 3
                           ↓
                    ┌─ ML Service (replica 1)
ML Load Balancer ──┤─ ML Service (replica 2)
                    └─ ML Service (replica 3)
```

### Вертикальное (Увеличение ресурсов)
- Backend: увеличить heap памяти JVM
- ML сервис: увеличить worker процессы в uvicorn

## Мониторинг

Рекомендуемые инструменты:
- **Logs:** ELK Stack (Elasticsearch, Logstash, Kibana)
- **Metrics:** Prometheus + Grafana
- **Tracing:** Jaeger или Zipkin
- **Health Check:** Spring Boot Actuator

## Контакты и поддержка

- Документация: `/docs/`
- Backend docs: `http://localhost:8080/api/swagger-ui.html` (если добавлен)
- ML docs: `http://localhost:8000/docs` (Swagger UI)

---

**Версия:** 1.0.0  
**Последнее обновление:** 2026-05-04
