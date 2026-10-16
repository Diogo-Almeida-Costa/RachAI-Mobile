package org.example.rachai.network

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GroupApi(
    private val client: HttpClient = RachaiHttpClient.instance,
    private val baseUrl: String = apiBaseUrl
) {
    suspend fun getMyGroups(): ApiResult<List<GroupResponse>> {
        val memberResult = safeCall<List<GroupResponse>> {
            client.get("$baseUrl/rachai/groups/member")
        }
        if (memberResult is ApiResult.Success && memberResult.data.isNotEmpty()) {
            return memberResult
        }

        // Fallback: se member veio vazio, busca também por grupos criados pelo usuário (owner)
        val ownerResult = safeCall<List<GroupResponse>> {
            client.get("$baseUrl/rachai/groups/owner")
        }
        if (ownerResult is ApiResult.Success && ownerResult.data.isNotEmpty()) {
            val list1 = (memberResult as? ApiResult.Success)?.data.orEmpty()
            val list2 = ownerResult.data
            return ApiResult.Success((list1 + list2).distinctBy { it.id })
        }

        if (memberResult is ApiResult.Success) {
            return memberResult
        }
        if (ownerResult is ApiResult.Success) {
            return ownerResult
        }
        return memberResult
    }

    suspend fun getGroupById(id: Long): ApiResult<GroupResponse> = safeCall {
        client.get("$baseUrl/rachai/groups/$id")
    }

    suspend fun createGroup(name: String, description: String?): ApiResult<GroupResponse> = safeCall {
        client.post("$baseUrl/rachai/groups") {
            contentType(ContentType.Application.Json)
            setBody(GroupRequest(name = name, description = description))
        }
    }

    suspend fun updateGroup(id: Long, name: String, description: String?): ApiResult<GroupResponse> = safeCall {
        client.put("$baseUrl/rachai/groups/$id") {
            contentType(ContentType.Application.Json)
            setBody(GroupRequest(name = name, description = description))
        }
    }

    suspend fun deleteGroup(id: Long): ApiResult<Unit> = safeStatusCall {
        client.delete("$baseUrl/rachai/groups/$id")
    }

    suspend fun addMember(groupId: Long, memberId: Long): ApiResult<GroupResponse> = safeCall {
        client.post("$baseUrl/rachai/groups/$groupId/members/$memberId")
    }

    suspend fun removeMember(groupId: Long, memberId: Long): ApiResult<GroupResponse> = safeCall {
        client.delete("$baseUrl/rachai/groups/$groupId/members/$memberId")
    }
}
