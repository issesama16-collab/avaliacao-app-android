package com.avaliacao.app.data.api

import com.avaliacao.app.data.models.QuestionDTO
import com.avaliacao.app.data.models.ResponseDTO
import com.avaliacao.app.data.models.SurveyDTO
import com.avaliacao.app.data.models.SurveyStats
import com.avaliacao.app.data.models.UserDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // ─── Auth ─────────────────────────────────────────────────────────────────
    @GET("auth/me")
    suspend fun getCurrentUser(): ApiResponse<UserDTO>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    // ─── Surveys ───────────────────────────────────────────────────────────────
    @GET("surveys")
    suspend fun listSurveys(): ApiResponse<List<SurveyDTO>>

    @GET("surveys/{id}")
    suspend fun getSurvey(@Path("id") id: Int): ApiResponse<SurveyDTO>

    @GET("surveys/slug/{slug}")
    suspend fun getSurveyBySlug(@Path("slug") slug: String): ApiResponse<SurveyWithQuestions>

    @POST("surveys")
    suspend fun createSurvey(@Body survey: CreateSurveyRequest): ApiResponse<SurveyDTO>

    @PUT("surveys/{id}")
    suspend fun updateSurvey(
        @Path("id") id: Int,
        @Body survey: UpdateSurveyRequest
    ): ApiResponse<SurveyDTO>

    @POST("surveys/{id}/publish")
    suspend fun publishSurvey(@Path("id") id: Int): ApiResponse<SurveyDTO>

    @POST("surveys/{id}/close")
    suspend fun closeSurvey(@Path("id") id: Int): ApiResponse<SurveyDTO>

    // ─── Questions ────────────────────────────────────────────────────────────
    @GET("questions")
    suspend fun listQuestions(@Query("surveyId") surveyId: Int): ApiResponse<List<QuestionDTO>>

    @POST("questions")
    suspend fun createQuestion(@Body question: CreateQuestionRequest): ApiResponse<List<QuestionDTO>>

    @PUT("questions/{id}")
    suspend fun updateQuestion(
        @Path("id") id: Int,
        @Body question: UpdateQuestionRequest
    ): ApiResponse<QuestionDTO>

    @POST("questions/{id}/delete")
    suspend fun deleteQuestion(@Path("id") id: Int): ApiResponse<List<QuestionDTO>>

    @POST("questions/reorder")
    suspend fun reorderQuestions(@Body request: ReorderQuestionsRequest): ApiResponse<Unit>

    // ─── Responses ────────────────────────────────────────────────────────────
    @POST("responses")
    suspend fun submitResponse(@Body response: SubmitResponseRequest): ApiResponse<ResponseDTO>

    @GET("responses")
    suspend fun listResponses(@Query("surveyId") surveyId: Int): ApiResponse<List<ResponseDTO>>

    @GET("responses/stats/{surveyId}")
    suspend fun getStats(@Path("surveyId") surveyId: Int): ApiResponse<SurveyStats>

    @GET("responses/export/{surveyId}")
    suspend fun exportCsv(@Path("surveyId") surveyId: Int): ApiResponse<ExportResponse>
}

// ─── Request/Response DTOs ────────────────────────────────────────────────────

data class ApiResponse<T>(
    val data: T? = null,
    val error: String? = null,
    val success: Boolean = true
)

data class SurveyWithQuestions(
    val survey: SurveyDTO,
    val questions: List<QuestionDTO>
)

data class CreateSurveyRequest(
    val title: String,
    val description: String? = null
)

data class UpdateSurveyRequest(
    val title: String,
    val description: String? = null
)

data class CreateQuestionRequest(
    val surveyId: Int,
    val type: String,
    val label: String,
    val required: Boolean = false,
    val options: List<String>? = null,
    val scaleMin: Int? = 1,
    val scaleMax: Int? = 5,
    val scaleMinLabel: String? = null,
    val scaleMaxLabel: String? = null
)

data class UpdateQuestionRequest(
    val surveyId: Int,
    val type: String,
    val label: String,
    val required: Boolean = false,
    val options: List<String>? = null,
    val scaleMin: Int? = 1,
    val scaleMax: Int? = 5,
    val scaleMinLabel: String? = null,
    val scaleMaxLabel: String? = null
)

data class ReorderQuestionsRequest(
    val surveyId: Int,
    val orderedIds: List<Int>
)

data class SubmitResponseRequest(
    val slug: String,
    val respondentName: String? = null,
    val respondentEmail: String? = null,
    val answers: List<AnswerRequest>
)

data class AnswerRequest(
    val questionId: Int,
    val value: String? = null,
    val values: List<String>? = null
)

data class ExportResponse(
    val csv: String,
    val filename: String
)
