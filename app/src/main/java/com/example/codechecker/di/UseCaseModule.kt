package com.example.codechecker.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Note: UseCase classes use @Inject constructors and are automatically
 * provided by Hilt. No explicit module binding is needed.
 * This prevents dependency cycles in the Dagger graph.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule
