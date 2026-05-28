# DAMUMED / IntelliHeart

Единый проект медицинского мобильного сервиса в стиле DAMUMED с голосовым помощником, записью к врачу, медкартой и интеграцией AI-интентов.

> Этот README — полное описание текущего состояния проекта, архитектуры, API, запуска и соответствия ТЗ.

## 1. О проекте

**Цель продукта:** дать пациенту быстрый доступ к медицинским сервисам через привычный UI и голосовое взаимодействие.

**Ключевые сценарии из ТЗ:**
- запись к врачу;
- вызов врача на дом;
- просмотр медицинской карты;
- голосовые запросы на русском/казахском;
- навигация по приложению через AI-помощника.

## 2. Текущее состояние реализации

Проект находится в стадии **MVP**.

### Реализовано сейчас
- Android-приложение (Jetpack Compose);
- Backend API (Spring Boot, Kotlin, JPA);
- ML microservice (FastAPI + модель интентов);
- голосовой сценарий с `SpeechRecognizer` + backend AI routing;
- уведомления внутри приложения + локальные системные уведомления;
- Swagger/OpenAPI для backend;
- базовые unit-тесты backend.

### В планах (по ТЗ, частично/не реализовано)
- iOS-клиент;
- полноценная авторизация (OTP/биометрия/семейный профиль);
- расширенные напоминания и push-центр;
- чат/поддержка, карта клиник, анализы с PDF;
- глубокая интеграция с МИС/лабораториями/платежами.

## 3. Архитектура

Проект состоит из 3 сервисов:

| Компонент | Технологии | Порт | Назначение |
| --- | --- | --- | --- |
| `frontend/` | Android, Kotlin, Jetpack Compose | — | UI, voice input, вызовы backend API |
| `backend/` | Spring Boot 3, Kotlin, JPA, PostgreSQL | `8080` | бизнес-логика, API, интеграция с ML |
| `ml_service/` | FastAPI, scikit-learn, joblib | `8000` | определение интента и action по тексту |

Поток обработки голоса:

1. Пользователь нажимает кнопку микрофона в Android.
2. `SpeechRecognizer` возвращает распознанный текст.
3. Frontend отправляет текст в `POST /api/assistant/query`.
4. Backend обращается в ML `/predict`.
5. Backend возвращает `text + action`.
6. Frontend озвучивает/показывает ответ и делает навигацию.

## 4. Структура репозитория

```text
damumed-alt-alt/
├── backend/           # Spring Boot API
├── frontend/          # Android приложение
├── ml_service/        # FastAPI AI-service
├── start-all.sh       # единый запуск ml_service + backend
├── build-backend.sh   # вспомогательный backend скрипт
└── README.md
```

## 5. Технологический стек

### Frontend
- Kotlin
- Jetpack Compose
- Retrofit + OkHttp
- Android SpeechRecognizer
- Android TextToSpeech

### Backend
- Kotlin
- Spring Boot (Web, Data JPA, Validation)
- PostgreSQL (по умолчанию)
- RestTemplate/WebClient стек для внешних вызовов
- springdoc-openapi (Swagger UI)

### ML service
- Python 3.10+
- FastAPI + Uvicorn
- scikit-learn + joblib
- Numpy

## 6. Требования к окружению

- **Java:** JDK 21 (для backend)
- **Python:** 3.10+
- **Android:** Android Studio + Android SDK (API 34)
- **OS:** Linux/macOS/Windows (с поправками на пути SDK/JDK)

## 7. Быстрый запуск

### 7.0 Docker (PostgreSQL + backend + ml_service)

```bash
docker compose up --build
```

После старта:
- backend: `http://localhost:8080`
- ml_service: `http://localhost:8000`

Frontend запускается отдельно в Android Studio (см. ниже).

### 7.1. Рекомендуемый запуск backend + ml_service

```bash
cd damumed-alt-alt
bash ./start-all.sh
```

Скрипт:
- проверяет Java 21;
- запускает `ml_service` на `8000`;
- запускает backend на `8080`;
- пишет логи в `/tmp/ml_service.log` и `/tmp/backend.log`.

Остановка:

```bash
kill <ML_PID> <BACKEND_PID>
```

### 7.2. Если `start-all.sh` ругается на зависимости ML

