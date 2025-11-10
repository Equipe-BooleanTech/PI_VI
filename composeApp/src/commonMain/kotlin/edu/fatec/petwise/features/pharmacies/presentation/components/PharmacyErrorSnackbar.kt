package edu.fatec.petwise.features.pharmacies.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.fatec.petwise.presentation.theme.fromHex

@Composable
fun PharmacyErrorSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null
) {
    var visible by remember { mutableStateOf(true) }

    if (visible) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.fromHex("#F44336"),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Erro",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onRetry != null) {
                        TextButton(
                            onClick = {
                                visible = false
                                onRetry()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Text("TENTAR NOVAMENTE")
                        }
                    }

                    TextButton(
                        onClick = {
                            visible = false
                            onDismiss?.invoke()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("FECHAR")
                    }
                }
            }
        }
    }
}
