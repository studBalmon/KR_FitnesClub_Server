package com.example.plugins.di

import com.example.data.repository.UserRepositoryImpl
import domain.repository.UserRepository
import domain.usecase.CreateWorkoutUseCase
import domain.usecase.DeleteWorkoutUseCase
import domain.usecase.GetAllWorkoutsUseCase
import domain.usecase.GetWorkoutByIdUseCase
import domain.usecase.LoginUserUseCase
import domain.usecase.RegisterUserUseCase
import org.koin.dsl.module

val appModule = module {

    // Repository
    single<UserRepository> { UserRepositoryImpl() }

    // UseCases
    single { LoginUserUseCase(get()) }
    single { RegisterUserUseCase(get<UserRepository>()) }
    single { CreateWorkoutUseCase(get()) }
    single { GetAllWorkoutsUseCase(get()) }
    single { GetWorkoutByIdUseCase(get()) }
    single { DeleteWorkoutUseCase(get()) }
}