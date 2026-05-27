package data.repository

import com.example.data.tables.CoachTable
import com.example.data.tables.UserTable
import com.example.data.tables.UserTable.email
import com.example.data.tables.UserTable.fio
import com.example.data.tables.UserTable.passwordHash
import com.example.data.tables.UserTable.phone
import com.example.data.tables.UserTable.userTypeId
import domain.model.User
import domain.repository.CoachRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class CoachRepositoryImpl: CoachRepository {

    override fun create(userId: Int, coachTypeId: Int): Int = transaction {
        CoachTable.insert {
            it[CoachTable.userId] = userId
            it[CoachTable.coachTypeId] = coachTypeId
        } get CoachTable.id
    }

    override fun getByUserId(userId: Int): Int? = transaction {
        CoachTable
            .selectAll().where { CoachTable.userId eq userId }
            .map { it[CoachTable.id] }
            .singleOrNull()
    }
}