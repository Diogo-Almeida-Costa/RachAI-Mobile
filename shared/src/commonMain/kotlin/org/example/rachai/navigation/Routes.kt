package org.example.rachai.navigation

import kotlinx.serialization.Serializable

/**
 * Rotas tipadas do app (type-safe navigation). Cada rota é uma classe/objeto
 * serializável; os argumentos ficam explícitos no construtor.
 */
@Serializable
data object LoginRoute

@Serializable
data class RegisterRoute(val prefillEmail: String? = null)

@Serializable
data object GroupsRoute

@Serializable
data object CreateGroupRoute

@Serializable
data class GroupDetailRoute(val groupId: Long, val groupName: String)

@Serializable
data class AddExpenseRoute(val groupId: Long)

@Serializable
data class AddMemberRoute(val groupId: Long)

@Serializable
data object ProfileRoute
