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
        BookingTable.selectAll().map { row ->
            row.toBooking(
                clients = getClientsForBooking(row[BookingTable.id])
            )
        }
    }

    override fun getById(id: Int): Booking? = transaction {
        BookingTable.selectAll().where { BookingTable.id eq id }
            .map { row ->
                row.toBooking(
                    clients = getClientsForBooking(row[BookingTable.id])
                )
            }
            .singleOrNull()
    }

    override fun getByClient(clientId: Int): List<Booking> = transaction {

        (BookingTable innerJoin BookingClientTable)
            .selectAll().where { BookingClientTable.clientId eq clientId }
            .map { row ->
                row.toBooking(
                    clients = getClientsForBooking(row[BookingTable.id])
                )
            }
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

    private fun getClientsForBooking(bookingId: Int): List<Int> = transaction {
        BookingClientTable
            .selectAll().where { BookingClientTable.bookingId eq bookingId }
            .map { it[BookingClientTable.clientId] }
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