package presentation.routes

import domain.repository.CoachRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import presentation.dto.CoachResponse
import presentation.util.role

fun Route.coachRoutes(
    coachRepository: CoachRepository
) {
    authenticate("auth-jwt") {

        get("/coaches") {
            if (call.role() == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                return@get
            }
            call.respond(coachRepository.getAllCoaches().map {
                CoachResponse(it.id, it.fio, it.coachTypeName)
            })
        }
    }
}
