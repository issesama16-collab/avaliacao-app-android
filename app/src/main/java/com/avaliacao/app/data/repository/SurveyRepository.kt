package com.avaliacao.app.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.avaliacao.app.data.api.ApiService
import com.avaliacao.app.data.api.CreateQuestionRequest
import com.avaliacao.app.data.api.CreateSurveyRequest
import com.avaliacao.app.data.api.ReorderQuestionsRequest
import com.avaliacao.app.data.api.SubmitResponseRequest
import com.avaliacao.app.data.api.UpdateQuestionRequest
import com.avaliacao.app.data.api.UpdateSurveyRequest
import com.avaliacao.app.data.db.AppDatabase
import com.avaliacao.app.data.models.QuestionEntity
import com.avaliacao.app.data.models.ResponseEntity
import com.avaliacao.app.data.models.SurveyEntity
import com.avaliacao.app.data.models.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SurveyRepository(
    private val context: Context,
    private val apiService: ApiService,
    private val database: AppDatabase
) {
    private val gson = Gson()
    private val dateFormatter = DateTimeFormatter.ISO_DATE_TIME

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // ─── Surveys ───────────────────────────────────────────────────────────────

    fun listUserSurveys(userId: Int): Flow<List<SurveyEntity>> {
        return database.surveyDao().listByUser(userId)
    }

    suspend fun getSurvey(id: Int): SurveyEntity? {
        return try {
            if (isOnline()) {
                val remote = apiService.getSurvey(id).data ?: return null
                val entity = remote.toEntity()
                database.surveyDao().insert(entity)
                entity
            } else {
                database.surveyDao().getById(id)
            }
        } catch (e: Exception) {
            database.surveyDao().getById(id)
        }
    }

    suspend fun createSurvey(title: String, description: String?): SurveyEntity? {
        val now = LocalDateTime.now().format(dateFormatter)
        val slug = title.lowercase().replace(" ", "-").take(50)

        return if (isOnline()) {
            try {
                val response = apiService.createSurvey(CreateSurveyRequest(title, description))
                val entity = response.data?.toEntity() ?: return null
                database.surveyDao().insert(entity)
                entity
            } catch (e: Exception) {
                // Fallback: create locally
                createLocalSurvey(title, description, slug, now)
            }
        } else {
            createLocalSurvey(title, description, slug, now)
        }
    }

    private suspend fun createLocalSurvey(
        title: String,
        description: String?,
        slug: String,
        now: String
    ): SurveyEntity {
        val entity = SurveyEntity(
            id = System.currentTimeMillis().toInt(),
            userId = 1, // TODO: get from auth
            title = title,
            description = description,
            slug = slug,
            status = "draft",
            createdAt = now,
            updatedAt = now,
            isLocal = true,
            isSynced = false
        )
        database.surveyDao().insert(entity)
        database.syncQueueDao().insert(
            SyncQueueEntity(
                entityType = "survey",
                entityId = entity.id,
                operation = "create",
                payload = gson.toJson(entity),
                createdAt = now
            )
        )
        return entity
    }

    suspend fun updateSurvey(id: Int, title: String, description: String?): SurveyEntity? {
        val survey = database.surveyDao().getById(id) ?: return null
        val now = LocalDateTime.now().format(dateFormatter)
        val updated = survey.copy(title = title, description = description, updatedAt = now)

        return if (isOnline()) {
            try {
                apiService.updateSurvey(id, UpdateSurveyRequest(title, description))
                database.surveyDao().update(updated)
                updated
            } catch (e: Exception) {
                database.surveyDao().update(updated)
                updated
            }
        } else {
            database.surveyDao().update(updated)
            database.syncQueueDao().insert(
                SyncQueueEntity(
                    entityType = "survey",
                    entityId = id,
                    operation = "update",
                    payload = gson.toJson(updated),
                    createdAt = now
                )
            )
            updated
        }
    }

    suspend fun publishSurvey(id: Int): SurveyEntity? {
        val survey = database.surveyDao().getById(id) ?: return null
        val published = survey.copy(status = "active")

        return if (isOnline()) {
            try {
                apiService.publishSurvey(id)
                database.surveyDao().update(published)
                published
            } catch (e: Exception) {
                null
            }
        } else {
            database.surveyDao().update(published)
            published
        }
    }

    // ─── Questions ────────────────────────────────────────────────────────────

    fun listQuestions(surveyId: Int): Flow<List<QuestionEntity>> {
        return database.questionDao().listBySurveyFlow(surveyId)
    }

    suspend fun addQuestion(
        surveyId: Int,
        type: String,
        label: String,
        required: Boolean = false,
        options: List<String>? = null,
        scaleMin: Int? = 1,
        scaleMax: Int? = 5,
        scaleMinLabel: String? = null,
        scaleMaxLabel: String? = null
    ): QuestionEntity? {
        val questions = database.questionDao().listBySurvey(surveyId)
        val order = questions.size
        val now = LocalDateTime.now().format(dateFormatter)

        return if (isOnline()) {
            try {
                val response = apiService.createQuestion(
                    CreateQuestionRequest(
                        surveyId, type, label, required, options,
                        scaleMin, scaleMax, scaleMinLabel, scaleMaxLabel
                    )
                )
                response.data?.forEach { database.questionDao().insert(it.toEntity()) }
                response.data?.lastOrNull()?.toEntity()
            } catch (e: Exception) {
                createLocalQuestion(
                    surveyId, type, label, required, options,
                    scaleMin, scaleMax, scaleMinLabel, scaleMaxLabel, order, now
                )
            }
        } else {
            createLocalQuestion(
                surveyId, type, label, required, options,
                scaleMin, scaleMax, scaleMinLabel, scaleMaxLabel, order, now
            )
        }
    }

    private suspend fun createLocalQuestion(
        surveyId: Int,
        type: String,
        label: String,
        required: Boolean,
        options: List<String>?,
        scaleMin: Int?,
        scaleMax: Int?,
        scaleMinLabel: String?,
        scaleMaxLabel: String?,
        order: Int,
        now: String
    ): QuestionEntity {
        val entity = QuestionEntity(
            id = System.currentTimeMillis().toInt(),
            surveyId = surveyId,
            order = order,
            type = type,
            label = label,
            required = required,
            options = options?.let { gson.toJson(it) },
            scaleMin = scaleMin,
            scaleMax = scaleMax,
            scaleMinLabel = scaleMinLabel,
            scaleMaxLabel = scaleMaxLabel,
            createdAt = now,
            isSynced = false
        )
        database.questionDao().insert(entity)
        database.syncQueueDao().insert(
            SyncQueueEntity(
                entityType = "question",
                entityId = entity.id,
                operation = "create",
                payload = gson.toJson(entity),
                createdAt = now
            )
        )
        return entity
    }

    suspend fun updateQuestion(
        id: Int,
        surveyId: Int,
        type: String,
        label: String,
        required: Boolean = false,
        options: List<String>? = null,
        scaleMin: Int? = 1,
        scaleMax: Int? = 5,
        scaleMinLabel: String? = null,
        scaleMaxLabel: String? = null
    ): QuestionEntity? {
        val question = database.questionDao().getById(id) ?: return null
        val now = LocalDateTime.now().format(dateFormatter)
        val updated = question.copy(
            type = type,
            label = label,
            required = required,
            options = options?.let { gson.toJson(it) },
            scaleMin = scaleMin,
            scaleMax = scaleMax,
            scaleMinLabel = scaleMinLabel,
            scaleMaxLabel = scaleMaxLabel
        )

        if (isOnline()) {
            try {
                apiService.updateQuestion(
                    id,
                    UpdateQuestionRequest(
                        surveyId, type, label, required, options,
                        scaleMin, scaleMax, scaleMinLabel, scaleMaxLabel
                    )
                )
            } catch (e: Exception) {
                // Continue with local update
            }
        }

        database.questionDao().update(updated)
        return updated
    }

    suspend fun deleteQuestion(id: Int, surveyId: Int) {
        if (isOnline()) {
            try {
                apiService.deleteQuestion(id)
            } catch (e: Exception) {
                // Continue with local delete
            }
        }
        database.questionDao().deleteById(id)
    }

    suspend fun reorderQuestions(surveyId: Int, orderedIds: List<Int>) {
        if (isOnline()) {
            try {
                apiService.reorderQuestions(ReorderQuestionsRequest(surveyId, orderedIds))
            } catch (e: Exception) {
                // Continue with local reorder
            }
        }

        val questions = database.questionDao().listBySurvey(surveyId)
        orderedIds.forEachIndexed { index, questionId ->
            val question = questions.find { it.id == questionId }
            if (question != null) {
                database.questionDao().update(question.copy(order = index))
            }
        }
    }

    // ─── Responses ────────────────────────────────────────────────────────────

    suspend fun submitResponse(
        slug: String,
        respondentName: String?,
        respondentEmail: String?,
        answers: List<Pair<Int, Any?>>
    ): Boolean {
        val now = LocalDateTime.now().format(dateFormatter)

        return if (isOnline()) {
            try {
                val answerRequests = answers.map { (qId, value) ->
                    when (value) {
                        is List<*> -> com.avaliacao.app.data.api.AnswerRequest(
                            qId, null, value.map { it.toString() }
                        )
                        else -> com.avaliacao.app.data.api.AnswerRequest(
                            qId, value?.toString(), null
                        )
                    }
                }
                apiService.submitResponse(
                    SubmitResponseRequest(slug, respondentName, respondentEmail, answerRequests)
                )
                true
            } catch (e: Exception) {
                // Save locally for sync
                saveResponseLocally(slug, respondentName, respondentEmail, answers, now)
                true
            }
        } else {
            saveResponseLocally(slug, respondentName, respondentEmail, answers, now)
            true
        }
    }

    private suspend fun saveResponseLocally(
        slug: String,
        respondentName: String?,
        respondentEmail: String?,
        answers: List<Pair<Int, Any?>>,
        now: String
    ) {
        val survey = database.surveyDao().getBySlug(slug) ?: return
        val responseEntity = ResponseEntity(
            id = System.currentTimeMillis().toInt(),
            surveyId = survey.id,
            respondentName = respondentName,
            respondentEmail = respondentEmail,
            submittedAt = now,
            isLocal = true,
            isSynced = false
        )
        val responseId = database.responseDao().insert(responseEntity).toInt()

        answers.forEach { (qId, value) ->
            val answerEntity = com.avaliacao.app.data.models.AnswerEntity(
                id = System.currentTimeMillis().toInt() + kotlin.random.Random.nextInt(),
                responseId = responseId,
                questionId = qId,
                value = if (value is List<*>) null else value?.toString(),
                values = if (value is List<*>) gson.toJson(value) else null,
                createdAt = now,
                isSynced = false
            )
            database.answerDao().insert(answerEntity)
        }
    }

    suspend fun getSurveyStats(surveyId: Int): com.avaliacao.app.data.models.SurveyStats? {
        return if (isOnline()) {
            try {
                apiService.getStats(surveyId).data
            } catch (e: Exception) {
                getLocalStats(surveyId)
            }
        } else {
            getLocalStats(surveyId)
        }
    }

    private suspend fun getLocalStats(surveyId: Int): com.avaliacao.app.data.models.SurveyStats? {
        val questions = database.questionDao().listBySurvey(surveyId)
        val responses = database.responseDao().listBySurvey(surveyId)

        val answersByQuestion = questions.map { q ->
            val answers = responses.flatMap { r ->
                database.answerDao().listByResponse(r.id)
                    .filter { it.questionId == q.id }
            }
            com.avaliacao.app.data.models.QuestionStats(
                q.toDTO(),
                answers.map { it.toDTO() }
            )
        }

        return com.avaliacao.app.data.models.SurveyStats(
            totalResponses = responses.size,
            answersByQuestion = answersByQuestion
        )
    }
}

