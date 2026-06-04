package domain.repository

import java.time.LocalDateTime

/** Пользователь, находящийся внутри (открытое посещение). */
data class InsideVisit(
    val userId: Int,
    val fio: String,
    val entryTime: LocalDateTime
)

/** Открытое посещение пользователя. */
data class OpenVisit(
    val id: Int,
    val entryTime: LocalDateTime
)

interface VisitRepository {

    /** Открытое посещение пользователя (вошёл и не вышел) или null. */
    fun findOpenVisit(userId: Int): OpenVisit?

    /** Отметить вход (создаёт открытое посещение). */
    fun checkIn(userId: Int)

    /** Отметить выход (проставляет время выхода = сейчас). */
    fun checkOut(visitId: Int)

    /** Список пользователей внутри. */
    fun getInside(): List<InsideVisit>

    /** Удалить все посещения пользователя (при удалении пользователя). */
    fun deleteByUser(userId: Int)

    /**
     * Закрывает «зависшие» посещения старше [thresholdHours] часов
     * (время выхода = время входа + threshold). Возвращает число закрытых.
     */
    fun autoCloseStale(thresholdHours: Long): Int
}
