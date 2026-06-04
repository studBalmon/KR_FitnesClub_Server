package domain.repository

import domain.model.Booking

interface BookingRepository {

    fun create(booking: Booking): Int

    fun getAll(): List<Booking>

    fun getById(id: Int): Booking?

    fun getByClient(clientId: Int): List<Booking>

    fun delete(id: Int): Boolean

    fun addClientToBooking(bookingId: Int, clientId: Int): Boolean

    fun removeClientFromBooking(bookingId: Int, clientId: Int): Boolean

    fun removeAllByClient(clientId: Int): Int

    fun getByCoach(coachId: Int): List<Booking>

    fun update(booking: Booking): Boolean

    fun searchByName(query: String): List<Booking>
}