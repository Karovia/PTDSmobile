package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ClinicalLightColorScheme = lightColorScheme(
    primary = ClinicalPrimary,
    onPrimary = ClinicalOnPrimary,
    primaryContainer = ClinicalPrimaryContainer,
    onPrimaryContainer = ClinicalOnPrimaryContainer,
    secondary = ClinicalSecondary,
    onSecondary = ClinicalOnSecondary,
    secondaryContainer = ClinicalSecondaryContainer,
    onSecondaryContainer = ClinicalOnSecondaryContainer,
    tertiary = ClinicalTertiary,
    tertiaryContainer = ClinicalTertiaryContainer,
    background = ClinicalSurface,
    onBackground = ClinicalOnSurface,
    surface = ClinicalSurface,
    onSurface = ClinicalOnSurface,
    surfaceVariant = ClinicalSurfaceContainerHighest,
    onSurfaceVariant = ClinicalOnSurfaceVariant,
    surfaceContainerLowest = ClinicalSurfaceContainerLowest,
    surfaceContainerLow = ClinicalSurfaceContainerLow,
    surfaceContainer = ClinicalSurfaceContainer,
    surfaceContainerHigh = ClinicalSurfaceContainerHigh,
    surfaceContainerHighest = ClinicalSurfaceContainerHighest,
    inverseSurface = ClinicalInverseSurface,
    inverseOnSurface = ClinicalInverseOnSurface,
    outline = ClinicalOutline,
    outlineVariant = ClinicalOutlineVariant,
    error = ClinicalError,
    errorContainer = ClinicalErrorContainer
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ClinicalLightColorScheme,
        typography = Typography,
        content = content
    )
}
