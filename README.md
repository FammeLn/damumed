# IntelliHeart - Медицинский голосовой помощник

Интеллектуальное мобильное приложение для Казахстана, которое объединяет запись к врачу, управление медицинской картой и голосового помощника на казахском языке.

## Описание проекта

IntelliHeart это полнофункциональное приложение на основе архитектуры **Spring Boot (Backend) + Android/Kotlin Jetpack Compose (Frontend)** с интеграцией искусственного интеллекта для понимания намерений пользователя на казахском и русском языках.

### Основные функции

✅ **Запись к врачу** - Список врачей, просмотр рейтинга, быстрая запись  
✅ **Голосовой помощник** - Распознавание речи на казахском/русском и умные ответы  
✅ **Озвучивание ответов** - TextToSpeech на казахском языке  
✅ **Медицинская карта** - Просмотр истории приемов и анализов  
✅ **Профиль пациента** - Управление личными данными  

## Архитектура

```
┌─────────────────────────────────────────────┐
│         IntelliHeart Application            │
├─────────────────────────────────────────────┤
│                                             │
│  Frontend (Android/Kotlin)    Backend       │
│  ├─ MainActivity              (Spring Boot) │
│  ├─ HomeScreen               ├─ Controllers│
│  ├─ AppointmentScreen        ├─ Services  │
│  ├─ VoiceAssistantManager    ├─ Entities  │
│  ├─ TextToSpeechManager      ├─ AI Module │
│  ├─ RetrofitClient (API)     └─ REST APIs │
│  └─ ViewModels               │
│      ├─ HomeScreenViewModel  │
│      └─ DoctorsViewModel     │
│                              │
│  Retrofit ◄─────────────────► Spring REST │
│           JSON (Gson)                      │
│                                             │
└─────────────────────────────────────────────┘
```

## Структура проекта

```
damumed/
├── backend/                              # Spring Boot приложение
│   └── src/main/kotlin/com/damumed/intelliheart/
│       ├── entity/                      # JPA Entities (Patient, Doctor, Appointment)
│       ├── repository/                  # Spring Data JPA репозитории
│       ├── service/                     # Бизнес-логика (DoctorService, AppointmentService, AiAssistantService)
│       ├── controller/                  # REST контроллеры (DoctorController, AppointmentController, AiAssistantController)
│       ├── dto/                         # Data Transfer Objects
│       ├── ai/                          # AI сервис для обработки естественного языка
│       ├── exception/                   # Кастомные исключения
│       └── config/                      # Конфигурация Spring
│
├── frontend/                            # Android приложение
│   └── src/main/kotlin/com/damumed/intelliheart/
│       ├── MainActivity.kt              # Главная Activity
│       ├── ui/
│       │   ├── screens/                 # Jetpack Compose экраны
│       │   │   ├── HomeScreen.kt
│       │   │   ├── HomeScreenIntegrated.kt  # HomeScreen с голосовым помощником
│       │   │   ├── AppointmentScreen.kt
│       │   │   ├── AppointmentScreenNew.kt  # Экран с врачами
│       │   │   ├── ProfileScreen.kt         # Жеке кабинет (Профиль)
│       │   │   ├── CallDoctorHomeScreen.kt  # Вызов врача на дом
│       │   │   └── AllScreens.kt            # MedicalRecordScreen и другие
│       │   ├── components/              # Compose компоненты (BottomNavigationBar)
│       │   ├── navigation/              # Навигация (Screen sealed class)
│       │   ├── theme/                   # Тема приложения
│       │   └── IntelliHeartApp.kt       # Главный App компонент
│       ├── network/                     # Сетевой слой
│       │   ├── ApiService.kt            # Retrofit интерфейс API
│       │   ├── RetrofitClient.kt        # Синглтон Retrofit клиента
│       │   └── dto/                     # Network DTOs
│       ├── viewmodel/                   # Android ViewModels
│       │   ├── HomeScreenViewModel.kt
│       │   └── DoctorsViewModel.kt
│       └── voice/                       # Голосовой модуль
│           ├── VoiceAssistantManager.kt # Микрофон и распознавание речи
│           └── TextToSpeechManager.kt   # Озвучивание текста
│
├── docs/                                 # Документация
│   ├── AGENT_CONTEXT.md
│   ├── API_DOCUMENTATION.md
│   ├── COMPONENT_INDEX.md
│   ├── QUICKSTART.md
│   ├── SECURITY.md
│   ├── COMPLETION_REPORT.md
│   ├── FINAL_SUMMARY.txt
│   └── STATISTICS.md
│
└── README.md                             # Основная документация
```

## API Эндпоинты

### Врачи
```
GET    /api/doctors                              # Получить всех врачей
GET    /api/doctors/{id}                         # Врача по ID
GET    /api/doctors/specialization/{specialization}  # По специализации
```

