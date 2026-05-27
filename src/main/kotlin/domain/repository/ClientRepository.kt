package domain.repository

import domain.model.Client

interface ClientRepository {

    fun getByUserId(userId: Int): Client?

}