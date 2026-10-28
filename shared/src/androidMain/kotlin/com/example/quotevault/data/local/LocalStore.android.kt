package com.example.quotevault.data.local

import android.content.Context

private var androidAppContext: Context? = null

fun initAndroidLocalStore(context: Context) {
    androidAppContext = context.applicationContext
}

actual fun createLocalStore(): LocalStore {
    val context = androidAppContext
    return if (context != null) {
        AndroidLocalStore(context)
    } else {
        MemoryLocalStore()
    }
}

private class AndroidLocalStore(context: Context) : LocalStore {
    private val prefs = context.getSharedPreferences("quotevault", Context.MODE_PRIVATE)

    override fun getString(key: String): String? = prefs.getString(key, null)

    override fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}
