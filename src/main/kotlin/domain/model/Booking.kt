package domain.model

import kotlinx.serialization.Contextual
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val id: Int,
    val coachId: Int,
    val workoutId: Int,
    val slots: Int,
    val name: String,
    val extra: String?,
    @Contextual
    val time: LocalDateTime,
    val clients: List<Int> = emptyList()
)