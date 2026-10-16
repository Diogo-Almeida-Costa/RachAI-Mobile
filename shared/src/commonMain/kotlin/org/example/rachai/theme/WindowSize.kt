package org.example.rachai.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

/** Três classes de tamanho de janela para layout adaptativo. */
enum class WindowSizeClass { Compact, Medium, Expanded }

/**
 * CompositionLocal que fornece o [WindowSizeClass] atual.
 * Provido por [RachaiTheme]; consumido por qualquer composable filho.
 */
val LocalWindowSizeClass = staticCompositionLocalOf { WindowSizeClass.Compact }

/**
 * Retorna a [WindowSizeClass] calculada com base na largura atual da janela.
 * Limiares: < 600 dp → Compact, 600–839 dp → Medium, ≥ 840 dp → Expanded.
 */
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val config = LocalConfiguration.current
    return when {
        config.screenWidthDp >= 840 -> WindowSizeClass.Expanded
        config.screenWidthDp >= 600 -> WindowSizeClass.Medium
        else -> WindowSizeClass.Compact
    }
}
