package com.example.presentation.routes

import com.example.data.repository.UserRepositoryImpl
import domain.model.User
import presentation.dto.CreateUserRequest
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import presentation.util.role

fun Route.userRoutes() {

    val repo = UserRepositoryImpl()

    route("/users") {

        post {
            val request = call.receive<CreateUserRequest>()

            val userId = repo.create(
                User(
                    id = 0,
                    fio = request.fio,
                    phone = request.phone,
                    email = request.email,
                    userTypeId = request.userTypeId,
                    passwordHash = request.password
                )
            )

            call.respond(mapOf("id" to userId))
        }

        get {
            call.respond(repo.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val user = repo.getById(id)

            if (user != null)
                call.respond(user)
            else
                call.respondText("User not found")
        }

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

        delete("/{id}") {
            val role = call.role()

            if (role != "ADMIN") {
                call.respond(mapOf("error" to "Forbidden"))
                return@delete
            }
            val id = call.parameters["id"]!!.toInt()
            val deleted = repo.delete(id)

            call.respond(mapOf("deleted" to deleted))
        }
    }
}