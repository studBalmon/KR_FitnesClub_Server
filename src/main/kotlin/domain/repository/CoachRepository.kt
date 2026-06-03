package domain.repository

/** Краткие данные тренера для аналитики: id = coaches.id (совпадает с booking.coachId). */
data class CoachBrief(
    val id: Int,
    val userId: Int,
    val fio: String,
    val coachTypeName: String?
)

interface CoachRepository {
    fun create(userId: Int, coachTypeId: Int): Int
    fun getByUserId(userId: Int): Int?
    fun getCoachTypeByUserId(userId: Int): Int?
    fun updateCoachType(userId: Int, newCoachTypeId: Int)
    fun deleteByUserId(userId: Int)
    fun getAllCoaches(): List<CoachBrief>
    fun getAllCoachTypes(): List<Pair<Int, String>>
    fun createCoachTypeName(name: String): Int
    fun updateCoachTypeName(id: Int, name: String): Boolean
    fun deleteCoachTypeById(id: Int): Boolean
}