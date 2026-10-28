package com.example.quotevault.data.repository

import com.example.quotevault.data.local.LocalStore
import com.example.quotevault.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val localStore: LocalStore,
) : SettingsRepository {
    override fun isDarkMode(): Boolean =
        localStore.getString(KEY_DARK_MODE) == "true"

    override fun setDarkMode(enabled: Boolean) {
        localStore.putString(KEY_DARK_MODE, enabled.toString())
    }

    private companion object {
        const val KEY_DARK_MODE = "dark_mode"
    }
}
