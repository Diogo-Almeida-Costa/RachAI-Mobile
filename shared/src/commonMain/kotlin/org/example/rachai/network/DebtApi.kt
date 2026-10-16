package org.example.rachai.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put

class DebtApi(
    private val client: HttpClient = RachaiHttpClient.instance,
    private val baseUrl: String = apiBaseUrl
) {
    /** Recalcula e simplifica as dívidas do grupo a partir das despesas atuais. */
    suspend fun calculateDebts(groupId: Long): ApiResult<List<DebtResponse>> = safeCall {
        client.post("$baseUrl/rachai/debts/group/$groupId/calculate")
    }

    suspend fun getDebtsByGroup(groupId: Long): ApiResult<List<DebtResponse>> = safeCall {
        client.get("$baseUrl/rachai/debts/group/$groupId")
    }

    suspend fun settleDebt(debtId: Long): ApiResult<DebtResponse> = safeCall {
        client.put("$baseUrl/rachai/debts/$debtId/settle")
    }
}
