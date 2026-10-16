package org.example.rachai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

private val AvatarPalette = listOf(
    Color(0xFF1F9D7C), Color(0xFFE8604C), Color(0xFFE2A63B),
    Color(0xFF4C7FE8), Color(0xFF8E5AE0), Color(0xFF2AA9A0)
)

private fun colorFor(seed: String): Color {
    if (seed.isEmpty()) return AvatarPalette.first()
    return AvatarPalette[abs(seed.hashCode()) % AvatarPalette.size]
}

private fun initialsOf(firstName: String, lastName: String): String {
    val a = firstName.trim().firstOrNull()?.uppercaseChar() ?: ' '
    val b = lastName.trim().firstOrNull()?.uppercaseChar() ?: ' '
    val combined = "$a$b".trim()
    return combined.ifEmpty { "?" }
}

/**
 * Avatar circular com iniciais do usuário.
 *
 * @param contentDescription Descrição acessível anunciada pelo leitor de tela.
 *   Por padrão usa o nome completo do usuário.
 */
@Composable
fun RachaiAvatar(
    firstName: String,
    lastName: String = "",
    size: Dp = 40.dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = "$firstName $lastName".trim().ifEmpty { null }
) {
    val background = colorFor("$firstName$lastName")
    val semanticsModifier = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .background(background, CircleShape)
            .then(semanticsModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initialsOf(firstName, lastName),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = (size.value * 0.38f).sp
        )
    }
}
