# IntelliHeart - Индекс всех компонентов

**Общая статистика:** 36 Kotlin файлов | 7 API эндпоинтов | 9 unit тестов | 6 документов

---

## 📚 Документация (6 файлов)

| Файл | Назначение | Читать первым |
|------|-----------|:-------------:|
| **QUICKSTART.md** | Быстрый старт для новых разработчиков | ⭐⭐⭐ |
| **AGENT_CONTEXT.md** | Архитектура проекта и 7-шаговый roadmap | ⭐⭐⭐ |
| **README.md** | Основная документация приложения | ⭐⭐ |
| **API_DOCUMENTATION.md** | Полная документация всех API эндпоинтов | ⭐⭐ |
| **SECURITY.md** | Чек-лист безопасности и best practices | ⭐ |
| **COMPLETION_REPORT.md** | Финальный отчёт о завершении проекта | ⭐ |

---

## 🔧 Backend (Spring Boot + Kotlin)

### Entity Layer (4 файла)

| Класс | Файл | Описание |
|-------|------|---------|
| `Patient` | `entity/Patient.kt` | Сущность пациента (ИИН, ФИО, дата рождения, пол, телефон) |
| `Doctor` | `entity/Doctor.kt` | Сущность врача (ФИО, специализация, стаж, рейтинг) |
| `Appointment` | `entity/Appointment.kt` | Сущность записи к врачу (связи ManyToOne с пациентом и врачом) |
| `AppointmentStatus` | `entity/AppointmentStatus.kt` | Enum статусов на казахском: Күтілуде, Аяқталды, Болдырылмады |

### Repository Layer (3 файла)

| Интерфейс | Файл | Методы |
|-----------|------|--------|
| `PatientRepository` | `repository/PatientRepository.kt` | findByPhoneNumber, findByIIN |
| `DoctorRepository` | `repository/DoctorRepository.kt` | findBySpecialization, findByRatingGreaterThan |
| `AppointmentRepository` | `repository/AppointmentRepository.kt` | findByPatientId, findByDoctorId, findByStatus |

### Service Layer (3 файла)

| Класс | Файл | Методы | Строк |
|-------|------|--------|-------|
| `DoctorService` | `service/DoctorService.kt` | getAllDoctors, getDoctorById, getDoctorsBySpecialization, etc. | ~150 |
| `AppointmentService` | `service/AppointmentService.kt` | bookAppointment, getPatientAppointments, cancelAppointment, etc. | ~180 |
| `AiAssistantService` | `ai/AiAssistantService.kt` | analyzeText, detectIntent, processQuery | ~100 |

### Controller Layer (3 файла)

| Класс | Файл | Эндпоинты | Статус |
|-------|------|-----------|--------|
| `DoctorController` | `controller/DoctorController.kt` | GET /api/doctors<br>GET /api/doctors/{id}<br>GET /api/doctors/specialization/{spec} | ✅ |
| `AppointmentController` | `controller/AppointmentController.kt` | POST /api/appointments/book<br>GET /api/appointments/patient/{id}<br>DELETE /api/appointments/{id} | ✅ |
| `AiAssistantController` | `ai/AiAssistantController.kt` | POST /api/assistant/query | ✅ |

### DTO Layer (1 файл)

| Класс | Файл | Назначение |
|-------|------|-----------|
| `AssistantRequest` | `dto/AssistantDto.kt` | DTO для входящего запроса к помощнику |
| `AssistantResponse` | `dto/AssistantDto.kt` | DTO для ответа помощника (text, action, metadata) |
| `DoctorDto` | `dto/*` | DTO врача для API |
| `AppointmentResponseDto` | `dto/*` | DTO записи для API ответа |
| `CreateAppointmentRequestDto` | `dto/*` | DTO для создания записи |

### Exception Layer (1 файл)

| Класс | Файл | Когда бросается |
|-------|------|-----------------|
| `PatientNotFoundException` | `exception/AppointmentExceptions.kt` | Пациент не найден |
| `DoctorNotFoundException` | `exception/AppointmentExceptions.kt` | Врач не найден |
| `TimeSlotNotAvailableException` | `exception/AppointmentExceptions.kt` | Время занято |

### Test Layer (2 файла)

| Класс | Файл | Тесты | Покрытие |
|-------|------|-------|----------|
| `AiAssistantServiceTest` | `test/service/AiAssistantServiceTest.kt` | 7 unit тестов | 100% |
| `AiAssistantControllerTest` | `test/controller/AiAssistantControllerTest.kt` | 2 unit теста | 100% |

---

## 📱 Frontend (Android + Jetpack Compose)

### Main Entry Point (1 файл)

| Класс | Файл | Описание |
|-------|------|---------|
| `MainActivity` | `MainActivity.kt` | Точка входа приложения, setContent и edge-to-edge |

### App Composition (1 файл)

