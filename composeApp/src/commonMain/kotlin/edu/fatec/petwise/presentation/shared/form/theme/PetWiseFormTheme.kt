package edu.fatec.petwise.presentation.shared.form.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import edu.fatec.petwise.presentation.theme.ThemeDefinition
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex
import androidx.compose.ui.unit.dp

val LocalPetWiseTheme = staticCompositionLocalOf<ThemeDefinition> { 
    PetWiseTheme.Light 
}

@Composable
fun createPetWiseColorScheme(petWiseTheme: ThemeDefinition): ColorScheme {
    return lightColorScheme(
        primary = Color.fromHex(petWiseTheme.palette.primary),
        onPrimary = Color.White,
        primaryContainer = Color.fromHex(petWiseTheme.palette.primaryVariant),
        onPrimaryContainer = Color.fromHex(petWiseTheme.palette.textPrimary),
        
        secondary = Color.fromHex(petWiseTheme.palette.secondary),
        onSecondary = Color.fromHex(petWiseTheme.palette.textPrimary),
        secondaryContainer = Color.fromHex(petWiseTheme.palette.cardBackground),
        onSecondaryContainer = Color.fromHex(petWiseTheme.palette.textPrimary),
        
        tertiary = Color.fromHex(petWiseTheme.palette.accent),
        onTertiary = Color.White,
        
        background = Color.fromHex(petWiseTheme.palette.inputBackground),
        onBackground = Color.fromHex(petWiseTheme.palette.textPrimary),
        
        surface = Color.fromHex(petWiseTheme.palette.inputBackground),
        onSurface = Color.fromHex(petWiseTheme.palette.textPrimary),
        surfaceVariant = Color.fromHex(petWiseTheme.palette.cardBackground),
        onSurfaceVariant = Color.fromHex(petWiseTheme.palette.textSecondary),
        
        surfaceTint = Color.fromHex(petWiseTheme.palette.primary),
        inverseSurface = Color.fromHex(petWiseTheme.palette.textPrimary),
        inverseOnSurface = Color.fromHex(petWiseTheme.palette.inputBackground),
        
        error = Color.fromHex("#ff0000"),
        onError = Color.White,
        errorContainer = Color.fromHex("#FFEBEE"),
        onErrorContainer = Color.fromHex("#d32f2f"),
        
        outline = Color.fromHex(petWiseTheme.palette.textSecondary),
        outlineVariant = Color.fromHex(petWiseTheme.palette.border),
        
        scrim = Color.Black.copy(alpha = 0.32f),
        surfaceBright = Color.fromHex(petWiseTheme.palette.inputBackground),
        surfaceContainer = Color.fromHex(petWiseTheme.palette.cardBackground),
        surfaceContainerHigh = Color.fromHex(petWiseTheme.palette.cardBackground),
        surfaceContainerHighest = Color.fromHex(petWiseTheme.palette.cardBackground),
        surfaceContainerLow = Color.fromHex(petWiseTheme.palette.inputBackground),
        surfaceContainerLowest = Color.White,
        surfaceDim = Color.fromHex(petWiseTheme.palette.secondary)
    )
}


@Composable
fun PetWiseFormTheme(
    petWiseTheme: ThemeDefinition = PetWiseTheme.Light,
    content: @Composable () -> Unit
) {
    val colorScheme = createPetWiseColorScheme(petWiseTheme)
    
    CompositionLocalProvider(LocalPetWiseTheme provides petWiseTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = petWiseTheme.typography,
            content = content
        )
    }
}

object PetWiseFormColors {
    
    @Composable
    fun getFieldColors(
        petWiseTheme: ThemeDefinition = LocalPetWiseTheme.current,
        isError: Boolean = false,
        isFocused: Boolean = false
    ): TextFieldColors {
        return OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) Color.fromHex("#ff0000") 
                else Color.fromHex(petWiseTheme.palette.primary),
            unfocusedBorderColor = if (isError) Color.fromHex("#ff0000") 
                else Color.fromHex(petWiseTheme.palette.textSecondary),
            focusedLabelColor = if (isError) Color.fromHex("#ff0000") 
                else Color.fromHex(petWiseTheme.palette.primary),
            unfocusedLabelColor = if (isError) Color.fromHex("#ff0000") 
                else Color.fromHex(petWiseTheme.palette.textSecondary),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorBorderColor = Color.fromHex("#ff0000"),
            errorLabelColor = Color.fromHex("#ff0000"),
            errorContainerColor = Color.White,
            cursorColor = Color.fromHex(petWiseTheme.palette.primary),
            focusedPlaceholderColor = Color.fromHex(petWiseTheme.palette.textSecondary).copy(alpha = 0.6f),
            unfocusedPlaceholderColor = Color.fromHex(petWiseTheme.palette.textSecondary).copy(alpha = 0.6f)
        )
    }
    
    @Composable
    fun getPrimaryButtonColors(
        petWiseTheme: ThemeDefinition = LocalPetWiseTheme.current
    ): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = Color.fromHex(petWiseTheme.palette.primary),
            contentColor = Color.White,
            disabledContainerColor = Color.fromHex(petWiseTheme.palette.textSecondary).copy(alpha = 0.3f),
            disabledContentColor = Color.fromHex(petWiseTheme.palette.textSecondary).copy(alpha = 0.6f)
        )
    }
    
    @Composable
    fun getSegmentedControlColors(
        petWiseTheme: ThemeDefinition = LocalPetWiseTheme.current,
        isSelected: Boolean
    ): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = if (isSelected) 
                Color.fromHex(petWiseTheme.palette.primary) 
            else 
                Color.White,
            contentColor = if (isSelected) 
                Color.White 
            else 
                Color.fromHex(petWiseTheme.palette.textPrimary)
        )
    }
    
    @Composable
    fun getOutlinedButtonColors(
        petWiseTheme: ThemeDefinition = LocalPetWiseTheme.current
    ): ButtonColors {
        return ButtonDefaults.outlinedButtonColors(
            contentColor = Color.fromHex(petWiseTheme.palette.primary),
            disabledContentColor = Color.fromHex(petWiseTheme.palette.textSecondary).copy(alpha = 0.6f)
        )
    }
    
    @Composable
    fun getErrorTextColor(): Color {
        return Color.fromHex("#d32f2f")
    }
}

object PetWiseFormSpacing {
    
    @Composable
    fun getFieldSpacing(screenWidth: androidx.compose.ui.unit.Dp): androidx.compose.ui.unit.Dp {
        return when {
            screenWidth < 400.dp -> 12.dp
            screenWidth < 600.dp -> 16.dp
            else -> 20.dp
        }
    }
    
    @Composable
    fun getFieldHeight(screenWidth: androidx.compose.ui.unit.Dp): androidx.compose.ui.unit.Dp {
        return when {
            screenWidth < 400.dp -> 52.dp
            else -> 56.dp
        }
    }
    
    @Composable
    fun getButtonHeight(screenWidth: androidx.compose.ui.unit.Dp): androidx.compose.ui.unit.Dp {
        return when {
            screenWidth < 400.dp -> 44.dp
            screenWidth < 600.dp -> 48.dp
            else -> 52.dp
        }
    }
}