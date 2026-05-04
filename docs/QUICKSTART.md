# IntelliHeart - Быстрый старт

Это руководство поможет вам быстро разобраться в структуре проекта и начать разработку.

## 📂 Структура папок

```
backend/                      # Spring Boot API
├── src/main/kotlin/
│   └── com/damumed/intelliheart/
│       ├── entity/           # JPA сущности (Patient, Doctor, Appointment)
│       ├── repository/       # Spring Data JPA репозитории
│       ├── service/          # Бизнес-логика
│       ├── controller/       # REST контроллеры
│       ├── dto/              # Классы передачи данных
│       ├── ai/               # Голосовой помощник (NLP)
│       └── exception/        # Кастомные исключения
└── src/test/kotlin/          # Unit тесты

frontend/                     # Android Jetpack Compose
├── src/main/kotlin/
│   └── com/damumed/intelliheart/
│       ├── ui/
│       │   ├── screens/      # Экраны приложения
│       │   ├── components/   # Переиспользуемые компоненты
│       │   ├── theme/        # Тема и стили
│       │   └── navigation/   # Маршрутизация
│       ├── network/          # Retrofit сетевой слой
│       ├── viewmodel/        # MVVM ViewModels
│       ├── voice/            # Голосовой ввод/вывод
│       └── MainActivity.kt   # Точка входа приложения
├── build.gradle.kts          # Зависимости
└── AndroidManifest.xml       # Manifest

документация/
├── AGENT_CONTEXT.md          # 📋 Анализ проекта и roadmap
├── README.md                 # 📖 Основная документация
├── API_DOCUMENTATION.md      # 📡 API эндпоинты и примеры
├── SECURITY.md               # 🔒 Чек-лист безопасности
└── COMPLETION_REPORT.md      # ✅ Финальный отчёт
```

## 🚀 Быстрый старт разработки

### Вариант 1: Только Backend

```bash
# 1. Откройте папку в IDE (IntelliJ IDEA)
cd /workspaces/damumed/backend

# 2. Откройте файл build.gradle.kts
# 3. IDE автоматически синхронизирует зависимости
# 4. Создайте Gradle Run Configuration с параметром bootRun
# 5. API будет доступен на http://localhost:8080

# Для запуска тестов:
./gradlew test
```

### Вариант 2: Только Frontend

```bash
# 1. Откройте Android Studio
# 2. File → Open → выберите папку /workspaces/damumed/frontend
# 3. Подождите синхронизации Gradle
# 4. Выберите эмулятор или подключенное устройство
# 5. Нажмите Run (Shift+F10)
```

### Вариант 3: Full Stack (оба части)

```bash
# Терминал 1: Backend
cd /workspaces/damumed/backend
./gradlew bootRun
# Ждет на http://localhost:8080

# Терминал 2: Проверка Backend
curl http://localhost:8080/api/doctors

# Терминал 3: Frontend
# Откройте Android Studio и запустите фронтенд
```

## 🔑 Важные файлы для изучения

### 1️⃣ Backend разработчикам

**Стартовые точки:**
- `backend/src/main/kotlin/com/damumed/intelliheart/entity/` - Начните с сущностей
- `backend/src/main/kotlin/com/damumed/intelliheart/service/DoctorService.kt` - Логика врачей
- `backend/src/main/kotlin/com/damumed/intelliheart/ai/AiAssistantService.kt` - Логика помощника

**Добавление нового API эндпоинта:**
```kotlin
// 1. Создайте DTO в dto/
data class MyRequestDto(val field: String)

// 2. Добавьте метод в Service
fun myMethod(request: MyRequestDto): MyResponseDto { ... }

// 3. Добавьте эндпоинт в Controller
@PostMapping("/api/my-endpoint")
fun myEndpoint(@RequestBody request: MyRequestDto): ResponseEntity<MyResponseDto> { ... }

// 4. Напишите тест в test/kotlin/
```

### 2️⃣ Frontend разработчикам

**Стартовые точки:**
- `frontend/src/main/kotlin/com/damumed/intelliheart/MainActivity.kt` - Точка входа
- `frontend/src/main/kotlin/com/damumed/intelliheart/ui/IntelliHeartApp.kt` - Главная composable
- `frontend/src/main/kotlin/com/damumed/intelliheart/ui/screens/HomeScreenIntegrated.kt` - Пример интеграции

**Добавление нового экрана:**
```kotlin
// 1. Создайте composable в ui/screens/
@Composable
fun MyScreen() {
    // UI код
}

// 2. Добавьте маршрут в Screen.kt
sealed class Screen {
    object MyScreen : Screen()
}

// 3. Добавьте в NavHost в IntelliHeartApp.kt
composable<Screen.MyScreen> {
    MyScreen()
}

// 4. Добавьте вкладку в BottomNavigationBar если нужна
```

