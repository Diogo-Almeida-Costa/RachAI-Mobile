package org.example.rachai.network

import kotlinx.serialization.Serializable

/** Espelha ExpenseSplitRequestDTO (o "share" do backend é BigDecimal -> Double aqui) */
@Serializable
data class ExpenseSplitRequest(
    val debtorId: Long,
    val share: Double
)

/** Espelha ExpenseSplitResponseDTO */
@Serializable
data class ExpenseSplitResponse(
    val id: Long = 0,
    val debtorId: Long = 0,
    val debtorName: String = "",
    val share: Double = 0.0
)

/** Espelha ExpenseRequestDTO (POST/PUT /rachai/expenses) */
@Serializable
data class ExpenseRequest(
    val description: String,
    val amount: Double,
    val payerId: Long,
    val groupId: Long,
    val splits: List<ExpenseSplitRequest>
)

/** Espelha ExpenseResponseDTO */
@Serializable
data class ExpenseResponse(
    val id: Long = 0,
    val description: String = "",
    val amount: Double = 0.0,
    val payerId: Long = 0,
    val payerName: String = "",
    val groupId: Long = 0,
    val splits: List<ExpenseSplitResponse> = emptyList(),
    val createdAt: String? = null
)
