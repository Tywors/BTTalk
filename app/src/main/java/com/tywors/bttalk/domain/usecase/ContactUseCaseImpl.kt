package com.tywors.bttalk.domain.usecase

import android.content.Context
import com.tywors.bttalk.repository.room.dao.ContactDao
import com.tywors.bttalk.repository.room.entities.ContactEntity

class ContactUseCaseImpl(
    private val contactDao: ContactDao,
    private val context: Context,
) : ContactUseCase {

    override suspend fun createTemporalContact(id: String, ownKey: String): Long {
        return contactDao.insertTemporalContact(
            ContactEntity(
                id = id,
                ownKey = ownKey,
            )
        )
    }
}