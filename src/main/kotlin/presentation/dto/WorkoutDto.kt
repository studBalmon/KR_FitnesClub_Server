package presentation.dto

data class WorkoutRequest(
    val coachTypeId: Int,
    val name: String,
    val description: String?,
    val duration: Int
)