```bash
cd ml_service
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

### 7.3. Ручной запуск по частям

**ML service**

```bash
cd ml_service
source .venv/bin/activate
python main.py
```

**Backend**

```bash
cd backend
./gradlew bootRun
```

**Frontend**

Открыть `frontend/` в Android Studio и запустить `debug` сборку.

## 8. Конфигурация

### Backend -> ML URL

По умолчанию backend ожидает ML по `http://localhost:8000`.

- Файл: `backend/src/main/resources/application.yml`
- Параметр: `ml.service.url`
- Переменная окружения: `ML_SERVICE_URL`

### ML Service -> LLM (опционально)

LLM слой подключается в `ml_service` при наличии ключа и модели. Если LLM не настроен, сервис использует текущую ML‑классификацию интентов.

- `LLM_ENABLED` — `true/false` (по умолчанию `false`)
- `LLM_API_URL` — base URL (по умолчанию `https://api.openai.com/v1`)
- `LLM_API_KEY` — API ключ
- `LLM_MODEL` — имя модели (например, `gpt-4o-mini`)

### PostgreSQL (backend)

Backend теперь работает с PostgreSQL по умолчанию.

- `POSTGRES_URL` — JDBC URL, по умолчанию `jdbc:postgresql://localhost:5432/intelliheart`
- `POSTGRES_USER` — пользователь БД (по умолчанию `postgres`)
- `POSTGRES_PASSWORD` — пароль БД (по умолчанию `postgres`)

### Telegram авторизация (backend)

Задаются через переменные окружения:

- `TELEGRAM_BOT_TOKEN` — токен бота (обязательно для работы auth flow)
- `TELEGRAM_BOT_USERNAME` — username бота (по умолчанию `intelliheart_auth_bot`)
- `TELEGRAM_AUTH_EXPIRATION_MINUTES` — TTL заявки авторизации (по умолчанию `10`)
- `TELEGRAM_SESSION_TTL_HOURS` — TTL выданной сессии (по умолчанию `168`)

### Frontend -> Backend URL

Файл: `frontend/src/main/kotlin/com/damumed/intelliheart/network/RetrofitClient.kt`

По умолчанию:
- `http://10.0.2.2:8080/` (Android Emulator).

Для реального устройства:
- укажите IP машины, где запущен backend;
- либо используйте `adb reverse tcp:8080 tcp:8080`.

## 9. API документация (Swagger/OpenAPI)

После старта backend:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 10. Основные API endpoint'ы

### 10.1 Врачи

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| POST | `/api/doctors` | создать врача |
| GET | `/api/doctors` | список активных врачей |
| GET | `/api/doctors/{id}` | врач по id |
| GET | `/api/doctors/specialization/{specialization}` | врачи по специализации |
| GET | `/api/doctors/sorted` | врачи по рейтингу |
| GET | `/api/doctors/search?query=...` | поиск по имени |

Пример создания врача:

```bash
curl -X POST http://localhost:8080/api/doctors \
  -H "Content-Type: application/json" \
  -d '{
    "fullName":"Айдын Серікұлы",
    "specialization":"Кардиолог",
    "qualification":"Жоғары санат",
    "experienceYears":12,
    "licenseNumber":"KZ-CRD-001",
    "phoneNumber":"+77010000000",
    "email":"aidyn.cardiolog@clinic.kz",
    "workplace":"Damumed Clinic"
  }'
```

Примечание: при первом старте, если таблица врачей пустая, backend добавляет одного демо-врача.

### 10.2 Записи (appointments)

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| POST | `/api/appointments/book` | создать запись |
| GET | `/api/appointments/patient/{patientId}` | история записей пациента |
| GET | `/api/appointments/patient/{patientId}/active` | активные записи |
| GET | `/api/appointments/{appointmentId}` | запись по id |
| DELETE | `/api/appointments/{appointmentId}` | отмена записи |

### 10.3 Голосовой помощник

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| POST | `/api/assistant/query` | обработать голосовой/текстовый запрос |

Пример:

```bash
curl -X POST http://localhost:8080/api/assistant/query \
  -H "Content-Type: application/json" \
  -d '{"text":"Мне нужно жазылу к врачу"}'
```

### 10.4 Telegram авторизация по подтверждению номера

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| POST | `/api/auth/telegram/start` | старт авторизации, возвращает deep-link на бота |
| GET | `/api/auth/telegram/status/{authRequestId}` | статус подтверждения номера и access token |
| POST | `/api/telegram/webhook` | webhook для Telegram update payload |

