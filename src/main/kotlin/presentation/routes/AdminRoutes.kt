package presentation.routes

import com.example.data.seed.TestDataSeeder
import com.example.util.PasswordHasher
import domain.model.User
import domain.model.Workout
import domain.repository.ClientRepository
import domain.repository.CoachRepository
import domain.repository.UserRepository
import domain.repository.WorkoutRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import presentation.dto.*
import presentation.util.role

fun Route.adminRoutes(
    userRepository: UserRepository,
    clientRepository: ClientRepository,
    coachRepository: CoachRepository,
    workoutRepository: WorkoutRepository
) {
    authenticate("auth-jwt") {
        route("/admin") {

            fun checkAdmin(role: String?): Boolean = role == "ADMIN"

            // ════════════════════════════════════════════════════════════════
            // ТИПЫ ТРЕНЕРОВ (coach_types)
            // ════════════════════════════════════════════════════════════════

            get("/coach-types") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@get }
                call.respond(coachRepository.getAllCoachTypes().map { CoachTypeResponse(it.first, it.second) })
            }

            post("/coach-types") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@post }
                val req = call.receive<CoachTypeRequest>()
                try {
                    val id = coachRepository.createCoachTypeName(req.name)
                    call.respond(HttpStatusCode.Created, mapOf("id" to id))
                } catch (e: Exception) {
                    val msg = e.message ?: ""
                    if (msg.contains("unique", ignoreCase = true) || msg.contains("duplicate", ignoreCase = true))
                        call.respond(HttpStatusCode.Conflict, mapOf("error" to "Такой тип тренера уже существует"))
                    else
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to msg))
                }
            }

            patch("/coach-types/{id}") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@patch }
                val id = call.parameters["id"]!!.toInt()
                val req = call.receive<CoachTypeRequest>()
                try {
                    val updated = coachRepository.updateCoachTypeName(id, req.name)
                    if (updated) call.respond(mapOf("message" to "Обновлено"))
                    else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Не найдено"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Такое название уже используется"))
                }
            }

            delete("/coach-types/{id}") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@delete }
                val id = call.parameters["id"]!!.toInt()
                try {
                    val deleted = coachRepository.deleteCoachTypeById(id)
                    if (deleted) call.respond(mapOf("deleted" to true))
                    else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Не найдено"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Нельзя удалить: тип используется тренерами или занятиями"))
                }
            }

            // ════════════════════════════════════════════════════════════════
            // ТИПЫ ЗАНЯТИЙ (workouts)
            // ════════════════════════════════════════════════════════════════

            get("/workouts") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@get }
                call.respond(workoutRepository.getAll())
            }

            post("/workouts") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@post }
                val req = call.receive<WorkoutRequest>()
                try {
                    val id = workoutRepository.create(Workout(0, req.coachTypeId, req.name, req.description, req.duration))
                    call.respond(HttpStatusCode.Created, mapOf("id" to id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Ошибка")))
                }
            }

            patch("/workouts/{id}") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@patch }
                val id = call.parameters["id"]!!.toInt()
                val req = call.receive<WorkoutRequest>()
                val existing = workoutRepository.getById(id)
                    ?: return@patch call.respond(HttpStatusCode.NotFound, mapOf("error" to "Не найдено"))
                val updated = workoutRepository.update(id, existing.copy(
                    coachTypeId = req.coachTypeId,
                    name = req.name,
                    description = req.description,
                    duration = req.duration
                ))
                if (updated) call.respond(mapOf("message" to "Обновлено"))
                else call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Не удалось обновить"))
            }

            delete("/workouts/{id}") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@delete }
                val id = call.parameters["id"]!!.toInt()
                try {
                    val deleted = workoutRepository.delete(id)
                    if (deleted) call.respond(mapOf("deleted" to true))
                    else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Не найдено"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Нельзя удалить: тип занятия используется"))
                }
            }

            // ════════════════════════════════════════════════════════════════
            // ПОЛЬЗОВАТЕЛИ
            // ════════════════════════════════════════════════════════════════

            // Список тренеров (id == booking.coachId) — для аналитики
            get("/coaches") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@get }
                call.respond(coachRepository.getAllCoaches().map {
                    AdminCoachResponse(it.id, it.userId, it.fio, it.coachTypeName)
                })
            }

            // Список клиентов (id == booking.clientIds) — для аналитики
            get("/clients") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@get }
                call.respond(clientRepository.getAllClients().map {
                    AdminClientResponse(it.id, it.userId, it.fio, it.cardEndDate.toString())
                })
            }

            get("/users") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@get }
                val users = userRepository.getAll().map { user ->
                    val roleName = userRepository.getRoleByUserId(user.id)
                    AdminUserResponse(
                        id = user.id,
                        fio = user.fio,
                        phone = user.phone,
                        email = user.email,
                        userTypeId = user.userTypeId,
                        roleName = roleName,
                        coachTypeId = if (roleName == "COACH") coachRepository.getCoachTypeByUserId(user.id) else null,
                        cardEndDate = if (roleName == "CLIENT") clientRepository.getByUserId(user.id)?.cardEndDate?.toString() else null
                    )
                }
                call.respond(users)
            }

            // Продление абонемента клиента
            patch("/clients/{userId}/extend") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@patch }
                val userId = call.parameters["userId"]!!.toInt()
                val req = call.receive<ExtendSubscriptionRequest>()
                if (req.months <= 0) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Количество месяцев должно быть положительным"))
                    return@patch
                }
                val newDate = clientRepository.extendByUserId(userId, req.months)
                if (newDate != null) call.respond(mapOf("cardEndDate" to newDate.toString()))
                else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Клиент не найден"))
            }

            post("/users") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@post }
                val req = call.receive<AdminCreateUserRequest>()
                try {
                    val userId = userRepository.create(User(0, req.fio, req.phone, req.email, req.userTypeId, PasswordHasher.hash(req.password)))
                    when (userRepository.getRoleByUserId(userId)) {
                        "CLIENT" -> clientRepository.create(userId)
                        "COACH"  -> {
                            val coachTypeId = req.coachTypeId
                                ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "coachTypeId обязателен для тренера"))
                            coachRepository.create(userId, coachTypeId)
                        }
                    }
                    call.respond(HttpStatusCode.Created, mapOf("id" to userId))
                } catch (e: Exception) {
                    val msg = e.message ?: ""
                    if (msg.contains("unique", ignoreCase = true) || msg.contains("duplicate", ignoreCase = true))
                        call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email или телефон уже используются"))
                    else
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to msg))
                }
            }

            patch("/users/{id}") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@patch }
                val id = call.parameters["id"]!!.toInt()
                val user = userRepository.getById(id) ?: return@patch call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                val req = call.receive<AdminUpdateUserRequest>()
                val newHash = if (!req.newPassword.isNullOrBlank()) PasswordHasher.hash(req.newPassword) else user.passwordHash
                try {
                    userRepository.update(id, user.copy(fio = req.fio, phone = req.phone, email = req.email, passwordHash = newHash))
                    if (userRepository.getRoleByUserId(id) == "COACH" && req.coachTypeId != null)
                        coachRepository.updateCoachType(id, req.coachTypeId)
                    call.respond(mapOf("message" to "Пользователь обновлён"))
                } catch (e: Exception) {
                    val msg = e.message ?: ""
                    if (msg.contains("unique", ignoreCase = true) || msg.contains("duplicate", ignoreCase = true))
                        call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email или телефон уже используются"))
                    else
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to msg))
                }
            }

            delete("/users/{id}") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@delete }
                val id = call.parameters["id"]!!.toInt()
                userRepository.getById(id) ?: return@delete call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                try {
                    val roleName = userRepository.getRoleByUserId(id)
                    when (roleName) {
                        "CLIENT" -> clientRepository.deleteByUserId(id)
                        "COACH"  -> coachRepository.deleteByUserId(id)
                    }
                    call.respond(mapOf("deleted" to userRepository.delete(id)))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Нельзя удалить: у пользователя есть связанные данные"))
                }
            }

            // ════════════════════════════════════════════════════════════════
            // ТЕСТОВЫЕ ДАННЫЕ (для проверки всех функций)
            // ════════════════════════════════════════════════════════════════

            get("/test-data/status") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@get }
                call.respond(TestDataStatusResponse(TestDataSeeder.isPresent()))
            }

            post("/test-data/toggle") {
                if (!checkAdmin(call.role())) { call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden")); return@post }
                try {
                    if (TestDataSeeder.isPresent()) {
                        TestDataSeeder.clear()
                        call.respond(TestDataToggleResponse("cleared", "Тестовые данные удалены"))
                    } else {
                        TestDataSeeder.seed()
                        call.respond(TestDataToggleResponse("seeded", "Тестовые данные добавлены"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Ошибка")))
                }
            }
        }
    }
}
