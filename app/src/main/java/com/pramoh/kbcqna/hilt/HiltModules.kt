package com.pramoh.kbcqna.hilt

import android.content.Context
import androidx.room.Room
import com.pramoh.kbcqna.data.db.LeaderboardDB
import com.pramoh.kbcqna.data.remote.LoggingInterceptor
import com.pramoh.kbcqna.data.remote.QuestionsAPI
import com.pramoh.kbcqna.data.repository.MainRepositoryImpl
import com.pramoh.kbcqna.data.repository.SharedPrefRepositoryImpl
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.domain.repository.SharedPrefRepository
import com.pramoh.kbcqna.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModules {

    @Provides
    fun provideSharedPrefRepository(@ApplicationContext appContext: Context): SharedPrefRepository {
        return SharedPrefRepositoryImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideQuestionAPI(): QuestionsAPI {

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor())
            .build()

        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuestionsAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideLeaderboardDB(@ApplicationContext appContext: Context): LeaderboardDB {
        return Room.databaseBuilder(appContext, LeaderboardDB::class.java, "LeaderboardDB")
            .build()
    }

    @Provides
    fun provideMainRepository(questionsAPI: QuestionsAPI, leaderboardDB: LeaderboardDB): MainRepository {
        return MainRepositoryImpl(questionsAPI, leaderboardDB)
    }
}