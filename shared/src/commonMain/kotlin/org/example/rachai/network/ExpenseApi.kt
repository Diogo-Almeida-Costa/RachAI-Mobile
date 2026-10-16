package org.example.rachai.network

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ExpenseApi(
    private val client: HttpClient = RachaiHttpClient.instance,
    private val baseUrl: String = apiBaseUrl
) {
    suspend fun getExpensesByGroup(groupId: Long): ApiResult<List<ExpenseResponse>> = safeCall {
        client.get("$baseUrl/rachai/expenses/group/$groupId")
    }

    suspend fun getExpenseById(expenseId: Long): ApiResult<ExpenseResponse> = safeCall {
        client.get("$baseUrl/rachai/expenses/$expenseId")
    }

    suspend fun createExpense(request: ExpenseRequest): ApiResult<ExpenseResponse> = safeCall {
        client.post("$baseUrl/rachai/expenses") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun updateExpense(expenseId: Long, request: ExpenseRequest): ApiResult<ExpenseResponse> = safeCall {
        client.put("$baseUrl/rachai/expenses/$expenseId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun deleteExpense(expenseId: Long): ApiResult<Unit> = safeStatusCall {
        client.delete("$baseUrl/rachai/expenses/$expenseId")
    }
}
