package plugins.di

import com.example.data.repository.UserRepositoryImpl
import data.repository.BookingRepositoryImpl
import data.repository.ClientRepositoryImpl
import data.repository.WorkoutRepositoryImpl
import domain.repository.BookingRepository
import domain.repository.ClientRepository
import domain.repository.UserRepository
import domain.repository.WorkoutRepository
import domain.usecase.AddClientToBookingUseCase
import domain.usecase.CreateBookingUseCase
import domain.usecase.CreateWorkoutUseCase
import domain.usecase.DeleteBookingUseCase
import domain.usecase.DeleteWorkoutUseCase
import domain.usecase.GetAllWorkoutsUseCase
import domain.usecase.GetBookingsByClientUseCase
import domain.usecase.GetBookingsUseCase
import domain.usecase.GetClientByUserIdUseCase
import domain.usecase.GetWorkoutByIdUseCase
import domain.usecase.JoinBookingUseCase
import domain.usecase.LoginUserUseCase
import domain.usecase.RegisterUserUseCase
import org.koin.dsl.module

val appModule = module {

    // Repository
    single<UserRepository> { UserRepositoryImpl() }
    single<WorkoutRepository> { WorkoutRepositoryImpl() }
    single<BookingRepository> { BookingRepositoryImpl() }
    single<ClientRepository> { ClientRepositoryImpl() }

    // UseCases
    single { LoginUserUseCase(get()) }
    single { RegisterUserUseCase(get<UserRepository>()) }
    single { CreateWorkoutUseCase(get()) }
    single { GetAllWorkoutsUseCase(get()) }
    single { GetWorkoutByIdUseCase(get()) }
    single { DeleteWorkoutUseCase(get()) }
    single { CreateBookingUseCase(get()) }
    single { GetBookingsUseCase(get()) }
    single { GetBookingsByClientUseCase(get()) }
    single { DeleteBookingUseCase(get()) }
    single { JoinBookingUseCase(get()) }
    single { AddClientToBookingUseCase(get()) }
    single { GetClientByUserIdUseCase(get()) }
}