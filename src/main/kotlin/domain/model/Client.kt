package domain.model

import java.time.LocalDate

data class Client(
    val id: Int,
    val userId: Int,
    val cardEndDate: LocalDate
)