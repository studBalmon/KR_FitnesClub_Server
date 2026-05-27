package data.repository

import com.example.data.tables.BookingTable
import data.tables.BookingClientTable
import domain.model.Booking
import domain.repository.BookingRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class BookingRepositoryImpl : BookingRepository {

    override fun create(booking: Booking): Int = transaction {
        BookingTable.insert {
            it[coachId] = booking.coachId
            it[workoutId] = booking.workoutId
            it[slots] = booking.slots
            it[name] = booking.name
            it[extra] = booking.extra
            it[time] = booking.time
        } get BookingTable.id
    }

    override fun getAll(): List<Booking> = transaction {

        val clientsMap = BookingClientTable
            .selectAll()
            .groupBy { it[BookingClientTable.bookingId] }
            .mapValues { entry ->
                entry.value.map { it[BookingClientTable.clientId] }
            }

        BookingTable.selectAll().map { row ->
            row.toBooking(
                clients = clientsMap[row[BookingTable.id]] ?: emptyList()
            )
        }
    }

    override fun getById(id: Int): Booking? = transaction {

        val clients = BookingClientTable
            .selectAll().where { BookingClientTable.bookingId eq id }
            .map { it[BookingClientTable.clientId] }

        BookingTable
            .selectAll().where { BookingTable.id eq id }
            .map { it.toBooking(clients) }
            .singleOrNull()
    }

    override fun getByClient(clientId: Int): List<Booking> = transaction {

        val bookingIds = BookingClientTable
            .selectAll().where { BookingClientTable.clientId eq clientId }
            .map { it[BookingClientTable.bookingId] }

        val clientsMap = BookingClientTable
            .selectAll()
            .groupBy { it[BookingClientTable.bookingId] }
            .mapValues { entry ->
                entry.value.map { it[BookingClientTable.clientId] }
            }

        BookingTable
            .selectAll().where { BookingTable.id inList bookingIds }
            .map { it.toBooking(clientsMap[it[BookingTable.id]] ?: emptyList()) }
    }

    override fun getByCoach(coachId: Int): List<Booking> = transaction {

        val clientsMap = BookingClientTable
            .selectAll()
            .groupBy { it[BookingClientTable.bookingId] }
            .mapValues { entry ->
                entry.value.map { it[BookingClientTable.clientId] }
            }

        BookingTable
            .selectAll().where { BookingTable.coachId eq coachId }
            .map { it.toBooking(clientsMap[it[BookingTable.id]] ?: emptyList()) }
    }

    override fun delete(id: Int): Boolean = transaction {
        BookingClientTable.deleteWhere { BookingClientTable.bookingId eq id }
        BookingTable.deleteWhere { BookingTable.id eq id } > 0
    }

    override fun addClientToBooking(bookingId: Int, clientId: Int): Boolean = transaction {

        val exists = BookingClientTable.selectAll().where {
            (BookingClientTable.bookingId eq bookingId) and
                    (BookingClientTable.clientId eq clientId)
        }.count() > 0

        if (exists) return@transaction false

        val currentCount = BookingClientTable
            .selectAll().where { BookingClientTable.bookingId eq bookingId }
            .count()

        val slots = BookingTable
            .selectAll().where { BookingTable.id eq bookingId }
            .single()[BookingTable.slots]

        if (currentCount >= slots) return@transaction false

        BookingClientTable.insert {
            it[BookingClientTable.bookingId] = bookingId
            it[BookingClientTable.clientId] = clientId
        }

        true
    }

    private fun ResultRow.toBooking(clients: List<Int>) = Booking(
        id = this[BookingTable.id],
        coachId = this[BookingTable.coachId],
        workoutId = this[BookingTable.workoutId],
        slots = this[BookingTable.slots],
        name = this[BookingTable.name],
        extra = this[BookingTable.extra],
        time = this[BookingTable.time],
        clients = clients
    )
}