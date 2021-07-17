package com.moony.calc.di

import com.moony.calc.data.CategoryRepository
import com.moony.calc.data.TransactionRepository
import com.moony.calc.database.CategoryDao
import com.moony.calc.database.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideTransactionRepository(dao: TransactionDao): TransactionRepository =
        TransactionRepository(dao)

    @Provides
    @ViewModelScoped
    fun provideCategoryRepository(dao: CategoryDao): CategoryRepository = CategoryRepository(dao)

}