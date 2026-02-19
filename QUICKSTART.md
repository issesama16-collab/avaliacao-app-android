# 🚀 Quick Start - AvaliaApp Android

Comece a desenvolver em 5 minutos!

## 1️⃣ Pré-requisitos

- Android Studio Flamingo ou superior
- Android SDK 24+ (API 24)
- JDK 11+

## 2️⃣ Clone e Abra

```bash
cd /home/ubuntu/avaliacao-android
# Abra em Android Studio
```

## 3️⃣ Configure o Backend

Edite `app/src/main/java/com/avaliacao/app/data/api/RetrofitClient.kt`:

```kotlin
private const val BASE_URL = "https://seu-backend.com/api/trpc/"
```

## 4️⃣ Build e Run

```bash
# Build
./gradlew build

# Run no emulador
./gradlew installDebug
```

## 5️⃣ Teste Offline

1. Abra o app
2. Crie uma avaliação
3. Ative Airplane Mode no emulador
4. Veja que funciona offline
5. Desative Airplane Mode
6. Dados sincronizam automaticamente

## 📁 Arquivos Importantes

| Arquivo | Descrição |
|---------|-----------|
| `MainActivity.kt` | Ponto de entrada do app |
| `data/repository/SurveyRepository.kt` | Lógica offline-first |
| `data/db/AppDatabase.kt` | Banco de dados local |
| `ui/screens/` | Telas do app |
| `INTEGRATION_GUIDE.md` | Integração com backend |

## 🔑 Funcionalidades Principais

✅ **Offline-First**: Funciona sem internet  
✅ **Sincronização Automática**: Dados sincronizam quando conectado  
✅ **6 Tipos de Campo**: Texto, múltipla escolha, escala, estrelas  
✅ **Material Design 3**: UI elegante e moderna  
✅ **Jetpack Compose**: UI declarativa  

## 📊 Estrutura de Dados

```
Surveys (Avaliações)
├── Questions (Perguntas)
│   ├── Type: text, textarea, single_choice, multiple_choice, scale, rating
│   └── Options: Lista de opções
└── Responses (Respostas)
    └── Answers (Respostas de cada pergunta)
```

## 🔄 Fluxo Offline-First

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
Sincronizar Automaticamente
```

## 🧪 Testar Offline

### Método 1: Airplane Mode
```
Settings → Airplane Mode → ON
```

### Método 2: Desabilitar Dados
```
Emulator → Extended Controls → Cellular → OFF
```

### Método 3: Verificar Banco Local
```bash
adb shell sqlite3 /data/data/com.avaliacao.app/databases/avaliacao_database
sqlite> SELECT * FROM surveys;
```

## 🐛 Troubleshooting

### Erro: "Cannot resolve symbol"
```bash
./gradlew clean
./gradlew build
```

### Erro: "Database version mismatch"
```bash
# Limpar dados do app
adb shell pm clear com.avaliacao.app
```

### Erro: "Network error"
```
Verifique se o backend está rodando
Verifique a URL em RetrofitClient.kt
```

## 📚 Próximos Passos

1. **Implementar SurveyBuilderScreen**: Editor de avaliações
2. **Implementar SurveyRespondScreen**: Interface de resposta
3. **Implementar SurveyResultsScreen**: Painel de resultados
4. **Adicionar Autenticação**: OAuth Manus
5. **Testar Sincronização**: Offline-first completo

## 🔗 Recursos

- [README.md](./README.md) - Documentação completa
- [INTEGRATION_GUIDE.md](./INTEGRATION_GUIDE.md) - Integração com backend
- [TODO.md](./TODO.md) - Roadmap de desenvolvimento

## 💡 Dicas

- Use `Logcat` para debug
- Ative `Network Profiler` para monitorar requisições
- Use `Database Inspector` para verificar dados locais
- Teste com `Airplane Mode` frequentemente

## 📞 Suporte

Dúvidas? Abra uma issue no repositório!

---

**Pronto para começar? Abra o projeto em Android Studio e divirta-se! 🎉**
