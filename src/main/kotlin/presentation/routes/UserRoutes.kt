package com.example.presentation.routes

import domain.model.User
import domain.repository.ClientRepository
import domain.repository.UserRepository
import domain.usecase.GetUserByIdUseCase
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import presentation.dto.CreateUserRequest
import presentation.dto.ProfileResponse
import presentation.dto.UpdateProfileRequest
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import presentation.util.role
import presentation.util.userId

fun Route.userRoutes(
    getUserByIdUseCase: GetUserByIdUseCase,
    userRepository: UserRepository,
    clientRepository: ClientRepository
) {
    authenticate("auth-jwt") {
        route("/users") {

            // Профиль текущего пользователя + дата абонемента
            get("/me/profile") {
                val userId = call.userId()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))

                val user = getUserByIdUseCase(userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))

                val client = clientRepository.getByUserId(userId)

                call.respond(
                    ProfileResponse(
                        id = user.id,
                        fio = user.fio,
                        phone = user.phone,
                        email = user.email,
                        cardEndDate = client?.cardEndDate?.toString(),
                        userTypeId = user.userTypeId
                    )
                )
            }

            // Обновить ФИО / телефон / почту текущего пользователя
            patch("/me") {
                val userId = call.userId()
                    ?: return@patch call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))

                val user = getUserByIdUseCase(userId)
                    ?: return@patch call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))

                val request = call.receive<UpdateProfileRequest>()

                try {
                    userRepository.update(
                        userId,
                        user.copy(fio = request.fio, phone = request.phone, email = request.email)
                    )
                    call.respond(mapOf("message" to "Профиль обновлён"))
                } catch (e: Exception) {
                    val msg = e.message ?: ""
                    if (msg.contains("unique", ignoreCase = true) || msg.contains("duplicate", ignoreCase = true)) {
                        call.respond(HttpStatusCode.Conflict, mapOf("error" to "Телефон или почта уже используются"))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to msg))
                    }
                }
            }

            // Текущий пользователь (базовый)
            get("/me") {
                val userId = call.userId()
                    ?: return@get call.respond(mapOf("error" to "Unauthorized"))

                val user = getUserByIdUseCase(userId)
                    ?: return@get call.respond(mapOf("error" to "User not found"))

                call.respond(user)
            }

            get {
                call.respond(userRepository.getAll())
            }

            get("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val user = userRepository.getById(id)
                if (user != null) call.respond(user)
                else call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
            }

            post {
                val request = call.receive<CreateUserRequest>()
                val id = userRepository.create(
                    User(0, request.fio, request.phone, request.email, request.userTypeId, request.password)
                )
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            }

            put("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val request = call.receive<CreateUserRequest>()
                val updated = userRepository.update(
                    id,
                    User(id, request.fio, request.phone, request.email, request.userTypeId, request.password)
                )
                call.respond(mapOf("updated" to updated))
            }

            delete("/{id}") {
                if (call.role() != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
                    return@delete
                }
                val id = call.parameters["id"]!!.toInt()
                call.respond(mapOf("deleted" to userRepository.delete(id)))
            }
        }
    }
}
