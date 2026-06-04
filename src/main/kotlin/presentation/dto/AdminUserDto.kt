package presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class AdminUserResponse(
    val id: Int,
    val fio: String,
    val phone: String,
    val email: String,
    val userTypeId: Int,
    val roleName: String,
    val coachTypeId: Int?,
    val cardEndDate: String? = null   // только для клиентов, ISO-дата
)

@Serializable
data class ExtendSubscriptionRequest(
    val months: Int
)

@Serializable
data class AdminCoachResponse(
    val id: Int,            // coaches.id (== booking.coachId)
    val userId: Int,
    val fio: String,
    val coachTypeName: String? = null
)

@Serializable
data class AdminClientResponse(
    val id: Int,            // clients.id (== booking.clientIds)
    val userId: Int,
    val fio: String,
    val cardEndDate: String
)

@Serializable
data class ScanRequest(val token: String, val force: Boolean = false)

@Serializable
data class ScanResponse(val action: String, val fio: String)   // "entered" | "exited" | "warn_quick_exit"

@Serializable
data class InsideVisitResponse(
    val userId: Int,
    val fio: String,
    val entryTime: String,
    val minutesInside: Long,
    val nextClassName: String? = null,   // ближайшее сегодняшнее занятие клиента
    val nextClassTime: String? = null    // время "HH:mm"
)

@Serializable
data class TestDataStatusResponse(val present: Boolean)

@Serializable
data class TestDataToggleResponse(val action: String, val message: String)

@Serializable
data class AdminCreateUserRequest(
    val fio: String,
    val phone: String,
    val email: String,
    val password: String,
    val userTypeId: Int,
    val coachTypeId: Int? = null
)

@Serializable
data class AdminUpdateUserRequest(
    val fio: String,
    val phone: String,
    val email: String,
    val newPassword: String? = null,
    val coachTypeId: Int? = null   // для тренеров
)

@Serializable
data class CoachTypeResponse(
    val id: Int,
    val name: String
)

@Serializable
data class CoachTypeRequest(val name: String)
