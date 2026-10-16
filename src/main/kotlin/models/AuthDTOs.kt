package com.models

import kotlinx.serialization.Serializable

@Serializable
data class UserRecord(
    val username: String,
    val password: String,
    val role: String,
    val nombre: String
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String? = null,
    val nombre: String? = null
)