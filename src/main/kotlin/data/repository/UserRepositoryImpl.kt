package com.example.data.repository

import com.example.data.tables.UserTable
import com.example.data.tables.UserTypesTable
import domain.model.User
import domain.repository.UserRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {

    override fun create(user: User): Int = transaction {
        UserTable.insert {
            it[fio] = user.fio
            it[phone] = user.phone
            it[email] = user.email
            it[userTypeId] = user.userTypeId
            it[passwordHash] = user.passwordHash
        } get UserTable.id
    }

    override fun getAll(): List<User> = transaction {
        UserTable.selectAll().map {
            it.toUser()
        }
    }

    override fun getById(id: Int): User? = transaction {
        UserTable.selectAll().where { UserTable.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }

    override fun update(id: Int, user: User): Boolean = transaction {
        UserTable.update({ UserTable.id eq id }) {
            it[fio] = user.fio
            it[phone] = user.phone
            it[email] = user.email
            it[userTypeId] = user.userTypeId
            it[passwordHash] = user.passwordHash
        } > 0
    }

    override fun delete(id: Int): Boolean = transaction {
        UserTable.deleteWhere { UserTable.id eq id } > 0
    }

    private fun ResultRow.toUser() = User(
        id = this[UserTable.id],
        fio = this[UserTable.fio],
        phone = this[UserTable.phone],
        email = this[UserTable.email],
        userTypeId = this[UserTable.userTypeId],
        passwordHash = this[UserTable.passwordHash]
    )

    override fun findByEmail(email: String): User? = transaction {
        UserTable
            .selectAll()
            .where { UserTable.email eq email }
            .map {
                User(
                    id = it[UserTable.id],
                    fio = it[UserTable.fio],
                    phone = it[UserTable.phone],
                    email = it[UserTable.email],
                    userTypeId = it[UserTable.userTypeId],
                    passwordHash = it[UserTable.passwordHash]
                )
            }
            .singleOrNull()
    }

    override fun getRoleByUserId(userId: Int): String = transaction {

        (UserTable innerJoin UserTypesTable)
            .selectAll().where { UserTable.id eq userId }
            .single()[UserTypesTable.name]
    }
}