## 📱 Основные возможности

### Распознавание голоса (Speech Recognition)
```kotlin
// В frontend/src/main/kotlin/com/damumed/intelliheart/voice/VoiceAssistantManager.kt
voiceAssistantManager.startListening()
// Результат вернётся в callback
```

### Синтез речи (Text-to-Speech)
```kotlin
// В frontend/src/main/kotlin/com/damumed/intelliheart/voice/TextToSpeechManager.kt
textToSpeechManager.speak("Қайырлы күн!")
```

### API запросы
```kotlin
// В frontend/src/main/kotlin/com/damumed/intelliheart/network/ApiService.kt
val doctors = apiService.getDoctors()
val response = apiService.queryAssistant(AssistantRequest("жазылу"))
```

## 🔧 Полезные команды

### Backend
```bash
# Запуск приложения
./gradlew bootRun

# Запуск тестов
./gradlew test

# Сборка JAR
./gradlew build

# Просмотр задач
./gradlew tasks

# Очистка
./gradlew clean
```

### Frontend
```bash
# В Android Studio:
# Rebuild: Ctrl+F9
# Run: Shift+F10
# Debug: Shift+F9
# Lint: Ctrl+Alt+Shift+I
```

## 🐛 Debugging

### Backend - Логирование
```kotlin
// Используйте SLF4J
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(MyClass::class.java)
logger.info("Сообщение")
logger.error("Ошибка", exception)
```

### Frontend - Logcat
```
# В Android Studio:
# View → Tool Windows → Logcat
# Фильтруйте по "intelliheart"
# Используйте Log.d(), Log.e(), Log.i()
```

## 🌐 API примеры

### Получить список врачей
```bash
curl http://localhost:8080/api/doctors
```

### Создать запись к врачу
```bash
curl -X POST http://localhost:8080/api/appointments/book \
  -H "Content-Type: application/json" \
  -d '{"patientId": 1, "doctorId": 1, "appointmentDateTime": "2026-05-15T10:00:00"}'
```

### Отправить текст помощнику
```bash
curl -X POST http://localhost:8080/api/assistant/query \
  -H "Content-Type: application/json" \
  -d '{"text": "Дәрігерге жазылу керек"}'
```

## 📚 Документация

| Файл | Для кого | Содержание |
|------|----------|-----------|
| `AGENT_CONTEXT.md` | Всем | Архитектура и roadmap проекта |
| `README.md` | Всем | Общая информация |
| `API_DOCUMENTATION.md` | Backend + Frontend | Все API эндпоинты |
| `SECURITY.md` | Security инженеры | Чек-лист безопасности |

## ✅ Чек-лист перед commit

Перед тем как сделать коммит:

- [ ] Код соответствует Kotlin conventions
- [ ] Все комментарии на русском
- [ ] Весь UI текст на казахском
- [ ] Нет debug логирования
- [ ] Все тесты проходят
- [ ] Нет hard-coded значений (используйте константы)
- [ ] Error сообщения на казахском
- [ ] Коммит message на русском

## 🆘 Частые ошибки

### "Cannot resolve symbol 'MainActivity'"
**Решение:** Убедитесь что Android Studio синхронизировала gradle файл

### "API недоступен"
**Решение:** 
- Убедитесь что Backend запущен на `http://localhost:8080`
- Проверьте BASE_URL в `ApiService.kt`

### "Voice recognition not working"
**Решение:**
- Проверьте Permission для RECORD_AUDIO в AndroidManifest.xml
- На эмуляторе может не работать SpeechRecognizer

### "Казахский TTS не работает"
**Решение:**
- На многих устройствах TTS по умолчанию русский
- Код автоматически переходит на русский если казахский не доступен

## 🚢 Деплой

### Деплой Backend
```bash
./gradlew build
# Скопируйте build/libs/app.jar на сервер
java -jar app.jar --server.port=8080
```

### Деплой Frontend
```
# В Android Studio:
# Build → Generate Signed Bundle/APK
# Загрузите в Google Play Store
```

## 📞 Контакты

По вопросам - смотрите документацию в файлах:
- Архитектура: `AGENT_CONTEXT.md`
- API: `API_DOCUMENTATION.md`
- Безопасность: `SECURITY.md`

---

**Последнее обновление:** 2026-05-04  
**Версия:** 1.0  
**Язык:** Русский (документация), Казахский (UI)
