package com.nowiwr01p.domain.location.usecase.local

import com.nowiwr01p.core.datastore.location.data.Country
import com.nowiwr01p.domain.UseCase
import com.nowiwr01p.domain.location.repository.LocationDataStoreRepository

class IsCountrySetUseCase(
    private val repository: LocationDataStoreRepository
): UseCase<Unit, Country> {

    override suspend fun execute(input: Unit): Country {
        return repository.isCountrySet()
    }
}