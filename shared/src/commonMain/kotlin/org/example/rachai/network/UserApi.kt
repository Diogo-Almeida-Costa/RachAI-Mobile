package org.example.rachai.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class UserApi(
    private val client: HttpClient = RachaiHttpClient.instance,
    private val baseUrl: String = apiBaseUrl
) {
    suspend fun getMyProfile(): ApiResult<UserProfile> = safeCall {
        client.get("$baseUrl/rachai/users/me")
    }

    suspend fun updateMyProfile(profile: UserProfile): ApiResult<UserProfile> = safeCall {
        client.put("$baseUrl/rachai/users/me") {
            contentType(ContentType.Application.Json)
            setBody(profile)
        }
    }

    suspend fun listAllUsers(): ApiResult<List<UserSummary>> = safeCall {
        client.get("$baseUrl/rachai/users")
    }

    suspend fun listFriends(): ApiResult<List<FriendshipSummary>> = safeCall {
        client.get("$baseUrl/rachai/users/friends")
    }

    suspend fun addFriend(friendId: Long): ApiResult<FriendshipSummary> = safeCall {
        client.post("$baseUrl/rachai/users/friends/$friendId")
    }
}


internal suspend inline fun <reified T> safeCall(
    crossinline block: suspend () -> HttpResponse
): ApiResult<T> {
    return runCatching {
        val response = block()
        if (response.status.isSuccess()) {
            ApiResult.Success(response.body<T>())
        } else {
            ApiResult.Error(response.extractErrorMessage())
        }
    }.getOrElse { throwable ->
        ApiResult.Error(throwable.toFriendlyMessage())
    }
}


internal suspend inline fun safeStatusCall(
    crossinline block: suspend () -> HttpResponse
): ApiResult<Unit> {
    return runCatching {
        val response = block()
        if (response.status.isSuccess()) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(response.extractErrorMessage())
        }
    }.getOrElse { throwable ->
        ApiResult.Error(throwable.toFriendlyMessage())
    }
}

internal suspend fun HttpResponse.extractErrorMessage(): String {
    return runCatching {
        body<ApiErrorResponse>().message.takeIf { it.isNotBlank() }
    }.getOrNull() ?: "Erro inesperado (HTTP ${status.value})"
}

internal fun Throwable.toFriendlyMessage(): String = when (this) {
    is HttpRequestTimeoutException -> "O servidor demorou demais para responder"
    else -> message ?: "Não foi possível conectar ao servidor"
}
