#!/bin/bash

# Script de build para AvaliaApp Android
# Uso: ./build.sh [debug|release|both]

set -e

BUILD_TYPE=${1:-both}
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT_DIR="$PROJECT_DIR/app/build/outputs"

echo "╔════════════════════════════════════════════════════════════╗"
echo "║          AvaliaApp Android - Build Script                  ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado!"
    echo "Instale Docker em: https://docs.docker.com/get-docker/"
    exit 1
fi

echo "📦 Configurações:"
echo "  Tipo: $BUILD_TYPE"
echo "  Diretório: $PROJECT_DIR"
echo "  Output: $OUTPUT_DIR"
echo ""

# Criar diretório de output
mkdir -p "$OUTPUT_DIR"

case $BUILD_TYPE in
    debug)
        echo "🔨 Compilando APK Debug..."
        docker-compose -f "$PROJECT_DIR/docker-compose.yml" run --rm builder ./gradlew assembleDebug
        echo "✅ Debug APK gerado em: $OUTPUT_DIR/apk/debug/"
        ;;
    release)
        echo "🔨 Compilando APK Release..."
        docker-compose -f "$PROJECT_DIR/docker-compose.yml" run --rm builder ./gradlew assembleRelease
        echo "✅ Release APK gerado em: $OUTPUT_DIR/apk/release/"
        ;;
    both)
        echo "🔨 Compilando APK Debug e Release..."
        docker-compose -f "$PROJECT_DIR/docker-compose.yml" run --rm builder bash -c \
            "./gradlew assembleDebug && ./gradlew assembleRelease"
        echo "✅ APKs gerados em: $OUTPUT_DIR/"
        ;;
    *)
        echo "❌ Tipo de build inválido: $BUILD_TYPE"
        echo "Uso: ./build.sh [debug|release|both]"
        exit 1
        ;;
esac

echo ""
echo "📱 APKs Disponíveis:"
if [ -f "$OUTPUT_DIR/apk/debug/app-debug.apk" ]; then
    SIZE=$(du -h "$OUTPUT_DIR/apk/debug/app-debug.apk" | cut -f1)
    echo "  ✅ Debug: $OUTPUT_DIR/apk/debug/app-debug.apk ($SIZE)"
fi
if [ -f "$OUTPUT_DIR/apk/release/app-release-unsigned.apk" ]; then
    SIZE=$(du -h "$OUTPUT_DIR/apk/release/app-release-unsigned.apk" | cut -f1)
    echo "  ✅ Release: $OUTPUT_DIR/apk/release/app-release-unsigned.apk ($SIZE)"
fi

echo ""
echo "🚀 Próximos passos:"
echo "  1. Transferir APK para seu dispositivo Android"
echo "  2. Instalar: adb install app-debug.apk"
echo "  3. Ou abrir em Android Studio para debug"
echo ""
echo "✨ Build concluído com sucesso!"
