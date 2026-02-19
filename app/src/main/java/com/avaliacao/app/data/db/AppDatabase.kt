package com.avaliacao.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.avaliacao.app.data.models.AnswerEntity
import com.avaliacao.app.data.models.QuestionEntity
import com.avaliacao.app.data.models.ResponseEntity
import com.avaliacao.app.data.models.SurveyEntity
import com.avaliacao.app.data.models.SyncQueueEntity
import com.avaliacao.app.data.models.UserEntity

@Database(
    entities = [
        SurveyEntity::class,
        QuestionEntity::class,
        ResponseEntity::class,
        AnswerEntity::class,
        UserEntity::class,
        SyncQueueEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun surveyDao(): SurveyDao
    abstract fun questionDao(): QuestionDao
    abstract fun responseDao(): ResponseDao
    abstract fun answerDao(): AnswerDao
    abstract fun userDao(): UserDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "avaliacao_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
