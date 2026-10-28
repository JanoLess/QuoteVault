package com.example.quotevault.data.local

import platform.Foundation.NSUserDefaults

actual fun createLocalStore(): LocalStore = IosLocalStore()

private class IosLocalStore : LocalStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getString(key: String): String? = defaults.stringForKey(key)

    override fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }
}
