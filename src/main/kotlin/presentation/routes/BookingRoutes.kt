package presentation.routes

import domain.model.Booking
import domain.repository.ClientRepository
import domain.usecase.*
import presentation.dto.CoachBookingRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import presentation.util.role
import presentation.util.userId
import java.time.LocalDate

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
    searchBookingsByNameUseCase: SearchBookingsByNameUseCase,
    updateBookingUseCase: UpdateBookingUseCase,
    getParticipantsByBookingUseCase: GetParticipantsByBookingUseCase,
    removeClientFromAllBookingsUseCase: RemoveClientFromAllBookingsUseCase,
    clientRepository: ClientRepository
) {

    authenticate("auth-jwt") {

        route("/bookings") {

            get {
                if (call.role() == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    return@get
                }
                call.respond(getBookingsUseCase())
            }

            get("/my") {
                if (call.role() != "CLIENT") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@get
                }
                val userId = call.userId()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                val client = clientRepository.getByUserId(userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Client not found"))

                if (client.cardEndDate.isBefore(LocalDate.now())) {
                    removeClientFromAllBookingsUseCase(client.id)
                    call.respond(emptyList<Booking>())
                    return@get
                }

                call.respond(getBookingsByClientUseCase(client.id))
            }

            get("/coach") {
                if (call.role() != "COACH" && call.role() != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@get
                }
                val userId = call.userId()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                val coachId = getCoachByUserIdUseCase(userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Coach not found"))
                call.respond(getBookingsByCoachUseCase(coachId))
            }

            get("/search") {
                if (call.role() == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    return@get
                }
                val query = call.request.queryParameters["q"]
                if (query.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Query is empty"))
                    return@get
                }
                call.respond(searchBookingsByNameUseCase(query))
            }

            get("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val booking = getBookingByIdUseCase(id)
                if (booking != null) call.respond(booking)
                else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Booking not found"))
            }

            get("/{id}/participants") {
                if (call.role() != "COACH" && call.role() != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@get
                }
                val id = call.parameters["id"]!!.toInt()
                call.respond(getParticipantsByBookingUseCase(id))
            }

            post {
                val role = call.role()
                if (role != "COACH" && role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@post
                }
                val userId = call.userId()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                val coachId = getCoachByUserIdUseCase(userId)
                    ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Coach not found"))

                val request = call.receive<CoachBookingRequest>()
                val id = createBookingUseCase(
                    Booking(
                        id = 0,
                        coachId = coachId,
                        workoutId = 1,   
                        slots = request.slots,
                        name = request.name,
                        extra = request.extra,
                        time = request.time
                    )
                )
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            }

            patch("/{id}") {
                val role = call.role()
                if (role != "COACH" && role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@patch
                }

                val id = call.parameters["id"]!!.toInt()
                val existing = getBookingByIdUseCase(id)
                    ?: return@patch call.respond(HttpStatusCode.NotFound, mapOf("error" to "Booking not found"))

                if (role == "COACH") {
                    val userId = call.userId()
                        ?: return@patch call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    val coachId = getCoachByUserIdUseCase(userId)
                        ?: return@patch call.respond(HttpStatusCode.NotFound, mapOf("error" to "Coach not found"))
                    if (existing.coachId != coachId) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Не ваше занятие"))
                        return@patch
                    }
                }

                val request = call.receive<CoachBookingRequest>()
                val updated = updateBookingUseCase(
                    existing.copy(
                        name = request.name,
                        slots = request.slots,
                        extra = request.extra,
                        time = request.time
                    )
                )
                if (updated) call.respond(mapOf("message" to "Занятие обновлено"))
                else call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Не удалось обновить"))
            }

            delete("/{id}") {
                val role = call.role()
                if (role != "ADMIN" && role != "COACH") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@delete
                }
                val id = call.parameters["id"]!!.toInt()

                if (role == "COACH") {
                    val userId = call.userId()
                        ?: return@delete call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    val coachId = getCoachByUserIdUseCase(userId)
                        ?: return@delete call.respond(HttpStatusCode.NotFound, mapOf("error" to "Coach not found"))
                    val existing = getBookingByIdUseCase(id)
                        ?: return@delete call.respond(HttpStatusCode.NotFound, mapOf("error" to "Booking not found"))
                    if (existing.coachId != coachId) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Не ваше занятие"))
                        return@delete
                    }
                }

                call.respond(mapOf("deleted" to deleteBookingUseCase(id)))
            }

            post("/{id}/join") {
                if (call.role() != "CLIENT") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@post
                }
                val userId = call.userId()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                val client = clientRepository.getByUserId(userId)
                    ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Client not found"))

                if (client.cardEndDate.isBefore(LocalDate.now())) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Абонемент неактивен"))
                    return@post
                }

                val bookingId = call.parameters["id"]!!.toInt()
                val booking = getBookingByIdUseCase(bookingId)
                    ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Booking not found"))

                if (booking.time.toLocalDate().isBefore(LocalDate.now())) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Занятие уже прошло"))
                    return@post
                }

                val success = addClientToBookingUseCase(bookingId, client.id)
                if (success) call.respond(mapOf("message" to "Вы успешно записаны"))
                else call.respond(HttpStatusCode.Conflict, mapOf("error" to "Вы уже записаны или нет свободных мест"))
            }

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
        }
    }
}
