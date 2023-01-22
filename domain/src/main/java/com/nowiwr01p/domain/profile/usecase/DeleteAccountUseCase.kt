package com.nowiwr01p.domain.profile.usecase

import com.nowiwr01p.domain.UseCase
import com.nowiwr01p.domain.profile.repository.ProfileRepository

class DeleteAccountUseCase(
    private val repository: ProfileRepository
): UseCase<Unit, Unit> {

    override suspend fun execute(input: Unit) {
        repository.deleteAccount()
    }
}