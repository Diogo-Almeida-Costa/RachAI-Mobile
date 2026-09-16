package org.example.rachai.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess


class AuthApi(
    private val client: HttpClient = RachaiHttpClient.instance,
    private val baseUrl: String = apiBaseUrl
) {

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        return runCatching {
            val response: HttpResponse = client.post("$baseUrl/rachai/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email = email, password = password))
            }

            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<LoginResponse>())
            } else {
                ApiResult.Error(extractErrorMessage(response))
            }
        }.getOrElse { throwable ->
            ApiResult.Error(throwable.toFriendlyMessage())
        }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        imageUrl: String? = null,
        bio: String? = null
    ): ApiResult<UserResponse> {
        return runCatching {
            val response: HttpResponse = client.post("$baseUrl/rachai/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(
                    RegisterRequest(
                        email = email,
                        password = password,
                        firstName = firstName,
                        lastName = lastName,
                        imageUrl = imageUrl,
                        bio = bio
                    )
                )
            }

            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<UserResponse>())
            } else {
                ApiResult.Error(extractErrorMessage(response))
            }
        }.getOrElse { throwable ->
            ApiResult.Error(throwable.toFriendlyMessage())
        }
    }


    private suspend fun extractErrorMessage(response: HttpResponse): String {
        return runCatching {
            response.body<ApiErrorResponse>().message.takeIf { it.isNotBlank() }
        }.getOrNull() ?: "Erro inesperado (HTTP ${response.status.value})"
    }

    private fun Throwable.toFriendlyMessage(): String = when (this) {
        is HttpRequestTimeoutException ->
            "O servidor demorou demais para responder"
        else ->
            message ?: "Não foi possível conectar ao servidor"
    }
}
