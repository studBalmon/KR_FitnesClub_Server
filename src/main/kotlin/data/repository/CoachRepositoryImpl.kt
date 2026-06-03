package data.repository

import com.example.data.tables.CoachTable
import com.example.data.tables.CoachTypeTable
import com.example.data.tables.UserTable
import domain.repository.CoachBrief
import domain.repository.CoachRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CoachRepositoryImpl : CoachRepository {

    override fun create(userId: Int, coachTypeId: Int): Int = transaction {
        CoachTable.insert {
            it[CoachTable.userId] = userId
            it[CoachTable.coachTypeId] = coachTypeId
        } get CoachTable.id
    }

    override fun getByUserId(userId: Int): Int? = transaction {
        CoachTable.selectAll()
            .where { CoachTable.userId eq userId }
            .map { it[CoachTable.id] }
            .singleOrNull()
    }

    override fun getCoachTypeByUserId(userId: Int): Int? = transaction {
        CoachTable.selectAll()
            .where { CoachTable.userId eq userId }
            .map { it[CoachTable.coachTypeId] }
            .singleOrNull()
    }

    override fun updateCoachType(userId: Int, newCoachTypeId: Int) = transaction {
        CoachTable.update({ CoachTable.userId eq userId }) {
            it[CoachTable.coachTypeId] = newCoachTypeId
        }
        Unit
    }

    override fun deleteByUserId(userId: Int) = transaction {
        CoachTable.deleteWhere { CoachTable.userId eq userId }
        Unit
    }

    override fun getAllCoaches(): List<CoachBrief> = transaction {
        val typeNames = CoachTypeTable.selectAll()
            .associate { it[CoachTypeTable.id] to it[CoachTypeTable.name] }
        (CoachTable innerJoin UserTable).selectAll().map {
            CoachBrief(
                id = it[CoachTable.id],
                userId = it[CoachTable.userId],
                fio = it[UserTable.fio],
                coachTypeName = typeNames[it[CoachTable.coachTypeId]]
            )
        }
    }

    override fun getAllCoachTypes(): List<Pair<Int, String>> = transaction {
        CoachTypeTable.selectAll()
            .map { it[CoachTypeTable.id] to it[CoachTypeTable.name] }
    }

    override fun createCoachTypeName(name: String): Int = transaction {
        CoachTypeTable.insert { it[CoachTypeTable.name] = name } get CoachTypeTable.id
    }

    override fun updateCoachTypeName(id: Int, name: String): Boolean = transaction {
        CoachTypeTable.update({ CoachTypeTable.id eq id }) {
            it[CoachTypeTable.name] = name
        } > 0
    }

    override fun deleteCoachTypeById(id: Int): Boolean = transaction {
        CoachTypeTable.deleteWhere { CoachTypeTable.id eq id } > 0
    }
}
