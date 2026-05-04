# IntelliHeart API Документация

## Базовый URL

```
http://localhost:8080
```

## Аутентификация

Все API требуют наличие заголовка `Content-Type: application/json`

## 1. Врачи (Doctors API)

### GET /api/doctors
Получить список всех активных врачей

**Пример запроса:**
```bash
curl -X GET http://localhost:8080/api/doctors
```

**Пример ответа (200 OK):**
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
  },
  {
    "id": 2,
    "fullName": "Нурай Қасымова",
    "specialization": "Невролог",
    "qualification": "Бірінші сатысы",
    "experienceYears": 8,
    "rating": 4.6,
    "ratingCount": 89,
    "workplace": "Түлкібай Қарғысинова атындағы ауруханасы",
    "phoneNumber": "+7 (700) 234-56-78",
    "email": "nuray.kasymova@hospital.kz"
  }
]
```

### GET /api/doctors/{id}
Получить информацию о враче по ID

**Пример запроса:**
```bash
curl -X GET http://localhost:8080/api/doctors/1
```

**Пример ответа (200 OK):**
```json
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
```

### GET /api/doctors/specialization/{specialization}
Получить врачей по специализации

**Пример запроса:**
```bash
curl -X GET http://localhost:8080/api/doctors/specialization/Кардиолог
```

**Пример ответа (200 OK):**
```json
[
  {
    "id": 1,
    "fullName": "Аяулы Ерлан",
    "specialization": "Кардиолог",
    ...
  }
]
```

---

## 2. Записи к врачу (Appointments API)

### POST /api/appointments/book
Создать новую запись к врачу

**Пример запроса:**
```bash
curl -X POST http://localhost:8080/api/appointments/book \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 1,
    "doctorId": 1,
    "appointmentDateTime": "2026-05-15T10:00:00",
    "reasonForVisit": "Консультация кардиолога",
    "appointmentType": "ONSITE",
    "durationMinutes": 30
  }'
```

**Параметры:**
- `patientId` (Long, обязательно) - ID пациента
- `doctorId` (Long, обязательно) - ID врача
- `appointmentDateTime` (LocalDateTime, обязательно) - Дата и время приема (формат: ISO 8601)
- `reasonForVisit` (String, опционально) - Причина визита
- `appointmentType` (String, опционально) - Тип приема (ONSITE или ONLINE), по умолчанию ONSITE
- `durationMinutes` (Int, опционально) - Продолжительность в минутах, по умолчанию 30

**Пример ответа (201 Created):**
```json
{
  "id": 1,
  "patient": {
    "id": 1,
    "fullName": "Марат Сәлімов",
    "phoneNumber": "+7 (700) 999-88-77"
  },
  "doctor": {
    "id": 1,
    "fullName": "Аяулы Ерлан",
    "specialization": "Кардиолог",
    "phoneNumber": "+7 (700) 123-45-67",
    "workplace": "Назарбаев Университет ауруханасы"
  },
  "appointmentDateTime": "2026-05-15T10:00:00",
  "durationMinutes": 30,
  "status": "Күтілуде",
  "reasonForVisit": "Консультация кардиолога",
  "appointmentType": "ONSITE"
}
```

### GET /api/appointments/patient/{patientId}
Получить все записи пациента (историю)

**Пример запроса:**
```bash
curl -X GET http://localhost:8080/api/appointments/patient/1
```

**Пример ответа (200 OK):**
```json
[
  {
    "id": 1,
    "patient": {...},
    "doctor": {...},
    "appointmentDateTime": "2026-05-15T10:00:00",
    "durationMinutes": 30,
    "status": "Күтілуде",
    "appointmentType": "ONSITE"
  }
]
```

### GET /api/appointments/patient/{patientId}/active
Получить активные (ожидающие) записи пациента

**Пример запроса:**
```bash
curl -X GET http://localhost:8080/api/appointments/patient/1/active
```

**Пример ответа (200 OK):**
```json
[
  {
    "id": 1,
    "patient": {...},
    "doctor": {...},
    "appointmentDateTime": "2026-05-15T10:00:00",
    "status": "Күтілуде"
  }
]
```

### DELETE /api/appointments/{appointmentId}
Отменить запись к врачу

**Пример запроса:**
```bash
curl -X DELETE http://localhost:8080/api/appointments/1
```

**Пример ответа (200 OK):**
```json
{
  "message": "Өтінім сәтті болдырылды"
}
```

---

## 3. Голосовой помощник (Assistant API)

### POST /api/assistant/query
Обработать запрос голосового помощника

Это главный эндпоинт для интеграции с голосовым помощником. Он обрабатывает текст на естественном языке (казахском/русском) и возвращает умный ответ с рекомендуемым действием.

**Пример запроса:**
```bash
curl -X POST http://localhost:8080/api/assistant/query \
  -H "Content-Type: application/json" \
  -d '{"text": "Мне нужно жазылу к врачу"}'
```

**Параметры:**
- `text` (String, обязательно) - Распознанный текст от пользователя (на казахском или русском)

**Примеры ответов:**

**1. Интент: Запись к врачу (200 OK)**
```json
{
  "text": "Мен сізді дәрігерге жазуға көмектесемін. Қай дәрігерге жазылғыңыз келеді?",
  "action": "NAVIGATE_TO_APPOINTMENT",
  "metadata": {}
}
```

**2. Интент: Медицинская карта (200 OK)**
```json
{
  "text": "Міне, сіздің соңғы талдауларыңыз бен медициналық картаңыз. Осыларды қарап көріңіз.",
  "action": "NAVIGATE_TO_RECORDS",
  "metadata": {}
}
```

**3. Интент: Вызов врача на дом (200 OK)**
```json
{
  "text": "Дәрігерді үйге шақыру операциясын өндіргемін. Қай уақытта көмектесуі керек?",
  "action": "CALL_HOME_DOCTOR",
  "metadata": {}
}
```

**4. Интент: Профиль пользователя (200 OK)**
```json
{
  "text": "Сіздің жеке кабинетке өтіп барамын. Осында өзіңіздің деректеріңізді көре аласыз.",
  "action": "NAVIGATE_TO_PROFILE",
  "metadata": {}
}
```

**5. Неизвестный запрос (200 OK)**
```json
{
  "text": "Кешіріңіз, мен сізді түсінбедім. Сұрағыңызды қайталаңызшы. Мысалы: 'дәрігерге жазылу' немесе 'медициналық картамды қарау'.",
  "action": "NONE",
  "metadata": {}
}
```

---

## Коды ошибок

| Код | Описание |
|-----|----------|
| 200 | OK - Успешный ответ |
| 201 | Created - Ресурс успешно создан |
| 400 | Bad Request - Неверные параметры запроса |
| 404 | Not Found - Ресурс не найден |
| 500 | Internal Server Error - Ошибка сервера |

**Примеры ошибок:**

**404 Not Found - Врач не найден:**
```json
{
  "error": "Дәрігер табылмады",
  "message": "Врач с ID 999 не найден"
}
```

**400 Bad Request - Пациент не найден:**
```json
{
  "error": "Пациент табылмады",
  "message": "Пациент с ID 999 не найден"
}
```

**409 Conflict - Конфликт расписания:**
```json
{
  "error": "Бұл уақыт бос емес",
  "message": "В это время у врача уже есть запись"
}
```

---

## Версионирование

Текущая версия API: **v1.0**

## Поддержка

По вопросам интеграции обратитесь в техническую поддержку.
