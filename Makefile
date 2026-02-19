.PHONY: help build build-debug build-release build-optimized clean install uninstall logs

# Cores para output
BLUE := \033[0;34m
GREEN := \033[0;32m
YELLOW := \033[0;33m
RED := \033[0;31m
NC := \033[0m # No Color

help:
	@echo "$(BLUE)╔════════════════════════════════════════════════════════════╗$(NC)"
	@echo "$(BLUE)║          AvaliaApp Android - Makefile Commands              ║$(NC)"
	@echo "$(BLUE)╚════════════════════════════════════════════════════════════╝$(NC)"
	@echo ""
	@echo "$(GREEN)Build Commands:$(NC)"
	@echo "  make build              - Compilar Debug + Release (Docker)"
	@echo "  make build-debug        - Compilar apenas Debug (Docker)"
	@echo "  make build-release      - Compilar apenas Release (Docker)"
	@echo "  make build-optimized    - Compilar com multi-stage (mais rápido)"
	@echo "  make build-local        - Compilar localmente (requer Gradle)"
	@echo ""
	@echo "$(GREEN)Device Commands:$(NC)"
	@echo "  make install            - Instalar Debug APK no dispositivo"
	@echo "  make uninstall          - Desinstalar app do dispositivo"
	@echo "  make logs               - Ver logs do app"
	@echo ""
	@echo "$(GREEN)Cleanup:$(NC)"
	@echo "  make clean              - Limpar build artifacts"
	@echo "  make clean-docker       - Limpar imagens Docker"
	@echo "  make clean-all          - Limpar tudo"
	@echo ""

build:
	@echo "$(YELLOW)🔨 Compilando Debug + Release com Docker...$(NC)"
	@docker-compose up --build
	@echo "$(GREEN)✅ Build concluído!$(NC)"

build-debug:
	@echo "$(YELLOW)🔨 Compilando Debug com Docker...$(NC)"
	@docker-compose run --rm builder ./gradlew assembleDebug
	@echo "$(GREEN)✅ Debug APK pronto!$(NC)"

build-release:
	@echo "$(YELLOW)🔨 Compilando Release com Docker...$(NC)"
	@docker-compose run --rm builder ./gradlew assembleRelease
	@echo "$(GREEN)✅ Release APK pronto!$(NC)"

build-optimized:
	@echo "$(YELLOW)🔨 Compilando com multi-stage build (otimizado)...$(NC)"
	@docker build -f Dockerfile.optimized -t avaliacao-app:slim .
	@docker run --rm -v $$(pwd)/app/build/outputs:/outputs avaliacao-app:slim
	@echo "$(GREEN)✅ Build otimizado concluído!$(NC)"

build-local:
	@echo "$(YELLOW)🔨 Compilando localmente...$(NC)"
	@./gradlew assembleDebug assembleRelease
	@echo "$(GREEN)✅ Build local concluído!$(NC)"

install:
	@echo "$(YELLOW)📱 Instalando Debug APK...$(NC)"
	@adb install -r app/build/outputs/apk/debug/app-debug.apk
	@echo "$(GREEN)✅ Instalação concluída!$(NC)"

uninstall:
	@echo "$(YELLOW)📱 Desinstalando app...$(NC)"
	@adb uninstall com.avaliacao.app
	@echo "$(GREEN)✅ Desinstalação concluída!$(NC)"

logs:
	@echo "$(YELLOW)📋 Exibindo logs...$(NC)"
	@adb logcat com.avaliacao.app:V *:S

clean:
	@echo "$(YELLOW)🧹 Limpando build artifacts...$(NC)"
	@./gradlew clean
	@rm -rf app/build/
	@echo "$(GREEN)✅ Limpeza concluída!$(NC)"

clean-docker:
	@echo "$(YELLOW)🧹 Limpando imagens Docker...$(NC)"
	@docker-compose down
	@docker rmi avaliacao-app:latest avaliacao-app:slim 2>/dev/null || true
	@echo "$(GREEN)✅ Docker limpo!$(NC)"

clean-all: clean clean-docker
	@echo "$(YELLOW)🧹 Limpando tudo...$(NC)"
	@docker system prune -f
	@echo "$(GREEN)✅ Limpeza completa concluída!$(NC)"

# Targets adicionais
.PHONY: version check-docker check-gradle

version:
	@echo "$(BLUE)Versões:$(NC)"
	@docker --version
	@docker-compose --version
	@java -version 2>&1 | head -3

check-docker:
	@command -v docker >/dev/null 2>&1 || { echo "$(RED)❌ Docker não instalado$(NC)"; exit 1; }
	@echo "$(GREEN)✅ Docker instalado$(NC)"

check-gradle:
	@command -v gradle >/dev/null 2>&1 || { echo "$(RED)❌ Gradle não instalado$(NC)"; exit 1; }
	@echo "$(GREEN)✅ Gradle instalado$(NC)"
