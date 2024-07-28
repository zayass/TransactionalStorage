package org.zayass.assessment.storage

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.zayass.assessment.storage.core.StringSuspendStorage
import org.zayass.assessment.storage.core.SuspendStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideStorage(): StringSuspendStorage = SuspendStorage()
}
