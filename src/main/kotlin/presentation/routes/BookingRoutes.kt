package presentation.routes

import io.ktor.server.routing.*
import io.ktor.server.response.*

fun Route.bookingRoutes() {

    route("/bookings") {

        get {
            call.respond("Bookings list")
        }
    }
}