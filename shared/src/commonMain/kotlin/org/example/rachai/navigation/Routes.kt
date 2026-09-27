package org.example.rachai.navigation

import kotlinx.serialization.Serializable

/**
 * Rotas tipadas do app, usadas com Navigation Compose (type-safe navigation).
 * Cada rota é uma classe/objeto serializável; os argumentos ficam explícitos
 * no construtor em vez de serem passados como strings soltas no NavHost.
 */
@Serializable
data object LoginRoute

@Serializable
data class RegisterRoute(
    val prefillEmail: String? = null
)

@Serializable
data class HomeRoute(
    val userEmail: String
)
