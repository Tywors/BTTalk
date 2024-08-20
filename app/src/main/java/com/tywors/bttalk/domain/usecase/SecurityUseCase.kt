package com.tywors.bttalk.domain.usecase

interface SecurityUseCase {

    suspend fun generateRandomKey(length: Int): String
}