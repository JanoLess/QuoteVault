package com.example.quotevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: Int,
    val quote: String,
    val author: String,
)
