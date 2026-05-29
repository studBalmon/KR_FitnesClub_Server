package data.repository

import com.example.data.tables.ClientTable
import domain.model.Client
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

    private fun ResultRow.toClient() = Client(
        id = this[ClientTable.id],
        userId = this[ClientTable.userId],
        cardEndDate = this[ClientTable.cardEndDate]
    )
}