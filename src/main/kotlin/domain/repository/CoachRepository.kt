package domain.repository

interface CoachRepository {
    fun create(userId: Int, coachTypeId: Int): Int

    fun getByUserId(userId: Int): Int?
}