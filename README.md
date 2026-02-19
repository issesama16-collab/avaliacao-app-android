# AvaliaApp - Android Native

Aplicativo Android nativo em Kotlin com suporte completo a offline-first, sincronização com backend e painel completo de avaliações.

## 🎯 Funcionalidades

- ✅ **Offline-First**: Funciona completamente sem internet, sincroniza quando conectado
- ✅ **Painel Completo**: Criar, editar e gerenciar avaliações
- ✅ **6 Tipos de Campo**: Texto, múltipla escolha, escala, estrelas, etc.
- ✅ **Respostas Públicas**: Interface de resposta via link único
- ✅ **Resultados com Gráficos**: Visualização de dados em tempo real
- ✅ **Exportação CSV**: Download de resultados
- ✅ **Material Design 3**: UI elegante e responsiva

## 🛠️ Stack Técnico

- **Language**: Kotlin 1.9.10
- **UI Framework**: Jetpack Compose
- **Database**: Room Database
- **Networking**: Retrofit 2 + OkHttp
- **Navigation**: Jetpack Navigation Compose
- **Dependency Injection**: Koin
- **Architecture**: MVVM + Repository Pattern

## 📋 Pré-requisitos

- Android Studio Flamingo ou superior
- Android SDK 24+ (API 24)
- Kotlin Plugin 1.9.10+
- Gradle 8.1.2+

## 🚀 Setup

### 1. Clone o repositório
```bash
cd /home/ubuntu/avaliacao-android
```

### 2. Abra no Android Studio
```bash
# Ou abra o arquivo build.gradle.kts na raiz do projeto
```

### 3. Configure o backend
Edite `app/src/main/java/com/avaliacao/app/data/api/ApiService.kt` e configure a URL base do backend:

```kotlin
// Em ApiService.kt
const val BASE_URL = "https://seu-backend.com/api/"
```

### 4. Build e Run
```bash
# Build
./gradlew build

# Run no emulador
./gradlew installDebug
```

## 📁 Estrutura do Projeto

```
app/src/main/
├── java/com/avaliacao/app/
│   ├── data/
│   │   ├── api/          # Retrofit API client
│   │   ├── db/           # Room Database + DAOs
│   │   ├── models/       # Data models
│   │   └── repository/   # Repository pattern (offline-first logic)
│   ├── ui/
│   │   ├── navigation/   # Navigation graph
│   │   ├── screens/      # Composable screens
│   │   └── theme/        # Material 3 theme
│   └── MainActivity.kt
├── res/
│   ├── values/           # Strings, colors, styles
│   └── drawable/         # Icons, images
└── AndroidManifest.xml
```

## 🔄 Sincronização Offline

O app usa um padrão **offline-first** com fila de sincronização:

1. **Operações Locais**: Todas as ações são salvas no Room Database primeiro
2. **Fila de Sincronização**: Operações pendentes são armazenadas em `SyncQueueEntity`
3. **Sincronização Automática**: Quando conectado, a fila é processada automaticamente
4. **Retry Logic**: Falhas são retentadas até 3 vezes

### Fluxo de Sincronização

```
Ação do Usuário
    ↓
Salvar Localmente (Room)
    ↓
Adicionar à Fila de Sync
    ↓
[Online?] → Sim → Enviar para Backend
    ↓ Não
Aguardar Conexão
    ↓
Sincronizar Fila
    ↓
Marcar como Sincronizado
```

## 🗄️ Banco de Dados Local

O app usa Room Database com as seguintes tabelas:

- **surveys**: Avaliações criadas
- **questions**: Perguntas das avaliações
- **responses**: Respostas dos respondentes
- **answers**: Respostas individuais de cada pergunta
- **users**: Dados do usuário autenticado
- **sync_queue**: Fila de operações pendentes

## 🔐 Autenticação

O app suporta autenticação via Manus OAuth. Configure em:

```kotlin
// server/_core/oauth.ts (backend)
// Gere credenciais OAuth e configure no backend
```

## 📊 Gráficos

O app usa bibliotecas nativas do Android para renderizar gráficos:
- BarChart para múltipla escolha
- PieChart para distribuição
- LineChart para escalas

## 📤 Exportação

Exporte resultados em CSV:
```kotlin
// Implementado em SurveyRepository.kt
suspend fun exportCsv(surveyId: Int): String
```

## 🧪 Testes

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## 🐛 Troubleshooting

### Erro: "Cannot resolve symbol 'androidx.compose'"
- Sincronize o Gradle: `./gradlew sync`
- Invalide cache: File → Invalidate Caches

### Erro: "Database version mismatch"
- Delete dados do app: Settings → Apps → AvaliaApp → Storage → Clear All Data
- Reinstale o app

### Offline não funciona
- Verifique permissões em AndroidManifest.xml
- Ative "Airplane Mode" para testar offline

## 📝 Roadmap

- [ ] Temas customizáveis (claro/escuro)
- [ ] Notificações push para novas respostas
- [ ] Biometria para autenticação
- [ ] Backup automático em nuvem
- [ ] Suporte a múltiplos idiomas

## 📄 Licença

MIT License - veja LICENSE.md

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📞 Suporte

Para suporte, abra uma issue no repositório ou entre em contato através de support@avaliacao.app

---

**Desenvolvido com ❤️ em Kotlin**
