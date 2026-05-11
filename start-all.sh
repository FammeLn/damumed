#!/bin/bash

# Скрипт для запуска всех компонентов DAMUMED
# Использует Java 21 для совместимости

set -e

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
ML_DIR="$ROOT_DIR/ml_service"
BACKEND_DIR="$ROOT_DIR/backend"

JAVA_REQUIRED_MAJOR="21"

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_VERSION_LINE="$("$JAVA_HOME/bin/java" -version 2>&1 | head -1)"
else
    JAVA_VERSION_LINE=""
fi

if [ -z "$JAVA_VERSION_LINE" ] || ! echo "$JAVA_VERSION_LINE" | grep -q "\"$JAVA_REQUIRED_MAJOR"; then
    for candidate in \
        /usr/local/sdkman/candidates/java/21.0.10-ms \
        /usr/local/sdkman/candidates/java/current \
        /home/codespace/java/21.0.10-ms \
        /home/codespace/java/current
    do
        if [ -x "$candidate/bin/java" ]; then
            JAVA_HOME="$candidate"
            break
        fi
    done
fi

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "======================================"
echo "🚀 DAMUMED - Запуск всех компонентов"
echo "======================================"
echo ""

# Проверка Java
if ! command -v java >/dev/null 2>&1; then
    echo "  ❌ Java не найдена. Установите JDK 21 и выставьте JAVA_HOME"
    exit 1
fi

JAVA_VERSION_LINE="$(java -version 2>&1 | head -1)"
if ! echo "$JAVA_VERSION_LINE" | grep -q "\"$JAVA_REQUIRED_MAJOR"; then
    echo "  ❌ Нужна Java $JAVA_REQUIRED_MAJOR. Текущая версия: $JAVA_VERSION_LINE"
    exit 1
fi

echo "✓ Java версия: $JAVA_VERSION_LINE"
echo ""

# Запуск ML Service
echo "📊 Запуск ML Service на порту 8000..."
cd "$ML_DIR"

PYTHON_BIN="python"
if [ -x "$ML_DIR/.venv/bin/python" ]; then
    PYTHON_BIN="$ML_DIR/.venv/bin/python"
fi

if ! "$PYTHON_BIN" -c "import fastapi, joblib" >/dev/null 2>&1; then
    echo "  ❌ Зависимости ML сервиса не установлены"
    echo "  ➜ Выполните: cd $ML_DIR && python -m venv .venv && source .venv/bin/activate && pip install -r requirements.txt"
    exit 1
fi

"$PYTHON_BIN" main.py > /tmp/ml_service.log 2>&1 &
ML_PID=$!
echo "  PID: $ML_PID"
sleep 2

# Проверка ML Service
if curl -s http://localhost:8000/health > /dev/null; then
    echo "  ✅ ML Service работает на http://localhost:8000"
else
    echo "  ⚠️  ML Service может быть недоступен"
fi
echo ""

# Запуск Backend
cd "$BACKEND_DIR"
echo "🔌 Запуск Backend на порту 8080..."
./gradlew bootRun > /tmp/backend.log 2>&1 &
BACKEND_PID=$!
echo "  PID: $BACKEND_PID"
sleep 5

# Проверка Backend
if curl -s http://localhost:8080/api/doctors > /dev/null 2>&1; then
    echo "  ✅ Backend работает на http://localhost:8080"
else
    echo "  ⚠️  Backend может быть недоступен"
fi
echo ""

echo "======================================"
echo "✅ Все компоненты запущены!"
echo "======================================"
echo ""
echo "Логи:"
echo "  ML Service: tail -f /tmp/ml_service.log"
echo "  Backend:    tail -f /tmp/backend.log"
echo ""
echo "API endpoints:"
echo "  Backend:     http://localhost:8080"
echo "  ML Service:  http://localhost:8000"
echo ""
echo "Android Frontend:"
echo "  Откройте Android Studio и запустите frontend"
echo "  File -> Open -> $ROOT_DIR/frontend"
echo ""
echo "Для остановки сервисов:"
echo "  kill $ML_PID $BACKEND_PID"
echo ""

# Ожидание сигнала завершения
trap "kill $ML_PID $BACKEND_PID 2>/dev/null; echo '✓ Сервисы остановлены'" EXIT
wait
