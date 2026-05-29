package presentation.routes

import domain.model.Booking
import domain.usecase.*
import presentation.dto.BookingRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import presentation.util.role
import presentation.util.userId

fun Route.bookingRoutes(
    createBookingUseCase: CreateBookingUseCase,
    getBookingsUseCase: GetBookingsUseCase,
    getBookingByIdUseCase: GetBookingByIdUseCase,
    getBookingsByClientUseCase: GetBookingsByClientUseCase,
    deleteBookingUseCase: DeleteBookingUseCase,
    addClientToBookingUseCase: AddClientToBookingUseCase,
    leaveBookingUseCase: LeaveBookingUseCase,
    getClientByUserIdUseCase: GetClientByUserIdUseCase,
    getCoachByUserIdUseCase: GetCoachByUserIdUseCase,
    getBookingsByCoachUseCase: GetBookingsByCoachUseCase,
    searchBookingsByNameUseCase: SearchBookingsByNameUseCase
) {

    authenticate("auth-jwt") {

        route("/bookings") {

            // ВСЕ
            get {
                val role = call.role()

                if (role == null) {
                    call.respond(mapOf("error" to "Unauthorized"))
                    return@get
                }

                call.respond(getBookingsUseCase())
            }

            // CLIENT
            get("/my") {

                if (call.role() != "CLIENT") {
                    call.respond(mapOf("error" to "Forbidden"))
                    return@get
                }

                val userId = call.userId()
                    ?: return@get call.respond(mapOf("error" to "Unauthorized"))

                val clientId = getClientByUserIdUseCase(userId)
                    ?: return@get call.respond(mapOf("error" to "Client not found"))

                call.respond(getBookingsByClientUseCase(clientId))
            }

            // COACH + ADMIN
            post {

                val role = call.role()

                if (role != "COACH" && role != "ADMIN") {
                    call.respond(mapOf("error" to "Forbidden"))
                    return@post
                }

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

            get("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val booking = getBookingByIdUseCase(id)
                if (booking != null) call.respond(booking)
                else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Booking not found"))
            }

            get("/search") {
                val role = call.role()

                if (role == null) {
                    call.respond(mapOf("error" to "Unauthorized"))
                    return@get
                }

                val query = call.request.queryParameters["q"]

                if (query.isNullOrBlank()) {
                    call.respond(mapOf("error" to "Query is empty"))
                    return@get
                }

                call.respond(searchBookingsByNameUseCase(query))
            }

            // CLIENT
            post("/{id}/join") {

                if (call.role() != "CLIENT") {
                    call.respond(mapOf("error" to "Forbidden"))
                    return@post
                }

                val userId = call.userId()
                    ?: return@post call.respond(mapOf("error" to "Unauthorized"))

                val clientId = getClientByUserIdUseCase(userId)
                    ?: return@post call.respond(mapOf("error" to "Client not found"))

                val bookingId = call.parameters["id"]!!.toInt()

                val success = addClientToBookingUseCase(bookingId, clientId)

                if (success) {
                    call.respond(mapOf("message" to "Вы успешно записаны"))
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        mapOf("error" to "Вы уже записаны на это занятие или нет свободных мест")
                    )
                }
            }

            // CLIENT — отменить свою запись
            delete("/{id}/join") {
                if (call.role() != "CLIENT") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@delete
                }
                val userId = call.userId()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                val clientId = getClientByUserIdUseCase(userId)
                    ?: return@delete call.respond(HttpStatusCode.NotFound, mapOf("error" to "Client not found"))
                val bookingId = call.parameters["id"]!!.toInt()
                val removed = leaveBookingUseCase(bookingId, clientId)
                if (removed) call.respond(mapOf("message" to "Запись отменена"))
                else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Вы не записаны на это занятие"))
            }

            // ADMIN
            delete("/{id}") {

                if (call.role() != "ADMIN") {
                    call.respond(mapOf("error" to "Forbidden"))
                    return@delete
                }

                val id = call.parameters["id"]!!.toInt()
                val deleted = deleteBookingUseCase(id)

                call.respond(mapOf("deleted" to deleted))
            }

            get("/coach") {

                if (call.role() != "COACH" && call.role() != "ADMIN") {
                    call.respond(mapOf("error" to "Forbidden"))
                    return@get
                }

                val userId = call.userId()
                    ?: return@get call.respond(mapOf("error" to "Unauthorized"))

                val coachId = getCoachByUserIdUseCase(userId)
                    ?: return@get call.respond(mapOf("error" to "Coach not found"))

                val bookings = getBookingsByCoachUseCase(coachId)

                call.respond(bookings)
            }
        }
    }
}