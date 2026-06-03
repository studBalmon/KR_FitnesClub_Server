package domain.repository

import domain.model.Client
import java.time.LocalDate

/** Краткие данные клиента для аналитики: id = clients.id (совпадает с booking.clientIds). */
data class ClientBrief(
    val id: Int,
    val userId: Int,
    val fio: String,
    val cardEndDate: LocalDate
)

interface ClientRepository {

    fun create(userId: Int): Int

    fun getByUserId(userId: Int): Client?

    fun getById(clientId: Int): Client?

    fun getAllClients(): List<ClientBrief>

    /** Продлевает абонемент на [months] месяцев. Возвращает новую дату окончания или null, если клиент не найден. */
    fun extendByUserId(userId: Int, months: Int): LocalDate?

    fun deleteByUserId(userId: Int)
}