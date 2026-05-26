package domain.repository

import domain.model.User

interface UserRepository {

    fun create(user: User): Int

    fun getAll(): List<User>

    fun getById(id: Int): User?

    fun update(id: Int, user: User): Boolean

    fun delete(id: Int): Boolean

    fun findByEmail(email: String): User?
}