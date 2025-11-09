package edu.fatec.petwise.features.medications.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun MedicationErrorSnackbar(
    errorMessage: String,
    onDismiss: () -> Unit,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val theme = PetWiseTheme.Light

    LaunchedEffect(errorMessage) {
        delay(5000) // Auto dismiss after 5 seconds
        onDismiss()
    }

    Snackbar(
        modifier = modifier,
        action = actionLabel?.let { label ->
            {
                TextButton(
                    onClick = { onActionClick?.invoke() }
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        dismissAction = {
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Fechar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        containerColor = Color.fromHex("#F44336"),
        contentColor = Color.White,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Erro",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = errorMessage,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MedicationSuccessSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = PetWiseTheme.Light

    LaunchedEffect(message) {
        delay(3000) // Auto dismiss after 3 seconds
        onDismiss()
    }

    Snackbar(
        modifier = modifier,
        dismissAction = {
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.Error, // You might want to use a check icon here
                    contentDescription = "Fechar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        containerColor = Color.fromHex("#4CAF50"),
        contentColor = Color.White,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Text(
            text = message,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}