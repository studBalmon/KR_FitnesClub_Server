package domain.repository

import domain.model.User

interface CoachRepository {
    fun create(userId: Int, coachTypeId: Int): Int

    fun getByUserId(userId: Int): Int?
}