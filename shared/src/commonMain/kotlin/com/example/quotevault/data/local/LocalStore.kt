package com.example.quotevault.data.local

interface LocalStore {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
}

class MemoryLocalStore : LocalStore {
    private val data = mutableMapOf<String, String>()

    override fun getString(key: String): String? = data[key]

    override fun putString(key: String, value: String) {
        data[key] = value
    }
}

expect fun createLocalStore(): LocalStore