// ─── Extension Functions ───────────────────────────────────────────────────────

private fun com.avaliacao.app.data.models.SurveyDTO.toEntity() = SurveyEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    slug = slug,
    status = status,
    requiresAuth = requiresAuth,
    createdAt = createdAt,
    updatedAt = updatedAt,
    closedAt = closedAt,
    isSynced = true,
    isLocal = false
)

private fun com.avaliacao.app.data.models.QuestionDTO.toEntity() = QuestionEntity(
    id = id,
    surveyId = surveyId,
    order = order,
    type = type,
    label = label,
    required = required,
    options = options?.let { Gson().toJson(it) },
    scaleMin = scaleMin,
    scaleMax = scaleMax,
    scaleMinLabel = scaleMinLabel,
    scaleMaxLabel = scaleMaxLabel,
    createdAt = createdAt,
    isSynced = true
)

private fun SurveyEntity.toDTO() = com.avaliacao.app.data.models.SurveyDTO(
    id = id,
    userId = userId,
    title = title,
    description = description,
    slug = slug,
    status = status,
    requiresAuth = requiresAuth,
    createdAt = createdAt,
    updatedAt = updatedAt,
    closedAt = closedAt
)

private fun QuestionEntity.toDTO() = com.avaliacao.app.data.models.QuestionDTO(
    id = id,
    surveyId = surveyId,
    order = order,
    type = type,
    label = label,
    required = required,
    options = options?.let { Gson().fromJson(it, List::class.java) as? List<String> },
    scaleMin = scaleMin,
    scaleMax = scaleMax,
    scaleMinLabel = scaleMinLabel,
    scaleMaxLabel = scaleMaxLabel,
    createdAt = createdAt
)

private fun com.avaliacao.app.data.models.AnswerEntity.toDTO() = com.avaliacao.app.data.models.AnswerDTO(
    id = id,
    responseId = responseId,
    questionId = questionId,
    value = value,
    values = values?.let { Gson().fromJson(it, List::class.java) as? List<String> },
    createdAt = createdAt
)
