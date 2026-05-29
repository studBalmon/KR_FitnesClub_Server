package presentation.plugins.di

import com.example.data.repository.UserRepositoryImpl
import data.repository.BookingRepositoryImpl
import data.repository.ClientRepositoryImpl
import data.repository.CoachRepositoryImpl
import data.repository.WorkoutRepositoryImpl
import domain.repository.BookingRepository
import domain.repository.ClientRepository
import domain.repository.CoachRepository
import domain.repository.UserRepository
import domain.repository.WorkoutRepository
import domain.usecase.AddClientToBookingUseCase
import domain.usecase.GetParticipantsByBookingUseCase
import domain.usecase.LeaveBookingUseCase
import domain.usecase.UpdateBookingUseCase
import domain.usecase.CreateBookingUseCase
import domain.usecase.CreateCoachByUserUseCase
import domain.usecase.CreateWorkoutUseCase
import domain.usecase.DeleteBookingUseCase
import domain.usecase.DeleteWorkoutUseCase
import domain.usecase.GetAllWorkoutsUseCase
import domain.usecase.GetBookingsByClientUseCase
import domain.usecase.GetBookingsByCoachUseCase
import domain.usecase.GetBookingByIdUseCase
import domain.usecase.GetBookingsUseCase
import domain.usecase.GetClientByUserIdUseCase
import domain.usecase.GetCoachByUserIdUseCase
import domain.usecase.GetUserByIdUseCase
import domain.usecase.GetWorkoutByIdUseCase
import domain.usecase.JoinBookingUseCase
import domain.usecase.LoginUserUseCase
import domain.usecase.RegisterUserUseCase
import domain.usecase.SearchBookingsByNameUseCase
import domain.usecase.SearchWorkoutsByNameUseCase
import org.koin.dsl.module

val appModule = module {

    single<UserRepository> { UserRepositoryImpl() }
    single<WorkoutRepository> { WorkoutRepositoryImpl() }
    single<BookingRepository> { BookingRepositoryImpl() }
    single<ClientRepository> { ClientRepositoryImpl() }
    single<CoachRepository> { CoachRepositoryImpl() }

    single { LoginUserUseCase(get()) }
    single { RegisterUserUseCase(get(), get()) }
    single { CreateWorkoutUseCase(get()) }
    single { GetAllWorkoutsUseCase(get()) }
    single { GetWorkoutByIdUseCase(get()) }
    single { DeleteWorkoutUseCase(get()) }
    single { CreateBookingUseCase(get()) }
    single { GetBookingsUseCase(get()) }
    single { GetBookingByIdUseCase(get()) }
    single { GetBookingsByClientUseCase(get()) }
    single { DeleteBookingUseCase(get()) }
    single { JoinBookingUseCase(get()) }
    single { AddClientToBookingUseCase(get()) }
    single { LeaveBookingUseCase(get()) }
    single { GetClientByUserIdUseCase(get()) }
    single { GetCoachByUserIdUseCase(get()) }
    single { GetBookingsByCoachUseCase(get()) }
    single { CreateCoachByUserUseCase(get(), get()) }
    single { SearchBookingsByNameUseCase(get()) }
    single { SearchWorkoutsByNameUseCase(get()) }
    single { GetUserByIdUseCase(get()) }
    single { UpdateBookingUseCase(get()) }
    single { GetParticipantsByBookingUseCase(get(), get(), get()) }
}