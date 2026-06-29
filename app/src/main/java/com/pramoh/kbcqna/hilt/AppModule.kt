package com.pramoh.kbcqna.hilt

import android.content.Context
import androidx.room.Room
import com.pramoh.kbcqna.data.db.LeaderboardDB
import com.pramoh.kbcqna.data.repository.MainRepositoryImpl
import com.pramoh.kbcqna.data.repository.SharedPrefRepositoryImpl
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.domain.repository.SharedPrefRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSharedPrefRepository(@ApplicationContext appContext: Context): SharedPrefRepository {
        return SharedPrefRepositoryImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideLeaderboardDB(@ApplicationContext appContext: Context): LeaderboardDB {
        return Room.databaseBuilder(appContext, LeaderboardDB::class.java, "LeaderboardDB")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideMainRepository(
        @ApplicationContext appContext: Context,
        leaderboardDB: LeaderboardDB
    ): MainRepository {
        return MainRepositoryImpl(appContext, leaderboardDB)
    }
}