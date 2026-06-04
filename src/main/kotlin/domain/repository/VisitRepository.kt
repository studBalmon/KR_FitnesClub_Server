package domain.repository

import java.time.LocalDateTime

data class InsideVisit(
    val userId: Int,
    val fio: String,
    val entryTime: LocalDateTime
)

data class OpenVisit(
    val id: Int,
    val entryTime: LocalDateTime
)

interface VisitRepository {

    fun findOpenVisit(userId: Int): OpenVisit?

    fun checkIn(userId: Int)

    fun checkOut(visitId: Int)

    fun getInside(): List<InsideVisit>

    fun deleteByUser(userId: Int)

    fun autoCloseStale(thresholdHours: Long): Int
}
