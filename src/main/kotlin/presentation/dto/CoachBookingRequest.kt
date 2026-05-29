package presentation.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class CoachBookingRequest(
    val name: String,
    val slots: Int,
    val extra: String?,
    @Contextual val time: LocalDateTime
)
