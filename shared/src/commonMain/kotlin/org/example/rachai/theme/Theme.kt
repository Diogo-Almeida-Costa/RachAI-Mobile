package org.example.rachai.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


object RachaiColors {
    val Jade = Color(0xFF22C55E)
    val JadeDark = Color(0xFF16A34A)
    val JadeContainerLight = Color(0xFFD4F3E7)
    val Coral = Color(0xFFE8604C)
    val CoralContainerLight = Color(0xFFFCDCD6)
    val Amber = Color(0xFFE2A63B)

    val BackgroundLight = Color(0xFFF7F8FA)
    val SurfaceLight = Color(0xFFFFFFFF)
    val OutlineLight = Color(0xFFE1E4E8)
    val TextPrimaryLight = Color(0xFF16211D)
    val TextSecondaryLight = Color(0xFF6B7570)

    val BackgroundDark = Color(0xFF101513)
    val SurfaceDark = Color(0xFF1B211E)
    val OutlineDark = Color(0xFF33403A)
    val TextPrimaryDark = Color(0xFFEAF0EC)
    val TextSecondaryDark = Color(0xFFA6B0AB)
}

/** Espaçamentos consistentes reutilizados nas telas e componentes. */
object RachaiSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
}

/** Raios de borda padrão dos componentes reutilizáveis. */
object RachaiRadius {
    val card = 18.dp
    val field = 14.dp
    val pill = 100.dp
}

/** Tipografia Material 3 completa do app. */
private val RachaiTypography = Typography(
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)

/** Shapes Material 3 alinhados com os raios do design system. */
private val RachaiShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(RachaiRadius.field),
    medium = RoundedCornerShape(RachaiRadius.card),
    large = RoundedCornerShape(RachaiRadius.pill),
    extraLarge = RoundedCornerShape(RachaiRadius.pill)
)

private val LightColors = lightColorScheme(
    primary = RachaiColors.Jade,
    onPrimary = Color.White,
    primaryContainer = RachaiColors.JadeContainerLight,
    onPrimaryContainer = RachaiColors.JadeDark,
    secondary = RachaiColors.Coral,
    onSecondary = Color.White,
    secondaryContainer = RachaiColors.CoralContainerLight,
    onSecondaryContainer = RachaiColors.Coral,
    background = RachaiColors.BackgroundLight,
    onBackground = RachaiColors.TextPrimaryLight,
    surface = RachaiColors.SurfaceLight,
    onSurface = RachaiColors.TextPrimaryLight,
    surfaceVariant = RachaiColors.BackgroundLight,
    onSurfaceVariant = RachaiColors.TextSecondaryLight,
    outline = RachaiColors.OutlineLight,
    error = RachaiColors.Coral
)

private val DarkColors = darkColorScheme(
    primary = RachaiColors.Jade,
    onPrimary = Color.White,
    primaryContainer = RachaiColors.JadeDark,
    onPrimaryContainer = RachaiColors.JadeContainerLight,
    secondary = RachaiColors.Coral,
    onSecondary = Color.White,
    secondaryContainer = RachaiColors.Coral.copy(alpha = 0.24f),
    onSecondaryContainer = RachaiColors.CoralContainerLight,
    background = RachaiColors.BackgroundDark,
    onBackground = RachaiColors.TextPrimaryDark,
    surface = RachaiColors.SurfaceDark,
    onSurface = RachaiColors.TextPrimaryDark,
    surfaceVariant = RachaiColors.SurfaceDark,
    onSurfaceVariant = RachaiColors.TextSecondaryDark,
    outline = RachaiColors.OutlineDark,
    error = RachaiColors.Coral
)

@Composable
fun RachaiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val windowSizeClass = rememberWindowSizeClass()
    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = RachaiTypography,
            shapes = RachaiShapes,
            content = content
        )
    }
}
