package domain.repository

import domain.model.Client

interface ClientRepository {

    fun create(userId: Int): Int

    fun getByUserId(userId: Int): Client?

}