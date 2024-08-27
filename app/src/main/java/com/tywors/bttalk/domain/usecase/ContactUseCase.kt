package com.tywors.bttalk.domain.usecase

interface ContactUseCase {
    suspend fun createTemporalContact(
        id: String,
        ownKey: String,
    ): Long
}