package com.nowiwr01p.core.datastore

import android.content.Context
import androidx.datastore.dataStore
import com.nowiwr01p.core.datastore.auth.AuthSecurityWarningSerializer
import com.nowiwr01p.core.datastore.location.LocationPreferencesSerializer

enum class DataStoreType {
    AUTH_SECURITY,
    LOCATION
}

class AuthSecurityWarningDataStore(fileName: String) {

    private val Context.authSecurityWarningDataStore by dataStore(
        fileName = fileName,
        serializer = AuthSecurityWarningSerializer
    )

    fun create(context: Context) = context.authSecurityWarningDataStore
}

class LocationDataStore(fileName: String) {

    private val Context.locationDataStore by dataStore(
        fileName = fileName,
        serializer = LocationPreferencesSerializer
    )

    fun create(context: Context) = context.locationDataStore
}