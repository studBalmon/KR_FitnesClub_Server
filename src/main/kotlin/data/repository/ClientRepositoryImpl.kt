package data.repository

import com.example.data.tables.ClientTable
import com.example.data.tables.UserTable
import domain.model.Client
import domain.repository.ClientBrief
import domain.repository.ClientRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class ClientRepositoryImpl : ClientRepository {

    override fun create(userId: Int): Int = transaction {
        ClientTable.insert {
            it[ClientTable.userId] = userId
            it[ClientTable.cardEndDate] = LocalDate.now().plusMonths(1)
        } get ClientTable.id
    }

    override fun getByUserId(userId: Int): Client? = transaction {
        ClientTable
            .selectAll()
            .where { ClientTable.userId eq userId }
            .map { it.toClient() }
            .singleOrNull()
    }

    override fun deleteByUserId(userId: Int) = transaction {
        ClientTable.deleteWhere { ClientTable.userId eq userId }
        Unit
    }

    override fun getById(clientId: Int): Client? = transaction {
        ClientTable
            .selectAll()
            .where { ClientTable.id eq clientId }
            .map { it.toClient() }
            .singleOrNull()
    }

    override fun getAllClients(): List<ClientBrief> = transaction {
        (ClientTable innerJoin UserTable).selectAll().map {
            ClientBrief(
                id = it[ClientTable.id],
                userId = it[ClientTable.userId],
                fio = it[UserTable.fio],
                cardEndDate = it[ClientTable.cardEndDate]
            )
        }
    }

    override fun extendByUserId(userId: Int, months: Int): LocalDate? = transaction {
        val current = ClientTable
            .selectAll()
            .where { ClientTable.userId eq userId }
            .map { it[ClientTable.cardEndDate] }
            .singleOrNull() ?: return@transaction null

        // если абонемент уже истёк — продлеваем от сегодня, иначе от текущей даты окончания
        val base = if (current.isBefore(LocalDate.now())) LocalDate.now() else current
        val newDate = base.plusMonths(months.toLong())

        ClientTable.update({ ClientTable.userId eq userId }) {
            it[cardEndDate] = newDate
        }
        newDate
    }

    private fun ResultRow.toClient() = Client(
        id = this[ClientTable.id],
        userId = this[ClientTable.userId],
        cardEndDate = this[ClientTable.cardEndDate]
    )
}