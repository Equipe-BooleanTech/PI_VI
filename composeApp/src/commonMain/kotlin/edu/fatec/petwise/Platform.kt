package edu.fatec.petwise

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform