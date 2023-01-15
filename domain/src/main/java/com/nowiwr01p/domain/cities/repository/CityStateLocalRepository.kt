package com.nowiwr01p.domain.cities.repository

import com.nowiwr01p.core.datastore.cities.data.City

interface CityStateLocalRepository {
    suspend fun getCity(): City
    suspend fun setCity(city: City)
}