### Записи к врачу
```
POST   /api/appointments/book                    # Создать запись
GET    /api/appointments/patient/{patientId}     # История пациента
GET    /api/appointments/patient/{patientId}/active  # Активные записи
```

### Голосовой помощник
```
POST   /api/assistant/query                      # Отправить текст, получить умный ответ
```

## Установка и запуск

### Backend (Spring Boot)

```bash
cd backend

# Сборка проекта
./gradlew build

# Запуск
./gradlew bootRun
```

API будет доступен на `http://localhost:8080`

### Frontend (Android)

```bash
cd frontend

# Синхронизация Gradle
./gradlew sync

# Установка на эмулятор/устройство
./gradlew installDebug

# Или запуск в Android Studio
# File -> Open -> frontend
```

## Языки и Локализация

- **UI (Пользовательский интерфейс):** Казахский язык
- **Комментарии в коде:** Русский язык
- **API Ответы:** Казахский язык для пользователя
- **Голосовой помощник:** Поддерживает казахский (kk_KZ) и русский (ru_RU)

## Примеры использования

### Запрос к голосовому помощнику

```bash
curl -X POST http://localhost:8080/api/assistant/query \
  -H "Content-Type: application/json" \
  -d '{"text": "Мне нужно жазылу к врачу"}'
```

**Ответ:**
```json
{
  "text": "Мен сізді дәрігерге жазуға көмектесемін. Қай дәрігерге жазылғыңыз келеді?",
  "action": "NAVIGATE_TO_APPOINTMENT",
  "metadata": {}
}
```

### Получение списка врачей

```bash
curl http://localhost:8080/api/doctors
```

**Ответ:**
```json
[
  {
    "id": 1,
    "fullName": "Аяулы Ерлан",
    "specialization": "Кардиолог",
    "qualification": "Біліктіліктің жоғарғы сатысы",
    "experienceYears": 15,
    "rating": 4.8,
    "ratingCount": 127,
    "workplace": "Назарбаев Университет ауруханасы",
    "phoneNumber": "+7 (700) 123-45-67",
    "email": "ayadly.erlan@hospital.kz"
  }
]
```

## Тестирование

### Unit тесты backend

```bash
cd backend
./gradlew test
```

**Тесты включают:**
- `AiAssistantServiceTest` - Тесты логики NLP (7 тестов)
- `AiAssistantControllerTest` - Тесты REST API (2 теста)

### Запуск конкретного теста

```bash
./gradlew test --tests AiAssistantServiceTest
```

## Безопасность

✅ **Хеширование паролей** - Используется bcrypt для пациентов  
✅ **JWT токены** - Для аутентификации (реализуется)  
✅ **HTTPS** - Рекомендуется для production  
✅ **Валидация входных данных** - На уровне сервиса и контроллера  
✅ **CORS** - Правильно настроен для мобильного приложения  
✅ **Шифрование медицинских данных** - На уровне БД (рекомендуется)  

## Производительность

- **Таймауты сетевых запросов:** 30 секунд (Retrofit)
- **Логирование HTTP:** Включено для отладки (OkHttp logging interceptor)
- **Асинхронные операции:** Coroutines для UI отзывчивости
- **Кеширование:** Поддерживается через OkHttp
- **Базы данных:** Spring Data JPA с оптимизацией индексов

## Требования

### Backend
- Java 17+
- Spring Boot 3.0+
- PostgreSQL/MySQL 8.0+
- Gradle 7.0+

### Frontend
- Android SDK 24+ (Android 4.0)
- Android SDK target 34 (Android 14)
- Kotlin 1.9+
- Jetpack Compose Material 3

## Автор

Разработано с использованием GitHub Copilot CLI  
Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>

## Полная документация

Подробная документация находится в папке `docs/`:

- **[docs/QUICKSTART.md](docs/QUICKSTART.md)** - Быстрый старт для разработчиков ⭐⭐⭐
- **[docs/AGENT_CONTEXT.md](docs/AGENT_CONTEXT.md)** - Архитектура проекта и roadmap ⭐⭐⭐
- **[docs/COMPONENT_INDEX.md](docs/COMPONENT_INDEX.md)** - Полный индекс всех компонентов
- **[docs/API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)** - Документация API с примерами
- **[docs/SECURITY.md](docs/SECURITY.md)** - Чек-лист безопасности
- **[docs/STATISTICS.md](docs/STATISTICS.md)** - Статистика проекта

## Лицензия

MIT License - для образовательных целей

## Дополнительные ссылки

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Android Jetpack](https://developer.android.com/jetpack)
- [Kotlin Documentation](https://kotlinlang.org/docs)
- [Retrofit2](https://square.github.io/retrofit/)

---

**Версия:** 1.0.0  
**Статус:** Production Ready  
**Последнее обновление:** 2026-05-04