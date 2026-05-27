package presentation.routes

import domain.model.Booking
import domain.usecase.*
import presentation.dto.BookingRequest
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.bookingRoutes(
    createBookingUseCase: CreateBookingUseCase,
    getBookingsUseCase: GetBookingsUseCase,
    getBookingsByClientUseCase: GetBookingsByClientUseCase,
    deleteBookingUseCase: DeleteBookingUseCase,
    addClientToBookingUseCase: AddClientToBookingUseCase,
    getClientByUserIdUseCase: GetClientByUserIdUseCase
) {

    authenticate("auth-jwt") {

        route("/bookings") {

            get {
                call.respond(getBookingsUseCase())
            }

            get("/my") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()?.toInt()

                if (userId == null) {
                    call.respond(mapOf("error" to "Unauthorized"))
                    return@get
                }

                val clientId = getClientByUserIdUseCase(userId)

                if (clientId == null) {
                    call.respond(mapOf("error" to "Client not found"))
                    return@get
                }

                call.respond(getBookingsByClientUseCase(clientId))
            }

            post {
                val request = call.receive<BookingRequest>()

                val id = createBookingUseCase(
                    Booking(
                        id = 0,
                        coachId = request.coachId,
                        workoutId = request.workoutId,
                        slots = request.slots,
                        name = request.name,
                        extra = request.extra,
                        time = request.time
                    )
                )

                call.respond(mapOf("id" to id))
            }

            post("/{id}/join") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()?.toInt()

                if (userId == null) {
                    call.respond(mapOf("error" to "Unauthorized"))
                    return@post
                }

                val clientId = getClientByUserIdUseCase(userId)

                if (clientId == null) {
                    call.respond(mapOf("error" to "Client not found"))
                    return@post
                }

                val bookingId = call.parameters["id"]!!.toInt()

                val success = addClientToBookingUseCase(bookingId, clientId)

                call.respond(mapOf("success" to success))
            }

            delete("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val deleted = deleteBookingUseCase(id)

                call.respond(mapOf("deleted" to deleted))
            }
        }
    }
}