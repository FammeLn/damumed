# DAMUMED / IntelliHeart — медицинский голосовой помощник

Интеллектуальное мобильное приложение для пациентов и клиник: запись к врачу, медкарта, напоминания и голосовые запросы на казахском и русском языках. Проект состоит из трёх частей: **Backend (Spring Boot)**, **ML microservice (FastAPI)** и **Android Frontend (Jetpack Compose)**.

## Состав и связь сервисов

| Компонент | Технологии | Порт | Куда ходит |
| --- | --- | --- | --- |
| Backend | Spring Boot, Kotlin, JPA | `8080` | В ML service (`/predict`) |
| ML service | FastAPI, scikit-learn | `8000` | — |
| Frontend | Android, Kotlin, Compose | — | В Backend (`/api/*`) |

## Требования

**Backend**
- JDK 21
- Gradle Wrapper (в репозитории)

**ML service**
- Python 3.10+
- pip

**Frontend**
- Android Studio + Android SDK (platform 34)
- Эмулятор или физическое устройство

## Быстрый старт

### 1) ML service (FastAPI)

```bash
cd ml_service
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python main.py
```

Проверка:

```bash
curl http://localhost:8000/health
```

### 2) Backend (Spring Boot)

По умолчанию сервис ждёт ML по адресу `http://localhost:8000`. Меняется через:
- `backend/src/main/resources/application.yml` (`ml.service.url`), или
- переменную окружения `ML_SERVICE_URL`.

```bash
cd backend
./gradlew bootRun
```

Проверка:

```bash
curl http://localhost:8080/api/doctors
```

### 3) Frontend (Android)

Откройте проект в Android Studio:

```
File -> Open -> <repo>/frontend
```

или через Gradle:

```bash
cd frontend
./gradlew installDebug
```

Если Android SDK не настроен, создайте `frontend/local.properties` (не коммитится):

```
sdk.dir=/path/to/Android/Sdk
```

**Базовый URL Backend** находится в:

```
frontend/src/main/kotlin/com/damumed/intelliheart/network/RetrofitClient.kt
```

По умолчанию используется `http://10.0.2.2:8080` (Android эмулятор).  
Для физического устройства укажите IP хоста, например `http://192.168.1.10:8080`,  
или используйте `adb reverse tcp:8080 tcp:8080`.

## Скрипт единого запуска

Скрипт запускает **ML service + Backend** и проверяет их доступность:

```bash
./start-all.sh
```

Frontend запускается отдельно в Android Studio.

## Основные API эндпоинты

### Врачи
```
GET    /api/doctors
GET    /api/doctors/{id}
GET    /api/doctors/specialization/{specialization}
GET    /api/doctors/sorted
GET    /api/doctors/search?query=...
```

### Записи
```
POST   /api/appointments/book
GET    /api/appointments/patient/{patientId}
GET    /api/appointments/patient/{patientId}/active
```

### Голосовой помощник
```
POST   /api/assistant/query
```

Пример:

```bash
curl -X POST http://localhost:8080/api/assistant/query \
  -H "Content-Type: application/json" \
  -d '{"text":"Мне нужно жазылу к врачу"}'
```

## Модели ML

Файлы модели находятся в `ml_service/`:
- `intent_model.pkl`
- `vectorizer.pkl`

Сервис читает их при старте и использует для классификации интентов.

## Тесты

```bash
cd backend
./gradlew test
```

---

**Статус:** MVP  
**Версия:** 1.0.0