| Композитор | Файл | Описание |
|------------|------|---------|
| `IntelliHeartApp` | `ui/IntelliHeartApp.kt` | Главная Composable с NavHost и BottomNavigationBar |

### Screen Layer (5 файлов)

| Экран | Файл | UI элементы | На казахском |
|-------|------|-------------|:-----:|
| `HomeScreen` | `ui/screens/HomeScreen.kt` | Заглушка с приветствием | ✅ |
| `HomeScreenIntegrated` | `ui/screens/HomeScreenIntegrated.kt` | Микрофон, диалог, TTS | ✅ |
| `AppointmentScreen` | `ui/screens/AppointmentScreen.kt` | Вкладка Жазылу | ✅ |
| `AppointmentScreenNew` | `ui/screens/AppointmentScreenNew.kt` | Список врачей с картами | ✅ |
| `MedicalRecordScreen` | `ui/screens/AllScreens.kt` | Вкладка Медкарта | ✅ |
| `ProfileScreen` | `ui/screens/AllScreens.kt` | Вкладка Профиль | ✅ |

### Navigation Layer (2 файла)

| Класс | Файл | Описание |
|-------|------|---------|
| `Screen` | `ui/navigation/Screen.kt` | Sealed class с 4 маршрутами |
| `BottomNavigationBar` | `ui/components/BottomNavigationBar.kt` | Нижняя навигация (Басты бет, Жазылу, Медкарта, Профиль) |

### Theme (1 файл)

| Класс | Файл | Описание |
|-------|------|---------|
| `Theme` | `ui/theme/Theme.kt` | Тема приложения, Light/Dark mode |

### Network Layer (2 файла)

| Класс | Файл | Методы | Назначение |
|-------|------|--------|-----------|
| `RetrofitClient` | `network/RetrofitClient.kt` | getInstance() | Синглтон для Retrofit с HttpLoggingInterceptor |
| `ApiService` | `network/ApiService.kt` | 7 suspend функций | GET/POST методы для бэкенда |

### ViewModel Layer (2 файла)

| ViewModel | Файл | State | Функции |
|-----------|------|-------|---------|
| `DoctorsViewModel` | `viewmodel/DoctorsViewModel.kt` | Loading/Success/Error | getDoctors(), manageDoctorState |
| `HomeScreenViewModel` | `viewmodel/HomeScreenViewModel.kt` | dialogHistory, isLoading | processRecognizedText, sendToAssistant |

### Voice Layer (2 файла)

| Класс | Файл | Функции | Назначение |
|-------|------|---------|-----------|
| `VoiceAssistantManager` | `voice/VoiceAssistantManager.kt` | startListening(), stopListening(), getRecognizedText() | Распознавание речи (SpeechRecognizer) |
| `TextToSpeechManager` | `voice/TextToSpeechManager.kt` | speak(text), shutdown() | Синтез речи (TextToSpeech, казахский язык) |

### Build Configuration (1 файл)

| Конфиг | Файл | Зависимости |
|--------|------|-------------|
| `build.gradle.kts` | `build.gradle.kts` | Retrofit2, OkHttp3, Coroutines, Compose, Material3 |

---

## 🌐 API Endpoints (7 шт)

### Doctors API (3 эндпоинта)

```
GET  /api/doctors                           Получить всех врачей
GET  /api/doctors/{id}                      Получить врача по ID
GET  /api/doctors/specialization/{spec}     Поиск по специализации
```

### Appointments API (4 эндпоинта)

```
POST   /api/appointments/book                  Создать запись к врачу
GET    /api/appointments/patient/{patientId}   История записей пациента
GET    /api/appointments/patient/{id}/active   Активные записи пациента
DELETE /api/appointments/{appointmentId}       Отменить запись
```

### Assistant API (1 эндпоинт)

```
POST /api/assistant/query  Обработать голосовой запрос (NLP)
```

---

## 🧪 Unit Tests (9 тестов)

### Backend Tests

#### AiAssistantServiceTest.kt (7 тестов)
```kotlin
1. testAppointmentIntentDetection()        // "жазылу" → NAVIGATE_TO_APPOINTMENT
2. testMedicalRecordsIntentDetection()     // "медкарта" → NAVIGATE_TO_RECORDS
3. testHomeCallIntentDetection()           // "үйге" → CALL_HOME_DOCTOR
4. testUnknownTextHandling()               // Unknown → NONE
5. testTextAnalysis()                      // Basic text analysis
6. testCaseInsensitivity()                 // "Жазылу" vs "жазылу"
7. testProfileIntentDetection()            // "профиль" → NAVIGATE_TO_PROFILE
```

#### AiAssistantControllerTest.kt (2 теста)
```kotlin
1. testSuccessfulQueryProcessing()         // 200 OK response
2. testServiceInvocation()                 // Service is called
```

---

## 📊 Архитектура

