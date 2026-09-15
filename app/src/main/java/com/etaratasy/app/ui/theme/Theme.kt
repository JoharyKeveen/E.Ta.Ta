package com.etaratasy.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Palette inspirée du papier administratif malgache et des couleurs nationales. */
object EtataColors {
    val Paper = Color(0xFFF3EEE2)
    val Surface = Color(0xFFFFFFFF)
    val Ink = Color(0xFF201D18)
    val InkSoft = Color(0xFF7A7469)
    val Rouge = Color(0xFFC1272D)
    val RougeSoft = Color(0xFFF5DEDD)
    val Vert = Color(0xFF2E4A3D)
    val VertSoft = Color(0xFFDCE6DE)
    val Ambre = Color(0xFF8A5A20)
    val AmbreSoft = Color(0xFFF1E1C6)
    val Line = Color(0xFFDED5C0)
}

private val scheme = lightColorScheme(
    primary = EtataColors.Ink,
    onPrimary = Color.White,
    secondary = EtataColors.Rouge,
    onSecondary = Color.White,
    background = EtataColors.Paper,
    onBackground = EtataColors.Ink,
    surface = EtataColors.Surface,
    onSurface = EtataColors.Ink,
    outline = EtataColors.Line
)

private val typo = Typography(
    headlineMedium = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = EtataColors.Ink),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    bodyMedium = TextStyle(fontSize = 14.sp),
    bodySmall = TextStyle(fontSize = 12.sp, color = EtataColors.InkSoft),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
)

@Composable
fun ETaraTasyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, typography = typo, content = content)
}
