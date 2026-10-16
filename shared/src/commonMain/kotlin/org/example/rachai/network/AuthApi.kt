package org.example.rachai.network

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(
    private val client: HttpClient = RachaiHttpClient.instance,
    private val baseUrl: String = apiBaseUrl
) {

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> = safeCall {
        client.post("$baseUrl/rachai/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = email, password = password))
        }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        imageUrl: String? = null,
        bio: String? = null
    ): ApiResult<UserResponse> = safeCall {
        client.post("$baseUrl/rachai/auth/register") {
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
    }
}
