# 🔨 Guia de Compilação - AvaliaApp Android

Este guia explica como compilar o APK usando Docker.

## 📋 Pré-requisitos

- **Docker** instalado ([Download](https://docs.docker.com/get-docker/))
- **Docker Compose** (geralmente vem com Docker Desktop)
- ~5GB de espaço em disco
- Conexão com internet (para download de dependências)

## 🚀 Compilação Rápida

### Opção 1: Usar Script (Recomendado)

```bash
# Clone o repositório
cd /home/ubuntu/avaliacao-android

# Compile Debug + Release
./build.sh both

# Ou apenas Debug
./build.sh debug

# Ou apenas Release
./build.sh release
```

### Opção 2: Usar Docker Compose Diretamente

```bash
cd /home/ubuntu/avaliacao-android

# Build e compile
docker-compose up --build
```

### Opção 3: Usar Docker Diretamente

```bash
cd /home/ubuntu/avaliacao-android

# Build imagem
docker build -t avaliacao-app:latest .

# Executar compilação
docker run --rm -v $(pwd)/app/build/outputs:/outputs avaliacao-app:latest
```

## 📦 Saída da Compilação

Os APKs serão gerados em:

```
app/build/outputs/
├── apk/
│   ├── debug/
│   │   └── app-debug.apk          (Para testes)
│   └── release/
│       └── app-release-unsigned.apk (Para produção)
└── bundle/
    └── release/
        └── app-release.aab        (Para Play Store)
```

## 📱 Instalar no Dispositivo

### Via ADB (Android Debug Bridge)

```bash
# Instalar Debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Ou forçar reinstalação
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Desinstalar
adb uninstall com.avaliacao.app
```

### Via Android Studio

1. Abra o projeto em Android Studio
2. Selecione `Run` → `Run 'app'`
3. Escolha o dispositivo/emulador

### Via Transferência Manual

1. Copie o APK para seu dispositivo
2. Abra o gerenciador de arquivos
3. Localize o APK e toque para instalar

## 🔧 Troubleshooting

### Erro: "Docker daemon is not running"

```bash
# Inicie o Docker
sudo systemctl start docker

# Ou no macOS
open /Applications/Docker.app
```

### Erro: "Permission denied while trying to connect to Docker daemon"

```bash
# Adicione seu usuário ao grupo docker
sudo usermod -aG docker $USER
newgrp docker
```

### Erro: "Out of disk space"

```bash
# Limpe cache do Docker
docker system prune -a

# Ou libere espaço manualmente
rm -rf app/build/
```

### Build muito lento

- Primeira compilação é lenta (download de SDK)
- Compilações subsequentes são mais rápidas (cache)
- Considere usar SSD para melhor performance

## 📊 Tempos de Compilação

| Tipo | Primeira Vez | Compilações Seguintes |
|------|---|---|
| Debug | 10-15 min | 2-3 min |
| Release | 15-20 min | 3-5 min |
| Both | 20-30 min | 5-8 min |

## 🔐 Assinatura de Release

Para assinar o APK de release:

```bash
# Gerar keystore (uma única vez)
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias avaliacao-app

# Adicionar ao gradle.properties
KEYSTORE_FILE=release.keystore
KEYSTORE_PASSWORD=sua_senha
KEY_ALIAS=avaliacao-app
KEY_PASSWORD=sua_senha

# Compilar assinado
./gradlew assembleRelease
```

## 📤 Upload para Play Store

1. **Gerar APK assinado** (veja seção acima)
2. **Criar conta Google Play**
3. **Criar aplicativo no Play Console**
4. **Upload do APK/AAB**
5. **Preencher informações da loja**
6. **Submeter para revisão**

## 🐛 Debug

### Ver logs da compilação

```bash
# Verbose output
./gradlew assembleDebug --info

# Stack trace completo
./gradlew assembleDebug --stacktrace
```

### Verificar conteúdo do APK

```bash
# Listar arquivos no APK
unzip -l app/build/outputs/apk/debug/app-debug.apk

# Extrair recursos
unzip app/build/outputs/apk/debug/app-debug.apk -d apk-contents/
```

## 📚 Recursos Adicionais

- [Gradle Build System](https://developer.android.com/build)
- [Docker Documentation](https://docs.docker.com/)
- [Android Studio Build Guide](https://developer.android.com/studio/build)
- [Play Store Publishing](https://developer.android.com/studio/publish)

## 💡 Dicas

- Use `--parallel` para compilações mais rápidas em máquinas multi-core
- Ative `gradle daemon` para builds incrementais
- Use `--build-cache` para reutilizar builds anteriores
- Considere usar `gradle wrapper` para versão consistente

## 📞 Suporte

Problemas na compilação? Abra uma issue no repositório com:
- Versão do Docker
- Versão do SO
- Mensagem de erro completa
- Saída do `docker --version`

---

**Pronto para compilar? Execute `./build.sh both` e aguarde! 🚀**
