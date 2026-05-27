package com.example.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {

    private const val secret = "super-secret-key-change-me"
    private const val issuer = "fitness-app"
    private const val audience = "fitness-client"

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: Long, email: String, role: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .sign(algorithm)
    }

    fun verifier() = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()
}