package presentation.routes

import domain.usecase.LoginUserUseCase
import domain.usecase.RegisterUserUseCase
import com.example.presentation.dto.LoginRequest
import presentation.dto.RegisterRequest
import com.example.util.JwtConfig
import com.example.util.PasswordHasher
import domain.usecase.CreateCoachByUserUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import presentation.dto.CoachRegisterRequest

fun Route.authRoutes(
    loginUserUseCase: LoginUserUseCase,
    registerUserUseCase: RegisterUserUseCase,
    createCoachByUserUseCase: CreateCoachByUserUseCase,
) {

    route("/auth") {

        post("/register") {
            val request = call.receive<RegisterRequest>()
            try {
                registerUserUseCase(
                    email = request.email,
                    password = PasswordHasher.hash(request.password),
                    fio = request.fio,
                    phone = request.phone,
                    userTypeId = request.userTypeId
                )
                call.respond(HttpStatusCode.Created, mapOf("message" to "User created"))
            } catch (e: Exception) {
                val message = e.message ?: ""
                if (message.contains("unique", ignoreCase = true) || message.contains("duplicate", ignoreCase = true)) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Пользователь с таким email или телефоном уже существует"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to message))
                }
            }
        }

        post("/register/coach") {
            val request = call.receive<CoachRegisterRequest>()
            try {
                createCoachByUserUseCase(
                    email = request.email,
                    password = PasswordHasher.hash(request.password),
                    fio = request.fio,
                    phone = request.phone,
                    userTypeId = 2,
                    coachTypeId = request.coachTypeId
                )
                call.respond(HttpStatusCode.Created, mapOf("message" to "Coach created"))
            } catch (e: Exception) {
                val message = e.message ?: ""
                if (message.contains("unique", ignoreCase = true) || message.contains("duplicate", ignoreCase = true)) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Пользователь с таким email или телефоном уже существует"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to message))
                }
            }
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val result = loginUserUseCase(request.email, request.password)

            if (result == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Неверный email или пароль"))
                return@post
            }

            val token = JwtConfig.generateToken(
                userId = result.user.id.toLong(),
                email = result.user.email,
                role = result.role
            )

            call.respond(mapOf("token" to token))
        }
    }
}
