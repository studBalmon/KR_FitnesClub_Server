package presentation.routes

import domain.usecase.*
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
    val getClientByUserIdUseCase by inject<GetClientByUserIdUseCase>()

    routing {
        authRoutes(loginUserUseCase, registerUserUseCase)

        workoutRoutes(
            createWorkoutUseCase,
            getAllWorkoutsUseCase,
            getWorkoutByIdUseCase,
            deleteWorkoutUseCase
        )

        bookingRoutes(
            createBookingUseCase,
            getBookingsUseCase,
            getBookingsByClientUseCase,
            deleteBookingUseCase,
            addClientToBookingUseCase,
            getClientByUserIdUseCase
        )
    }
}