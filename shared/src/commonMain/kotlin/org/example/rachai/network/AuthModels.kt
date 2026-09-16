package org.example.rachai.network

import kotlinx.serialization.Serializable

/** Espelha AuthenticationRequestDTO (POST /rachai/auth/login) */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/** O endpoint de login responde com o JWT: {"token": "..."} */
@Serializable
data class LoginResponse(
    val token: String
)

/** Espelha RegisterRequestDTO (POST /rachai/auth/register) */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String? = null,
    val bio: String? = null
)

/** Espelha UserResponseDTO, retornado no cadastro bem-sucedido */
@Serializable
data class UserResponse(
    val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val bio: String? = null,
    val createdAt: String? = null
)

/** Espelha o ErrorResponse do GlobalExceptionHandler, retornado em erros */
@Serializable
data class ApiErrorResponse(
    val status: Int = 0,
    val message: String = "",
    val timestamp: String? = null
)
