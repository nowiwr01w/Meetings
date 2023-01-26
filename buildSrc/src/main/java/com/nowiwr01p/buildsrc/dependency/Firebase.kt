package com.nowiwr01p.buildsrc.dependency

import com.nowiwr01p.buildsrc.dependency.Firebase.Version.AUTH_VERSION
import com.nowiwr01p.buildsrc.dependency.Firebase.Version.CONFIG_VERSION
import com.nowiwr01p.buildsrc.dependency.Firebase.Version.DATABASE_VERSION

object Firebase {

    const val AUTH = "com.google.firebase:firebase-auth-ktx:$AUTH_VERSION"
    const val CONFIG = "com.google.firebase:firebase-config-ktx:$CONFIG_VERSION"
    const val STORAGE = "com.google.firebase:firebase-storage-ktx:$DATABASE_VERSION"
    const val DATABASE = "com.google.firebase:firebase-database-ktx:$DATABASE_VERSION"

    private object Version {
        const val AUTH_VERSION = "21.1.0"
        const val CONFIG_VERSION = "21.2.1"
        const val DATABASE_VERSION = "20.1.0"
    }
}