package plugins.di

import com.example.data.repository.UserRepositoryImpl
import data.repository.WorkoutRepositoryImpl
import domain.repository.UserRepository
import domain.repository.WorkoutRepository
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
    single<WorkoutRepository> { WorkoutRepositoryImpl() }

    // UseCases
    single { LoginUserUseCase(get()) }
    single { RegisterUserUseCase(get<UserRepository>()) }
    single { CreateWorkoutUseCase(get()) }
    single { GetAllWorkoutsUseCase(get()) }
    single { GetWorkoutByIdUseCase(get()) }
    single { DeleteWorkoutUseCase(get()) }
}