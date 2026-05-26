package presentation.routes

import domain.usecase.CreateWorkoutUseCase
import domain.usecase.DeleteWorkoutUseCase
import domain.usecase.GetAllWorkoutsUseCase
import domain.usecase.GetWorkoutByIdUseCase
import domain.usecase.LoginUserUseCase
import domain.usecase.RegisterUserUseCase
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

    routing {
        authRoutes(loginUserUseCase, registerUserUseCase)
        workoutRoutes(
            createWorkoutUseCase,
            getAllWorkoutsUseCase,
            getWorkoutByIdUseCase,
            deleteWorkoutUseCase
        )
        bookingRoutes()
    }
}