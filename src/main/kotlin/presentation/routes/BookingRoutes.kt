package presentation.routes

import domain.model.Booking
import domain.usecase.*
import presentation.dto.CoachBookingRequest
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
    searchBookingsByNameUseCase: SearchBookingsByNameUseCase,
    updateBookingUseCase: UpdateBookingUseCase,
    getParticipantsByBookingUseCase: GetParticipantsByBookingUseCase
) {

    authenticate("auth-jwt") {

        route("/bookings") {

            // ── Все занятия (любая роль) ──────────────────────────────────────
            get {
                if (call.role() == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    return@get
                }
                call.respond(getBookingsUseCase())
            }

            // ── Занятия клиента ───────────────────────────────────────────────
            get("/my") {
                if (call.role() != "CLIENT") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@get
                }
                val userId = call.userId()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                val clientId = getClientByUserIdUseCase(userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Client not found"))
                call.respond(getBookingsByClientUseCase(clientId))
            }

            // ── Занятия тренера (свои) ────────────────────────────────────────
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

            // ── Поиск ─────────────────────────────────────────────────────────
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

            // ── Занятие по ID ─────────────────────────────────────────────────
            get("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val booking = getBookingByIdUseCase(id)
                if (booking != null) call.respond(booking)
                else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Booking not found"))
            }

            // ── Участники занятия ─────────────────────────────────────────────
            get("/{id}/participants") {
                if (call.role() != "COACH" && call.role() != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@get
                }
                val id = call.parameters["id"]!!.toInt()
                call.respond(getParticipantsByBookingUseCase(id))
            }

            // ── Создать занятие (тренер — coachId берётся из JWT) ─────────────
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
                        workoutId = 1,   // значение по умолчанию
                        slots = request.slots,
                        name = request.name,
                        extra = request.extra,
                        time = request.time
                    )
                )
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            }

            // ── Редактировать занятие (тренер, только своё) ───────────────────
            patch("/{id}") {
                if (call.role() != "COACH" && call.role() != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@patch
                }
                val userId = call.userId()
                    ?: return@patch call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                val coachId = getCoachByUserIdUseCase(userId)
                    ?: return@patch call.respond(HttpStatusCode.NotFound, mapOf("error" to "Coach not found"))

                val id = call.parameters["id"]!!.toInt()
                val existing = getBookingByIdUseCase(id)
                    ?: return@patch call.respond(HttpStatusCode.NotFound, mapOf("error" to "Booking not found"))

                if (existing.coachId != coachId) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Не ваше занятие"))
                    return@patch
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

            // ── Удалить занятие (тренер — только своё; admin — любое) ─────────
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

            // ── Записаться (CLIENT) ───────────────────────────────────────────
            post("/{id}/join") {
                if (call.role() != "CLIENT") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@post
                }
                val userId = call.userId()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                val clientId = getClientByUserIdUseCase(userId)
                    ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Client not found"))
                val bookingId = call.parameters["id"]!!.toInt()
                val success = addClientToBookingUseCase(bookingId, clientId)
                if (success) call.respond(mapOf("message" to "Вы успешно записаны"))
                else call.respond(HttpStatusCode.Conflict, mapOf("error" to "Вы уже записаны или нет свободных мест"))
            }

            // ── Отменить запись (CLIENT) ──────────────────────────────────────
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
