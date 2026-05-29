package presentation.routes

import com.example.presentation.routes.userRoutes
import domain.repository.ClientRepository
import domain.repository.UserRepository
import domain.usecase.*
import domain.usecase.LeaveBookingUseCase
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val loginUserUseCase by inject<LoginUserUseCase>()
    val registerUserUseCase by inject<RegisterUserUseCase>()

    val createWorkoutUseCase by inject<CreateWorkoutUseCase>()
    val getAllWorkoutsUseCase by inject<GetAllWorkoutsUseCase>()
    val getWorkoutByIdUseCase by inject<GetWorkoutByIdUseCase>()
    val deleteWorkoutUseCase by inject<DeleteWorkoutUseCase>()

    val createBookingUseCase by inject<CreateBookingUseCase>()
    val getBookingsUseCase by inject<GetBookingsUseCase>()
    val getBookingsByClientUseCase by inject<GetBookingsByClientUseCase>()
    val deleteBookingUseCase by inject<DeleteBookingUseCase>()

    val addClientToBookingUseCase by inject<AddClientToBookingUseCase>()
    val leaveBookingUseCase by inject<LeaveBookingUseCase>()
    val getClientByUserIdUseCase by inject<GetClientByUserIdUseCase>()
    val getCoachByUserIdUseCase by inject<GetCoachByUserIdUseCase>()
    val getBookingsByCoachUseCase by inject<GetBookingsByCoachUseCase>()

    val createCoachByUserUseCase by inject<CreateCoachByUserUseCase>()
    val searchBookingsByNameUseCase by inject<SearchBookingsByNameUseCase>()
    val searchWorkoutsByNameUseCase by inject<SearchWorkoutsByNameUseCase>()
    val getBookingByIdUseCase by inject<GetBookingByIdUseCase>()
    val getUserByIdUseCase by inject<GetUserByIdUseCase>()
    val userRepository by inject<UserRepository>()
    val clientRepository by inject<ClientRepository>()

    routing {

        authRoutes(
            loginUserUseCase,
            registerUserUseCase,
            createCoachByUserUseCase
        )

        workoutRoutes(
            createWorkoutUseCase,
            getAllWorkoutsUseCase,
            getWorkoutByIdUseCase,
            deleteWorkoutUseCase,
            searchWorkoutsByNameUseCase
        )

        bookingRoutes(
            createBookingUseCase,
            getBookingsUseCase,
            getBookingByIdUseCase,
            getBookingsByClientUseCase,
            deleteBookingUseCase,
            addClientToBookingUseCase,
            leaveBookingUseCase,
            getClientByUserIdUseCase,
            getCoachByUserIdUseCase,
            getBookingsByCoachUseCase,
            searchBookingsByNameUseCase
        )

        userRoutes(
            getUserByIdUseCase,
            userRepository,
            clientRepository
        )
    }
}