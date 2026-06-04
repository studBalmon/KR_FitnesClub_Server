package presentation.routes

import domain.model.Workout
import domain.usecase.*
import presentation.dto.WorkoutRequest
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import presentation.util.role

fun Route.workoutRoutes(
    createWorkoutUseCase: CreateWorkoutUseCase,
    getAllWorkoutsUseCase: GetAllWorkoutsUseCase,
    getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    deleteWorkoutUseCase: DeleteWorkoutUseCase,
    searchWorkoutsByNameUseCase: SearchWorkoutsByNameUseCase
) {

    authenticate("auth-jwt") {

        route("/workouts") {


            get {
                call.respond(getAllWorkoutsUseCase())
            }

            get("/search") {

                val query = call.request.queryParameters["q"]

                if (query.isNullOrBlank()) {
                    call.respond(mapOf("error" to "Query is empty"))
                    return@get
                }

                call.respond(searchWorkoutsByNameUseCase(query))
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
                val role = call.role()

                if (role != "COACH" && role != "ADMIN") {
                    call.respond(mapOf("error" to "Forbidden"))
                    return@post
                }

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
                val role = call.role()

                if (role != "ADMIN") {
                    call.respond(mapOf("error" to "Forbidden"))
                    return@delete
                }

                val id = call.parameters["id"]!!.toInt()
                val deleted = deleteWorkoutUseCase(id)

                call.respond(mapOf("deleted" to deleted))
            }
        }
    }
}