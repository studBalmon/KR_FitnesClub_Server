package data.repository

import com.example.data.tables.ClientTable
import domain.model.Client
import domain.repository.ClientRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ClientRepositoryImpl : ClientRepository {

    override fun getByUserId(userId: Int): Client? = transaction {
        ClientTable
            .selectAll()
            .where { ClientTable.userId eq userId }
            .map { it.toClient() }
            .singleOrNull()
    }

    private fun ResultRow.toClient() = Client(
        id = this[ClientTable.id],
        userId = this[ClientTable.userId],
        cardEndDate = this[ClientTable.cardEndDate]
    )
}