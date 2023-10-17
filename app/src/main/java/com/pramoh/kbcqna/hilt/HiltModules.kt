package com.pramoh.kbcqna.hilt

import android.content.Context
import com.pramoh.kbcqna.data.repository.MainRepositoryImpl
import com.pramoh.kbcqna.data.repository.SharedPrefRepositoryImpl
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.domain.repository.SharedPrefRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HiltModules {

    @Provides
    fun provideSharedPrefRepository(@ApplicationContext appContext: Context): SharedPrefRepository {
        return SharedPrefRepositoryImpl(appContext)
    }

    @Provides
    fun provideMainRepository(): MainRepository {
        return MainRepositoryImpl()
    }
}