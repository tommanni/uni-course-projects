package com.example.harjoitus_6_8.model.service.module

import com.example.harjoitus_6_8.model.service.AccountService
import com.example.harjoitus_6_8.model.service.StorageService
import com.example.harjoitus_6_8.model.service.impl.AccountServiceImpl
import com.example.harjoitus_6_8.model.service.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideStorageService(impl: StorageServiceImpl): StorageService
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService
}