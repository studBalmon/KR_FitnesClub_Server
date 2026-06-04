package presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: Int,
    val fio: String,
    val phone: String,
    val email: String,
    val cardEndDate: String?,  
    val userTypeId: Int        
)

@Serializable
data class PassTokenResponse(
    val token: String,
    val fio: String
)
