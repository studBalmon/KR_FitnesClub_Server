package com.example.presentation.routes

import com.example.data.repository.UserRepositoryImpl
import domain.model.User
import com.example.presentation.dto.CreateUserRequest
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes() {

    val repo = UserRepositoryImpl()

    route("/users") {

        // CREATE
        post {
            val request = call.receive<CreateUserRequest>()

            val userId = repo.create(
                User(
                    id = 0,
                    fio = request.fio,
                    phone = request.phone,
                    email = request.email,
                    userTypeId = request.userTypeId,
                    passwordHash = request.password // позже заменим на hash
                )
            )

            call.respond(mapOf("id" to userId))
        }

        // READ ALL
        get {
            call.respond(repo.getAll())
        }

        // READ BY ID
        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val user = repo.getById(id)

            if (user != null)
                call.respond(user)
            else
                call.respondText("User not found")
        }

        // UPDATE
        put("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val request = call.receive<CreateUserRequest>()

            val updated = repo.update(
                id,
                User(
                    id,
                    request.fio,
                    request.phone,
                    request.email,
                    request.userTypeId,
                    request.password
                )
            )

            call.respond(mapOf("updated" to updated))
        }

        // DELETE
        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val deleted = repo.delete(id)

            call.respond(mapOf("deleted" to deleted))
        }
    }
}