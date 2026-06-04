package com.example.presentation.routes

import domain.repository.ClientRepository
import domain.repository.UserRepository
import domain.usecase.GetUserByIdUseCase
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import presentation.dto.ProfileResponse
import presentation.dto.UpdateProfileRequest
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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

        }
    }
}
