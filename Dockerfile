# Dockerfile para compilar AvaliaApp Android APK
# Build: docker build -t avaliacao-app:latest .
# Run: docker run --rm -v $(pwd)/app/build/outputs:/outputs avaliacao-app:latest

FROM ubuntu:22.04

# Definir variáveis de ambiente
ENV ANDROID_HOME=/opt/android-sdk \
    PATH=$PATH:/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools:/opt/android-sdk/tools/bin \
    JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# Instalar dependências do sistema
RUN apt-get update && apt-get install -y \
    openjdk-11-jdk \
    wget \
    unzip \
    git \
    curl \
    gradle \
    && rm -rf /var/lib/apt/lists/*

# Criar diretório do Android SDK
RUN mkdir -p $ANDROID_HOME

# Download e instalação do Android SDK
RUN cd $ANDROID_HOME && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip -q commandlinetools-linux-9477386_latest.zip && \
    rm commandlinetools-linux-9477386_latest.zip && \
    mkdir -p cmdline-tools/latest && \
    mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null || true

# Aceitar licenças do Android SDK
RUN yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses

# Instalar componentes necessários
RUN $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
    "platforms;android-34" \
    "build-tools;34.0.0" \
    "platform-tools" \
    "tools" \
    "emulator" \
    "system-images;android-34;google_apis;x86_64"

# Copiar projeto
WORKDIR /app
COPY . .

# Dar permissão de execução ao gradlew
RUN chmod +x ./gradlew

# Build debug APK
RUN ./gradlew clean assembleDebug

# Build release APK (sem assinatura)
RUN ./gradlew assembleRelease

# Copiar APKs para diretório de saída
RUN mkdir -p /outputs && \
    cp app/build/outputs/apk/debug/app-debug.apk /outputs/ && \
    cp app/build/outputs/apk/release/app-release-unsigned.apk /outputs/

# Comando padrão
CMD ["sh", "-c", "echo 'APKs gerados em /outputs' && ls -lh /outputs/"]
