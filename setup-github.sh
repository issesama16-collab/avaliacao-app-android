#!/bin/bash

# Script para configurar GitHub Actions rapidamente
# Uso: ./setup-github.sh seu_usuario seu_repositorio

set -e

if [ $# -lt 2 ]; then
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║     AvaliaApp Android - GitHub Setup Script                ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo ""
    echo "Uso: ./setup-github.sh <usuario> <repositorio>"
    echo ""
    echo "Exemplo:"
    echo "  ./setup-github.sh seu_usuario avaliacao-app-android"
    echo ""
    echo "Passos:"
    echo "  1. Crie um repositório vazio no GitHub"
    echo "  2. Copie seu usuário e nome do repositório"
    echo "  3. Execute este script"
    echo ""
    exit 1
fi

GITHUB_USER=$1
REPO_NAME=$2
REPO_URL="https://github.com/${GITHUB_USER}/${REPO_NAME}.git"

echo "╔════════════════════════════════════════════════════════════╗"
echo "║     AvaliaApp Android - GitHub Setup Script                ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
echo "📊 Configurações:"
echo "  Usuário: $GITHUB_USER"
echo "  Repositório: $REPO_NAME"
echo "  URL: $REPO_URL"
echo ""

# Verificar se Git está instalado
if ! command -v git &> /dev/null; then
    echo "❌ Git não está instalado!"
    echo "Instale em: https://git-scm.com/download"
    exit 1
fi

echo "✅ Git encontrado"
echo ""

# Inicializar Git
echo "🔧 Inicializando Git..."
git init

# Configurar remote
echo "🔗 Adicionando remote..."
git remote remove origin 2>/dev/null || true
git remote add origin "$REPO_URL"

# Configurar user (se não configurado)
if [ -z "$(git config user.email)" ]; then
    echo "📧 Configure seu email (primeira vez):"
    read -p "Email: " EMAIL
    git config --global user.email "$EMAIL"
fi

if [ -z "$(git config user.name)" ]; then
    echo "👤 Configure seu nome (primeira vez):"
    read -p "Nome: " NAME
    git config --global user.name "$NAME"
fi

# Adicionar arquivos
echo "📦 Adicionando arquivos..."
git add .

# Commit
echo "💾 Fazendo commit..."
git commit -m "Initial commit: AvaliaApp Android with GitHub Actions" || true

# Branch
echo "🌿 Configurando branch..."
git branch -M main

# Push
echo "🚀 Fazendo push para GitHub..."
echo ""
echo "⚠️  Você será solicitado a autenticar no GitHub"
echo "Use seu token de acesso pessoal ou autenticação SSH"
echo ""

git push -u origin main

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║              ✅ Setup Concluído!                           ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
echo "📋 Próximos passos:"
echo ""
echo "1. Vá para: https://github.com/$GITHUB_USER/$REPO_NAME"
echo ""
echo "2. Clique em 'Actions' (menu superior)"
echo ""
echo "3. Veja o workflow 'Build APK' rodando"
echo ""
echo "4. Aguarde ~15 minutos para conclusão"
echo ""
echo "5. Clique no workflow concluído"
echo ""
echo "6. Desça até 'Artifacts' e download:"
echo "   - app-debug (para testes)"
echo "   - app-release (para produção)"
echo ""
echo "7. Instale no seu dispositivo:"
echo "   adb install -r app-debug.apk"
echo ""
echo "✨ Pronto! GitHub Actions está compilando seu APK! 🚀"
echo ""
