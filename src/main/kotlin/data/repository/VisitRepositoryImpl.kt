package data.repository

import com.example.data.tables.UserTable
import com.example.data.tables.VisitTable
import domain.repository.InsideVisit
import domain.repository.OpenVisit
import domain.repository.VisitRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class VisitRepositoryImpl : VisitRepository {

    override fun findOpenVisit(userId: Int): OpenVisit? = transaction {
        VisitTable.selectAll()
            .where { (VisitTable.userId eq userId) and VisitTable.exitTime.isNull() }
            .map { OpenVisit(it[VisitTable.id], it[VisitTable.entryTime]) }
            .firstOrNull()
    }

    override fun checkIn(userId: Int) = transaction {
        VisitTable.insert {
            it[VisitTable.userId] = userId
            it[VisitTable.entryTime] = LocalDateTime.now()
            it[VisitTable.exitTime] = null
        }
        Unit
    }

    override fun checkOut(visitId: Int) = transaction {
        VisitTable.update({ VisitTable.id eq visitId }) {
            it[exitTime] = LocalDateTime.now()
        }
        Unit
    }

    override fun getInside(): List<InsideVisit> = transaction {
        (VisitTable innerJoin UserTable).selectAll()
            .where { VisitTable.exitTime.isNull() }
            .map {
                InsideVisit(
                    userId = it[VisitTable.userId],
                    fio = it[UserTable.fio],
                    entryTime = it[VisitTable.entryTime]
                )
            }
    }

    override fun deleteByUser(userId: Int) = transaction {
        VisitTable.deleteWhere { VisitTable.userId eq userId }
        Unit
    }

    override fun autoCloseStale(thresholdHours: Long): Int = transaction {
        val cutoff = LocalDateTime.now().minusHours(thresholdHours)
        val stale = VisitTable.selectAll()
            .where { VisitTable.exitTime.isNull() and (VisitTable.entryTime less cutoff) }
            .map { it[VisitTable.id] to it[VisitTable.entryTime] }
        stale.forEach { (visitId, entry) ->
            VisitTable.update({ VisitTable.id eq visitId }) {
                it[exitTime] = entry.plusHours(thresholdHours)
            }
        }
        stale.size
    }
}
