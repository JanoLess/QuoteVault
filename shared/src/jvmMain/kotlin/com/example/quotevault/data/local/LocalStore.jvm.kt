package com.example.quotevault.data.local

import java.io.File

actual fun createLocalStore(): LocalStore = FileLocalStore()

private class FileLocalStore : LocalStore {
    private val dir = File(System.getProperty("user.home"), ".quotevault")

    override fun getString(key: String): String? {
        val file = fileFor(key)
        return if (file.exists()) file.readText() else null
    }

    override fun putString(key: String, value: String) {
        dir.mkdirs()
        fileFor(key).writeText(value)
    }

    private fun fileFor(key: String): File {
        val safeName = key.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return File(dir, "$safeName.txt")
    }
}
