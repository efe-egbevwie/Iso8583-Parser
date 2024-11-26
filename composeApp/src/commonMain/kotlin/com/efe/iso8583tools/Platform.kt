package com.efe.iso8583tools

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform