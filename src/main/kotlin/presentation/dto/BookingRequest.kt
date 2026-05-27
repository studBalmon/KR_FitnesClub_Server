package presentation.dto

import kotlinx.serialization.Contextual
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class BookingRequest(
    val coachId: Int,
    val workoutId: Int,
    val slots: Int,
    val name: String,
    val extra: String?,
    @Contextual
    val time: LocalDateTime
)