package presentation.routes

import domain.model.Workout
import domain.usecase.*
import presentation.dto.WorkoutRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.workoutRoutes(
    createWorkoutUseCase: CreateWorkoutUseCase,
    getAllWorkoutsUseCase: GetAllWorkoutsUseCase,
    getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    deleteWorkoutUseCase: DeleteWorkoutUseCase
) {

    route("/workouts") {

        get {
            call.respond(getAllWorkoutsUseCase())
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val workout = getWorkoutByIdUseCase(id)

            if (workout == null) {
                call.respond(mapOf("error" to "Not found"))
            } else {
                call.respond(workout)
            }
        }

        post {
            val request = call.receive<WorkoutRequest>()

            val id = createWorkoutUseCase(
                Workout(
                    id = 0,
                    coachTypeId = request.coachTypeId,
                    name = request.name,
                    description = request.description,
                    duration = request.duration
                )
            )

            call.respond(mapOf("id" to id))
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val deleted = deleteWorkoutUseCase(id)

            call.respond(mapOf("deleted" to deleted))
        }
    }
}