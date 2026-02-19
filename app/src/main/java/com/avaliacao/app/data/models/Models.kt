package com.avaliacao.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

// ─── Survey ───────────────────────────────────────────────────────────────────

@Entity(tableName = "surveys")
data class SurveyEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val description: String? = null,
    val slug: String,
    val status: String = "draft", // draft, active, closed
    val requiresAuth: Boolean = false,
    val createdAt: String,
    val updatedAt: String,
    val closedAt: String? = null,
    val isSynced: Boolean = false,
    val isLocal: Boolean = false,
)

data class SurveyDTO(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String? = null,
    val slug: String,
    val status: String,
    val requiresAuth: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val closedAt: String? = null,
    val responseCount: Int = 0,
)

// ─── Question ────────────────────────────────────────────────────────────────

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: Int,
    val surveyId: Int,
    val order: Int,
    val type: String, // text, textarea, multiple_choice, single_choice, scale, rating
    val label: String,
    val required: Boolean = false,
    val options: String? = null, // JSON array
    val scaleMin: Int? = 1,
    val scaleMax: Int? = 5,
    val scaleMinLabel: String? = null,
    val scaleMaxLabel: String? = null,
    val createdAt: String,
    val isSynced: Boolean = false,
)

data class QuestionDTO(
    val id: Int,
    val surveyId: Int,
    val order: Int,
    val type: String,
    val label: String,
    val required: Boolean,
    val options: List<String>? = null,
    val scaleMin: Int? = 1,
    val scaleMax: Int? = 5,
    val scaleMinLabel: String? = null,
    val scaleMaxLabel: String? = null,
    val createdAt: String,
)

// ─── Response ────────────────────────────────────────────────────────────────

@Entity(tableName = "responses")
data class ResponseEntity(
    @PrimaryKey val id: Int,
    val surveyId: Int,
    val respondentName: String? = null,
    val respondentEmail: String? = null,
    val submittedAt: String,
    val isSynced: Boolean = false,
    val isLocal: Boolean = false,
)

data class ResponseDTO(
    val id: Int,
    val surveyId: Int,
    val respondentName: String? = null,
    val respondentEmail: String? = null,
    val submittedAt: String,
)

// ─── Answer ──────────────────────────────────────────────────────────────────

@Entity(tableName = "answers")
data class AnswerEntity(
    @PrimaryKey val id: Int,
    val responseId: Int,
    val questionId: Int,
    val value: String? = null,
    val values: String? = null, // JSON array
    val createdAt: String,
    val isSynced: Boolean = false,
)

data class AnswerDTO(
    val id: Int,
    val responseId: Int,
    val questionId: Int,
    val value: String? = null,
    val values: List<String>? = null,
    val createdAt: String,
)

// ─── User ────────────────────────────────────────────────────────────────────

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val openId: String,
    val name: String? = null,
    val email: String? = null,
    val loginMethod: String? = null,
    val role: String = "user",
    val createdAt: String,
    val updatedAt: String,
    val lastSignedIn: String,
)

data class UserDTO(
    val id: Int,
    val openId: String,
    val name: String? = null,
    val email: String? = null,
    val loginMethod: String? = null,
    val role: String,
    val createdAt: String,
    val updatedAt: String,
    val lastSignedIn: String,
)

// ─── Sync State ───────────────────────────────────────────────────────────────

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val entityType: String, // survey, question, response, answer
    val entityId: Int,
    val operation: String, // create, update, delete
    val payload: String, // JSON
    val createdAt: String,
    val retries: Int = 0,
)

// ─── Stats ────────────────────────────────────────────────────────────────────

data class SurveyStats(
    val totalResponses: Int,
    val answersByQuestion: List<QuestionStats>,
)

data class QuestionStats(
    val question: QuestionDTO,
    val answers: List<AnswerDTO>,
)
