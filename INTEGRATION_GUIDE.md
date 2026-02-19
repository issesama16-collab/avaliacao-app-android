# Guia de Integração - AvaliaApp Android com Backend

Este documento descreve como integrar o app Android com o backend tRPC do AvaliaApp.

## 📡 Configuração da API

### 1. Configurar URL Base

Edite `app/src/main/java/com/avaliacao/app/data/api/RetrofitClient.kt`:

```kotlin
package com.avaliacao.app.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://seu-backend.com/api/trpc/"
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getApiService(): ApiService = retrofit.create(ApiService::class.java)
}
```

### 2. Configurar Autenticação OAuth

O app suporta autenticação via Manus OAuth. Configure em `MainActivity.kt`:

```kotlin
// TODO: Implementar fluxo OAuth
// 1. Redirecionar para login Manus
// 2. Capturar token de retorno
// 3. Salvar em DataStore
// 4. Adicionar header Authorization em todas as requisições
```

## 🔄 Fluxo de Sincronização

### Offline-First Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   AvaliaApp Android                      │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ┌──────────────┐         ┌──────────────┐              │
│  │   UI Layer   │         │ ViewModel    │              │
│  └──────┬───────┘         └──────┬───────┘              │
│         │                        │                       │
│  ┌──────▼────────────────────────▼──────┐               │
│  │     Repository (Offline-First)       │               │
│  │  ┌────────────────────────────────┐  │               │
│  │  │  1. Check Network Status       │  │               │
│  │  │  2. Try Remote API             │  │               │
│  │  │  3. Fallback to Local DB       │  │               │
│  │  │  4. Queue for Sync             │  │               │
│  │  └────────────────────────────────┘  │               │
│  └──────┬──────────────┬──────────────┬──┘               │
│         │              │              │                  │
│    ┌────▼──┐      ┌────▼──┐      ┌───▼────┐            │
│    │ Room  │      │ Sync  │      │ Retrofit
│    │ DB    │      │ Queue │      │ API     │            │
│    └───────┘      └───────┘      └─────────┘            │
│         │                              │                 │
│         └──────────────┬───────────────┘                 │
│                        │                                 │
│         ┌──────────────▼──────────────┐                 │
│         │  Network Connectivity       │                 │
│         │  Manager                    │                 │
│         └─────────────────────────────┘                 │
│                                                           │
└─────────────────────────────────────────────────────────┘
                         │
                         │ (quando online)
                         ▼
              ┌──────────────────────┐
              │  Backend (tRPC API)  │
              │                      │
              │  - Surveys           │
              │  - Questions         │
              │  - Responses         │
              │  - Stats             │
              └──────────────────────┘
```

### Endpoints tRPC Mapeados

O app mapeia os seguintes endpoints tRPC:

#### Surveys
```
GET  /surveys                    → listSurveys()
GET  /surveys/{id}               → getSurvey(id)
GET  /surveys/slug/{slug}        → getSurveyBySlug(slug)
POST /surveys                    → createSurvey(data)
PUT  /surveys/{id}               → updateSurvey(id, data)
POST /surveys/{id}/publish       → publishSurvey(id)
POST /surveys/{id}/close         → closeSurvey(id)
```

#### Questions
```
GET  /questions?surveyId={id}    → listQuestions(surveyId)
POST /questions                  → createQuestion(data)
PUT  /questions/{id}             → updateQuestion(id, data)
POST /questions/{id}/delete      → deleteQuestion(id)
POST /questions/reorder          → reorderQuestions(data)
```

#### Responses
```
POST /responses                  → submitResponse(data)
GET  /responses?surveyId={id}    → listResponses(surveyId)
GET  /responses/stats/{id}       → getStats(surveyId)
GET  /responses/export/{id}      → exportCsv(surveyId)
```

## 🔐 Autenticação

### Token Management

```kotlin
// Em DataStore
class TokenManager(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = intPreferencesKey("user_id")
    }

    suspend fun saveToken(accessToken: String, refreshToken: String, userId: Int) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
            preferences[USER_ID] = userId
        }
    }

    fun getAccessToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }
}
```

### Adicionar Token em Requisições

```kotlin
// Em OkHttpClient interceptor
.addInterceptor { chain ->
    val token = tokenManager.getAccessToken() // Obter token
    val request = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer $token")
        .build()
    chain.proceed(request)
}
```

## 📊 Modelos de Dados

### Survey
```kotlin
data class SurveyDTO(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String?,
    val slug: String,
    val status: String,  // draft, active, closed
    val requiresAuth: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val closedAt: String?,
    val responseCount: Int = 0
)
```

### Question
```kotlin
data class QuestionDTO(
    val id: Int,
    val surveyId: Int,
    val order: Int,
    val type: String,  // text, textarea, single_choice, multiple_choice, scale, rating
    val label: String,
    val required: Boolean,
    val options: List<String>?,
    val scaleMin: Int?,
    val scaleMax: Int?,
    val scaleMinLabel: String?,
    val scaleMaxLabel: String?,
    val createdAt: String
)
```

### Response
```kotlin
data class SubmitResponseRequest(
    val slug: String,
    val respondentName: String?,
    val respondentEmail: String?,
    val answers: List<AnswerRequest>
)

