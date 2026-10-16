package org.example.rachai.network

import kotlinx.serialization.Serializable

/** Espelha GroupRequestDTO (POST/PUT /rachai/groups) */
@Serializable
data class GroupRequest(
    val name: String,
    val description: String? = null
)

/** Espelha GroupResponseDTO */
@Serializable
data class GroupResponse(
    val id: Long = 0,
    val name: String = "",
    val description: String? = null,
    val owner: UserProfile? = null,
    val members: Set<UserProfile> = emptySet()
)
