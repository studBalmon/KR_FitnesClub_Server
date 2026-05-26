package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val id: Int,
    val coachTypeId: Int,
    val name: String,
    val description: String?,
    val duration: Int
)