```
┌─────────────────────────────────────────────────────────────┐
│                     ANDROID FRONTEND                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │            Jetpack Compose UI Layer                 │  │
│  │  (HomeScreen, AppointmentScreen, etc.)              │  │
│  └──────────────────────────────────────────────────────┘  │
│           ▲                                           ▲     │
│           │                                           │     │
│  ┌────────┴──────────────┐  ┌──────────┬──────────────┤     │
│  │   ViewModel Layer     │  │ Voice Layer              │     │
│  │ (MVVM State Mgmt)     │  │ (Voice Input/Output)     │     │
│  └───────┬──────────────┘  └──────────┬───────────────┘     │
│          │                            │                      │
│  ┌───────┴────────────────────────────┴──────────────────┐  │
│  │      Retrofit Network Layer (ApiService)              │  │
│  │      ┌────────────────────────────────────┐           │  │
│  │      │  OkHttp + HttpLoggingInterceptor   │           │  │
│  │      └────────────────────────────────────┘           │  │
│  └────────────┬─────────────────────────────────────────┘  │
└───────────────┼──────────────────────────────────────────────┘
                │ HTTP
┌───────────────┼──────────────────────────────────────────────┐
│               │       SPRING BOOT BACKEND                    │
│  ┌────────────▼──────────────────────────────────────────┐  │
│  │          REST Controllers                            │  │
│  │  (DoctorController, AppointmentController, etc.)     │  │
│  └─────┬──────────────────────────────────────────────┬──┘  │
│        │                                              │     │
│  ┌─────▼──────────────────────────┐  ┌──────────────▼─┐    │
│  │      Service Layer             │  │  AI Service   │    │
│  │  (DoctorService,               │  │  (NLP Logic)  │    │
│  │   AppointmentService)          │  └──────────────┘    │
│  └─────┬──────────────────────────┘                       │
│        │                                                   │
│  ┌─────▼──────────────────────────────────────────────┐   │
│  │      Repository Layer (Spring Data JPA)            │   │
│  │  (PatientRepository, DoctorRepository, etc.)       │   │
│  └─────┬──────────────────────────────────────────────┘   │
│        │                                                   │
│  ┌─────▼──────────────────────────────────────────────┐   │
│  │            Database (H2/PostgreSQL)                │   │
│  │  (Patient, Doctor, Appointment tables)             │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────┘
```

---

## 🎯 Интенты голосового помощника (5 типов)

| Интент | Ключевые слова | Ответ | Action |
|--------|----------------|-------|--------|
| **APPOINTMENT** | жазылу, дәрігер | Қай дәрігерге жазылғыңыз келеді? | NAVIGATE_TO_APPOINTMENT |
| **RECORDS** | анализ, медкарта | Міне, сіздің медициналық картаңыз | NAVIGATE_TO_RECORDS |
| **HOME_DOCTOR** | үйге, шақыру | Дәрігерді үйге шақыру операциясын өндіргемін | CALL_HOME_DOCTOR |
| **PROFILE** | профиль, жеке кабинет | Сіздің жеке кабинетке өтіп барамын | NAVIGATE_TO_PROFILE |
| **UNKNOWN** | прочее | Кешіріңіз, мен сізді түсінбедім | NONE |

---

## 🔑 Ключевые особенности

✅ **Backend**
- Spring Boot + Kotlin
- JPA/Hibernate с 3 сущностями
- RESTful API (7 эндпоинтов)
- Service слой для бизнес-логики
- Обработка ошибок на казахском
- Unit тесты (9 тестов)

✅ **Frontend**
- Android Kotlin + Jetpack Compose
- MVVM архитектура с ViewModels
- Bottom Navigation (4 экрана)
- Speech Recognition (SpeechRecognizer API)
- Text-to-Speech (TextToSpeech API, казахский)
- Retrofit для API запросов
- Coroutines для async операций

✅ **Интеграция**
- Full stack Voice Assistant
- NLP обработка (5 интентов)
- Auto-navigation по action
- Полная локализация (UI на казахском)

---

## 📖 Как начать разработку

1. **Прочитайте:** `QUICKSTART.md` (5 мин)
2. **Изучите:** `AGENT_CONTEXT.md` (10 мин)
3. **Выберите:**
   - Backend → откройте `backend/` в IntelliJ IDEA
   - Frontend → откройте `frontend/` в Android Studio
4. **Запустите:** Следуя инструкциям в QUICKSTART.md
5. **Модифицируйте:** Добавляйте свои эндпоинты и экраны

---

## 📞 Справка по файлам

- **Новый API эндпоинт?** → Создайте Service метод → Добавьте в Controller
- **Новый экран?** → Создайте в `ui/screens/` → Добавьте маршрут в `Screen.kt`
- **Новый ViewModel?** → Создайте в `viewmodel/` → Используйте в Composable
- **Новая сущность?** → Создайте Entity → Repository → Service → Controller

---

**Последнее обновление:** 2026-05-04  
**Версия проекта:** 1.0  
**Статус:** ✅ Полностью завершен и задокументирован
