package com.example.quotevault

class IosPlatform : Platform {
    override val name: String = "iOS"
}

actual fun getPlatform(): Platform = IosPlatform()
