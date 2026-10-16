package org.example.rachai.network

import kotlinx.serialization.Serializable

/** Espelha DebtResponseDTO */
@Serializable
data class DebtResponse(
    val id: Long = 0,
    val debtorId: Long = 0,
    val debtorName: String = "",
    val creditorId: Long = 0,
    val creditorName: String = "",
    val groupId: Long = 0,
    val amount: Double = 0.0,
    val settled: Boolean = false,
    val createdAt: String? = null
)
