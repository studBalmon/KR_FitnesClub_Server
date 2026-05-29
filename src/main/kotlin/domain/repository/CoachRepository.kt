package domain.repository

interface CoachRepository {
    fun create(userId: Int, coachTypeId: Int): Int
    fun getByUserId(userId: Int): Int?
    fun getCoachTypeByUserId(userId: Int): Int?
    fun updateCoachType(userId: Int, newCoachTypeId: Int)
    fun deleteByUserId(userId: Int)
    fun getAllCoachTypes(): List<Pair<Int, String>>
    fun createCoachTypeName(name: String): Int
    fun updateCoachTypeName(id: Int, name: String): Boolean
    fun deleteCoachTypeById(id: Int): Boolean
}