Пример старта:

```bash
curl -X POST http://localhost:8080/api/auth/telegram/start \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber":"+77011234567"}'
```

Дальше flow:

1. Приложение получает `botLink`.
2. Пользователь открывает бота по ссылке.
3. Бот просит нажать кнопку `Поделиться номером`.
4. После подтверждения бот валидирует номер и backend помечает auth как `VERIFIED`.
5. Приложение опрашивает `/status/{authRequestId}` и получает `accessToken`.

### 10.5 Email + пароль (простая авторизация)

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| POST | `/api/auth/register` | регистрация по email/паролю |
| POST | `/api/auth/login` | вход по email/паролю |

Пример регистрации:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret"}'
```

### 10.6 Пациенты

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| POST | `/api/patients` | создать профиль пациента |
| GET | `/api/patients/{id}` | получить пациента по id |

Пример создания пациента:

```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"iin":"990612345678","fullName":"Марат Сәлімов","dateOfBirth":"1999-06-12","gender":"Ер","phoneNumber":"+77009998877","address":"Алматы, Бостандыкский р-н","medicalHistory":"—"}'
```

### 10.7 Уведомления

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| GET | `/api/notifications?userId=...` | список уведомлений пользователя |
| POST | `/api/notifications` | создать уведомление |

Пример создания уведомления:

```bash
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"title":"IntelliHeart","message":"Тесттік хабарлама","userId":1}'
```

## 11. ML сервис и интенты

ML сервис:
- загружает `intent_model.pkl` и `vectorizer.pkl`;
- принимает текст в `/predict`;
- возвращает действие (`action`) и текст ответа (`text`).

Ключевые действия:
- `NAVIGATE_TO_APPOINTMENT`
- `NAVIGATE_TO_RECORDS`
- `CALL_DOCTOR`
- `NAVIGATE_TO_PROFILE`
- `NONE`

Если ML недоступен, backend использует fallback-логику по ключевым словам.

## 12. Голосовой модуль: важные детали

В Android-приложении реализовано:
- runtime-запрос разрешения `RECORD_AUDIO`;
- `SpeechRecognizer` + `RecognitionListener`;
- обработка ошибок распознавания с понятными сообщениями.

### Ограничения эмуляторов (включая BlueStacks)

Если показывается сообщение, что голосовое распознавание недоступно:
- в эмуляторе отсутствует/сломана системная speech-служба;
- проверьте `Google app` и `Speech Services by Google`;
- проверьте доступ к микрофону в настройках Android;
- для стабильной проверки лучше использовать Android Emulator (Google APIs) или реальное устройство.

## 13. Соответствие ТЗ (кратко)

ТЗ покрывает значительно более широкий продукт (iOS + Android, семейный аккаунт, OTP, чат, анализы, push-центр, интеграции с МИС и т.д.).

Текущий MVP покрывает ядро:
- базовый мобильный клиент (Android);
- запись/вызов/базовые медицинские разделы;
- голосовой помощник;
- backend API;
- отдельный ML сервис интентов.

## 14. Тестирование

### Backend

```bash
cd backend
./gradlew test
```

Покрыты тестами:
- `AiAssistantService`
- `AiAssistantController`
- создание врача (`DoctorService`, `DoctorController`)

### Frontend

```bash
cd frontend
./gradlew test
```

## 15. Безопасность и ограничения MVP

- Используется PostgreSQL без production-настроек и hardening.
- Нет полноценной production-аутентификации/авторизации.
- Голосовой помощник не ставит диагнозы и не заменяет врача.
- Для production нужны: TLS, аудит, роли, согласия, маскирование логов, интеграция с медрегуляторными требованиями.

## 16. Roadmap (следующие шаги)

1. Перевод БД на PostgreSQL + миграции.
2. Полный auth (OTP/JWT/биометрия).
3. Расширение сценариев NLP и многошаговые диалоги.
4. Push-уведомления и напоминания.
5. Интеграции с внешними МИС/лабораториями.
6. iOS клиент.
7. Нагрузочные и security тесты.

---

**Статус:** MVP  
**Версия:** 1.1.0  
**Языки интерфейса:** Казахский, Русский
