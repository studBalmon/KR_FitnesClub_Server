package com.example.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object ClientTable : Table("clients") {

    val id = integer("id").autoIncrement()

    val userId = reference("user_id", UserTable.id)

    val cardEndDate = date("card_end_date")

    override val primaryKey = PrimaryKey(id)
}