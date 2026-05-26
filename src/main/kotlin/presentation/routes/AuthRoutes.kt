package presentation.routes

import domain.usecase.LoginUserUseCase
import domain.usecase.RegisterUserUseCase
import com.example.presentation.dto.LoginRequest
import presentation.dto.RegisterRequest
import com.example.util.JwtConfig
import com.example.util.PasswordHasher
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(
    loginUserUseCase: LoginUserUseCase,
    registerUserUseCase: RegisterUserUseCase
) {

    route("/auth") {

        post("/register") {

            val request = call.receive<RegisterRequest>()
            println(request)

            registerUserUseCase(
                email = request.email,
                password = PasswordHasher.hash(request.password),
                fio = request.fio,
                phone = request.phone
            )

            call.respond(mapOf("message" to "User created"))
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            val user = loginUserUseCase(request.email, request.password)

            if (user == null) {
                call.respond(mapOf("error" to "Invalid credentials"))
                return@post
            }

            val token = JwtConfig.generateToken(user.id.toLong(), user.email)

            call.respond(mapOf("token" to token))
        }
    }
}