data class AnswerRequest(
    val questionId: Int,
    val value: String?,      // Para texto, escala, estrelas
    val values: List<String>? // Para múltipla escolha
)
```

## 🔄 Sincronização Manual

Para forçar sincronização manual:

```kotlin
// Em SurveyRepository
suspend fun syncPending() {
    val pendingItems = database.syncQueueDao().getPending()
    
    for (item in pendingItems) {
        try {
            when (item.entityType) {
                "survey" -> syncSurvey(item)
                "question" -> syncQuestion(item)
                "response" -> syncResponse(item)
            }
            database.syncQueueDao().deleteById(item.id)
        } catch (e: Exception) {
            database.syncQueueDao().incrementRetries(item.id)
        }
    }
}
```

## 🧪 Testando Offline

### Simular Offline no Emulador

1. **Android Studio**:
   - Abra `Logcat`
   - Procure por "Emulator"
   - Clique em "Emulator" → "Extended Controls"
   - Vá para "Cellular" e desabilite dados

2. **Ou use Airplane Mode**:
   - Abra Settings no emulador
   - Ative Airplane Mode

### Verificar Dados Locais

```bash
# Acessar banco de dados local
adb shell

# Navegar para banco de dados
cd /data/data/com.avaliacao.app/databases/

# Usar sqlite3
sqlite3 avaliacao_database

# Verificar tabelas
.tables

# Consultar dados
SELECT * FROM surveys;
```

## 📈 Performance

### Otimizações Implementadas

1. **Paginação**: Implementar paginação para listas grandes
2. **Caching**: Cache de respostas por 1 hora
3. **Compressão**: GZip para requisições/respostas
4. **Lazy Loading**: Carregar dados sob demanda

### Implementar Paginação

```kotlin
// Em ApiService
@GET("surveys")
suspend fun listSurveys(
    @Query("page") page: Int = 1,
    @Query("limit") limit: Int = 20
): ApiResponse<List<SurveyDTO>>
```

## 🚨 Tratamento de Erros

```kotlin
// Em Repository
try {
    val response = apiService.getSurvey(id)
    if (response.success && response.data != null) {
        database.surveyDao().insert(response.data.toEntity())
        return response.data
    } else {
        throw Exception(response.error ?: "Erro desconhecido")
    }
} catch (e: IOException) {
    // Erro de rede - tentar local
    return database.surveyDao().getById(id)
} catch (e: Exception) {
    // Outro erro
    throw e
}
```

## 📝 Logging

Configure logging para debug:

```kotlin
// Em RetrofitClient
val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
}

okHttpClient.addInterceptor(httpLoggingInterceptor)
```

## 🔗 Recursos Adicionais

- [tRPC Documentation](https://trpc.io)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

## 📞 Suporte

Para dúvidas sobre integração, abra uma issue no repositório.
