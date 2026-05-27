package data.tables

import com.example.data.tables.BookingTable
import com.example.data.tables.ClientTable
import org.jetbrains.exposed.sql.Table

object BookingClientTable : Table("booking_clients") {

    val bookingId = reference("booking_id", BookingTable.id)

    val clientId = reference("client_id", ClientTable.id)

    override val primaryKey = PrimaryKey(bookingId, clientId)
}