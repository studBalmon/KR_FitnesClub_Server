package com.example.data.seed

import com.example.data.tables.BookingTable
import com.example.data.tables.ClientTable
import com.example.data.tables.CoachTable
import com.example.data.tables.CoachTypeTable
import com.example.data.tables.UserTable
import com.example.data.tables.WorkoutTable
import com.example.util.PasswordHasher
import data.tables.BookingClientTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

object TestDataSeeder {

    private const val EMAIL_DOMAIN = "@test.fitpoint"
    private const val PREFIX = "[ТЕСТ] "
    private const val PASSWORD = "test1234"

    fun isPresent(): Boolean = transaction {
        UserTable.selectAll().where { UserTable.email like "%$EMAIL_DOMAIN" }.count() > 0
    }

    fun seed() = transaction {
        val ctStrength = CoachTypeTable.insert { it[name] = PREFIX + "Силовые" } get CoachTypeTable.id
        val ctCardio   = CoachTypeTable.insert { it[name] = PREFIX + "Кардио" } get CoachTypeTable.id

        val coachAUser = insertUser("Иван Силовиков (тест)", "+79000000001", "coach1$EMAIL_DOMAIN", 2)
        val coachA = CoachTable.insert {
            it[CoachTable.userId] = coachAUser; it[CoachTable.coachTypeId] = ctStrength
        } get CoachTable.id

        val coachBUser = insertUser("Пётр Кардиев (тест)", "+79000000002", "coach2$EMAIL_DOMAIN", 2)
        val coachB = CoachTable.insert {
            it[CoachTable.userId] = coachBUser; it[CoachTable.coachTypeId] = ctCardio
        } get CoachTable.id

        val wBench = insertWorkout(ctStrength, "Жим лёжа", 60)
        val wSquat = insertWorkout(ctStrength, "Приседания", 90)
        val wRun   = insertWorkout(ctCardio,   "Беговая дорожка", 45)
        val wBike  = insertWorkout(ctCardio,   "Велотренажёр", 30)

        val today = LocalDate.now()
        val clientDefs = listOf(
            ClientDef("Анна Активова (тест)",  "client1", today.plusDays(90)),
            ClientDef("Борис Активов (тест)",  "client2", today.plusDays(30)),
            ClientDef("Вера Годовая (тест)",   "client3", today.plusDays(200)),
            ClientDef("Глеб Скоро (тест)",     "client4", today.plusDays(3)),   // истекает < 7 дней
            ClientDef("Дарья Почти (тест)",    "client5", today.plusDays(6)),   // истекает < 7 дней
            ClientDef("Егор Просрочен (тест)", "client6", today.minusDays(5))   // истёк
        )
        val clientIds = clientDefs.mapIndexed { i, def ->
            val uid = insertUser(def.fio, "+7910000000${i + 1}", "${def.login}$EMAIL_DOMAIN", 3)
            ClientTable.insert {
                it[ClientTable.userId] = uid
                it[ClientTable.cardEndDate] = def.cardEnd
            } get ClientTable.id
        }

        val bookingDefs = listOf(
            BDef(coachA, wBench, 10,  0,  9, 8),
            BDef(coachA, wSquat,  8,  0, 18, 8),   // 100%
            BDef(coachB, wRun,   10,  1, 10, 3),
            BDef(coachB, wBike,   6,  1, 19, 0),   // пусто
            BDef(coachA, wBench, 12,  2,  8, 6),
            BDef(coachB, wRun,   10,  3, 11, 9),
            BDef(coachA, wSquat,  5,  4, 17, 5),   // 100%
            BDef(coachB, wBike,   8,  5, 20, 4),
            BDef(coachA, wBench, 10,  6, 12, 7),
            BDef(coachB, wRun,   10,  7,  9, 2),
            BDef(coachA, wSquat,  8, -2, 10, 6),   // прошедшее
            BDef(coachB, wBike,   6, -1, 18, 3)    // прошедшее
        )
        bookingDefs.forEachIndexed { idx, d ->
            val time = today.plusDays(d.dayOffset.toLong()).atTime(d.hour, 0)
            val bookingId = BookingTable.insert {
                it[BookingTable.coachId]   = d.coachId
                it[BookingTable.workoutId] = d.workoutId
                it[BookingTable.slots]     = d.slots
                it[BookingTable.name]      = PREFIX + "Занятие ${idx + 1}"
                it[BookingTable.extra]     = null
                it[BookingTable.time]      = time
            } get BookingTable.id

            clientIds.take(d.enroll.coerceAtMost(d.slots)).forEach { cId ->
                BookingClientTable.insert {
                    it[BookingClientTable.bookingId] = bookingId
                    it[BookingClientTable.clientId]  = cId
                }
            }
        }
        Unit
    }

    fun clear() = transaction {
        val testUserIds    = UserTable.selectAll().where { UserTable.email like "%$EMAIL_DOMAIN" }
            .map { it[UserTable.id] }
        val testClientIds  = ClientTable.selectAll().where { ClientTable.userId inList testUserIds }
            .map { it[ClientTable.id] }
        val testBookingIds = BookingTable.selectAll().where { BookingTable.name like "$PREFIX%" }
            .map { it[BookingTable.id] }

        if (testBookingIds.isNotEmpty())
            BookingClientTable.deleteWhere { BookingClientTable.bookingId inList testBookingIds }
        if (testClientIds.isNotEmpty())
            BookingClientTable.deleteWhere { BookingClientTable.clientId inList testClientIds }

        BookingTable.deleteWhere { BookingTable.name like "$PREFIX%" }

        if (testUserIds.isNotEmpty()) {
            ClientTable.deleteWhere { ClientTable.userId inList testUserIds }
            CoachTable.deleteWhere { CoachTable.userId inList testUserIds }
        }

        WorkoutTable.deleteWhere { WorkoutTable.name like "$PREFIX%" }
        CoachTypeTable.deleteWhere { CoachTypeTable.name like "$PREFIX%" }

        if (testUserIds.isNotEmpty())
            UserTable.deleteWhere { UserTable.id inList testUserIds }
        Unit
    }

    private fun insertUser(fio: String, phone: String, email: String, typeId: Int): Int =
        UserTable.insert {
            it[UserTable.fio] = fio
            it[UserTable.phone] = phone
            it[UserTable.email] = email
            it[UserTable.userTypeId] = typeId
            it[UserTable.passwordHash] = PasswordHasher.hash(PASSWORD)
        } get UserTable.id

    private fun insertWorkout(coachTypeId: Int, name: String, duration: Int): Int =
        WorkoutTable.insert {
            it[WorkoutTable.coachTypeId] = coachTypeId
            it[WorkoutTable.name] = PREFIX + name
            it[WorkoutTable.description] = "Тестовое занятие"
            it[WorkoutTable.duration] = duration
        } get WorkoutTable.id

    private data class ClientDef(val fio: String, val login: String, val cardEnd: LocalDate)
    private data class BDef(
        val coachId: Int, val workoutId: Int, val slots: Int,
        val dayOffset: Int, val hour: Int, val enroll: Int
    )
}
