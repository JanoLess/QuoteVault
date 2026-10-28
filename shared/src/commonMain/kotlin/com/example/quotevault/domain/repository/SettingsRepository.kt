package com.example.quotevault.domain.repository

interface SettingsRepository {
    fun isDarkMode(): Boolean
    fun setDarkMode(enabled: Boolean)
}
