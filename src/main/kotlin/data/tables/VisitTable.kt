package com.example.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object VisitTable : Table("visits") {

    val id = integer("id").autoIncrement()

    val userId = reference("user_id", UserTable.id)

    val entryTime = datetime("entry_time")

    val exitTime = datetime("exit_time").nullable()

    override val primaryKey = PrimaryKey(id)
}
