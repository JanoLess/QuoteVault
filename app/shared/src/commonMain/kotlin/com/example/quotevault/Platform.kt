package com.example.quotevault

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform