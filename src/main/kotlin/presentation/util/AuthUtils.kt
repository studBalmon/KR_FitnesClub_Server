package presentation.util

import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.principal

fun ApplicationCall.userId(): Int? =
    principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("userId")
        ?.asInt()

fun ApplicationCall.role(): String? =
    principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("role")
        ?.asString()