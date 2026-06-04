package domain.repository

import domain.model.Client
import java.time.LocalDate

data class ClientBrief(
    val id: Int,
    val userId: Int,
    val fio: String,
    val cardEndDate: LocalDate,
    val phone: String = ""
)

interface ClientRepository {

    fun create(userId: Int): Int

    fun getByUserId(userId: Int): Client?

    fun getById(clientId: Int): Client?

    fun getAllClients(): List<ClientBrief>

    fun extendByUserId(userId: Int, months: Int): LocalDate?

    fun deleteByUserId(userId: Int)
}