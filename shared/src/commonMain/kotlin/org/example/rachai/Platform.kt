package org.example.rachai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform