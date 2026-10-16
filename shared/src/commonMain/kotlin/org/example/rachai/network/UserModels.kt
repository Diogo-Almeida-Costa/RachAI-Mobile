package org.example.rachai.network

import kotlinx.serialization.Serializable

/** Espelha UserProfileDTO (GET/PUT /rachai/users/me) */
@Serializable
data class UserProfile(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val imageUrl: String? = null,
    val bio: String? = null
)

/** Espelha UserResponseDTO, usado em listagens (ex.: escolher amigos) */
@Serializable
data class UserSummary(
    val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val bio: String? = null,
    val createdAt: String? = null
)

/** Espelha FriendshipResponseDTO  */
@Serializable
data class FriendshipSummary(
    val id: Long = 0,
    val friend: UserProfile? = null
)
