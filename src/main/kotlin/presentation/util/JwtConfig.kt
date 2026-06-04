package com.example.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {

    private val secret = System.getenv("JWT_SECRET") ?: "super-secret-key-change-me"
    private const val issuer = "fitness-app"
    private const val audience = "fitness-client"
    // отдельная аудитория для пропуска: такой токен НЕ проходит обычную auth-проверку,
    // поэтому его нельзя использовать как bearer для доступа к API
    private const val passAudience = "fitness-pass"
    private const val passTtlMs = 1000L * 60 * 5   // 5 минут

    private val algorithm get() = Algorithm.HMAC256(secret)

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

    /** Подписанный токен-пропуск (для QR), кодирует id пользователя, живёт недолго. */
    fun generatePassToken(userId: Long): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(passAudience)
            .withClaim("userId", userId)
            .withClaim("type", "pass")
            .withExpiresAt(Date(System.currentTimeMillis() + passTtlMs))
            .sign(algorithm)

    /** Проверяет токен-пропуск и возвращает id пользователя или null (подделка/просрочен). */
    fun verifyPassToken(token: String): Long? = try {
        JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(passAudience)
            .build()
            .verify(token)
            .getClaim("userId")
            .asLong()
    } catch (e: Exception) {
        null
    }
}