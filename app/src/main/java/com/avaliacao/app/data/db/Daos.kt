package com.avaliacao.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.avaliacao.app.data.models.AnswerEntity
import com.avaliacao.app.data.models.QuestionEntity
import com.avaliacao.app.data.models.ResponseEntity
import com.avaliacao.app.data.models.SurveyEntity
import com.avaliacao.app.data.models.SyncQueueEntity
import com.avaliacao.app.data.models.UserEntity
import kotlinx.coroutines.flow.Flow

// ─── Survey DAO ───────────────────────────────────────────────────────────────

@Dao
interface SurveyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(survey: SurveyEntity)

    @Update
    suspend fun update(survey: SurveyEntity)

    @Delete
    suspend fun delete(survey: SurveyEntity)

    @Query("SELECT * FROM surveys WHERE id = :id")
    suspend fun getById(id: Int): SurveyEntity?

    @Query("SELECT * FROM surveys WHERE slug = :slug")
    suspend fun getBySlug(slug: String): SurveyEntity?

    @Query("SELECT * FROM surveys WHERE userId = :userId ORDER BY createdAt DESC")
    fun listByUser(userId: Int): Flow<List<SurveyEntity>>

    @Query("SELECT * FROM surveys WHERE status = 'active' ORDER BY createdAt DESC")
    fun listActive(): Flow<List<SurveyEntity>>

    @Query("DELETE FROM surveys WHERE id = :id")
    suspend fun deleteById(id: Int)
}

// ─── Question DAO ─────────────────────────────────────────────────────────────

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: QuestionEntity)

    @Update
    suspend fun update(question: QuestionEntity)

    @Delete
    suspend fun delete(question: QuestionEntity)

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getById(id: Int): QuestionEntity?

    @Query("SELECT * FROM questions WHERE surveyId = :surveyId ORDER BY `order` ASC")
    suspend fun listBySurvey(surveyId: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE surveyId = :surveyId ORDER BY `order` ASC")
    fun listBySurveyFlow(surveyId: Int): Flow<List<QuestionEntity>>

    @Query("DELETE FROM questions WHERE surveyId = :surveyId")
    suspend fun deleteBySurvey(surveyId: Int)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteById(id: Int)
}

// ─── Response DAO ─────────────────────────────────────────────────────────────

@Dao
interface ResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(response: ResponseEntity): Long

    @Update
    suspend fun update(response: ResponseEntity)

    @Delete
    suspend fun delete(response: ResponseEntity)

    @Query("SELECT * FROM responses WHERE id = :id")
    suspend fun getById(id: Int): ResponseEntity?

    @Query("SELECT * FROM responses WHERE surveyId = :surveyId ORDER BY submittedAt DESC")
    suspend fun listBySurvey(surveyId: Int): List<ResponseEntity>

    @Query("SELECT * FROM responses WHERE surveyId = :surveyId ORDER BY submittedAt DESC")
    fun listBySurveyFlow(surveyId: Int): Flow<List<ResponseEntity>>

    @Query("SELECT COUNT(*) FROM responses WHERE surveyId = :surveyId")
    suspend fun countBySurvey(surveyId: Int): Int

    @Query("DELETE FROM responses WHERE surveyId = :surveyId")
    suspend fun deleteBySurvey(surveyId: Int)
}

// ─── Answer DAO ───────────────────────────────────────────────────────────────

@Dao
interface AnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(answer: AnswerEntity)

    @Update
    suspend fun update(answer: AnswerEntity)

    @Delete
    suspend fun delete(answer: AnswerEntity)

    @Query("SELECT * FROM answers WHERE id = :id")
    suspend fun getById(id: Int): AnswerEntity?

    @Query("SELECT * FROM answers WHERE responseId = :responseId")
    suspend fun listByResponse(responseId: Int): List<AnswerEntity>

    @Query("SELECT * FROM answers WHERE questionId = :questionId")
    suspend fun listByQuestion(questionId: Int): List<AnswerEntity>

    @Query("DELETE FROM answers WHERE responseId = :responseId")
    suspend fun deleteByResponse(responseId: Int)

    @Query("DELETE FROM answers WHERE questionId = :questionId")
    suspend fun deleteByQuestion(questionId: Int)
}

// ─── User DAO ──────────────────────────────────────────────────────────────────

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Int): UserEntity?

    @Query("SELECT * FROM users WHERE openId = :openId")
    suspend fun getByOpenId(openId: String): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}

// ─── Sync Queue DAO ───────────────────────────────────────────────────────────

@Dao
interface SyncQueueDao {
    @Insert
    suspend fun insert(item: SyncQueueEntity): Long

    @Delete
    suspend fun delete(item: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getPending(limit: Int = 10): List<SyncQueueEntity>

    @Query("SELECT * FROM sync_queue WHERE retries < 3 ORDER BY createdAt ASC")
    suspend fun getRetryable(): List<SyncQueueEntity>

    @Query("UPDATE sync_queue SET retries = retries + 1 WHERE id = :id")
    suspend fun incrementRetries(id: Int)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM sync_queue")
    suspend fun deleteAll()
}
