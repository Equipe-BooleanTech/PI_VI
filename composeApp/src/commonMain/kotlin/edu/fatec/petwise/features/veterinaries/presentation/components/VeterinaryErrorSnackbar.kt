package edu.fatec.petwise.features.veterinaries.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VeterinaryErrorSnackbar(
    message: String,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier,
        action = {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Erro",
                tint = Color.White
            )
        },
        containerColor = Color.Red,
        contentColor = Color.White,
        dismissAction = null
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}