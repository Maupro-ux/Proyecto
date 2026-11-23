package com.proyecto.gyrocampro.utils

object AuthSession {
    @Volatile
    var isAuthenticated: Boolean = false
        private set

    fun markAuthenticated() {
        isAuthenticated = true
    }

    fun clear() {
        isAuthenticated = false
    }
}
