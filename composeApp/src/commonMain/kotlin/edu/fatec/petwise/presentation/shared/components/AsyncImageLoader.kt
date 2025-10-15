package edu.fatec.petwise.presentation.shared.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ImageState {
    LOADING, SUCCESS, ERROR, EMPTY
}

@Composable
fun AsyncImage(
    imageUrl: String?,
    contentDescription: String?,
    fallbackIcon: ImageVector = Icons.Default.Pets,
    fallbackColor: Color = Color.Gray,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier,
    onStateChange: ((ImageState) -> Unit)? = null
) {
    var imageState by remember(imageUrl) { mutableStateOf(ImageState.EMPTY) }
    
    LaunchedEffect(imageState) {
        onStateChange?.invoke(imageState)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(fallbackColor),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUrl.isNullOrEmpty() -> {
                LaunchedEffect(Unit) {
                    imageState = ImageState.EMPTY
                }
                Icon(
                    imageVector = fallbackIcon,
                    contentDescription = contentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.6f)
                )
            }
            else -> {
                LaunchedEffect(imageUrl) {
                    imageState = ImageState.LOADING
                    println("AsyncImage: Tentando carregar imagem: $imageUrl")
                    
                    try {
                        kotlinx.coroutines.delay(1000)
                        println("AsyncImage: Simulating image load success for: $imageUrl")
                        imageState = ImageState.SUCCESS
                    } catch (e: Exception) {
                        println("AsyncImage: Falha ao carregar imagem: ${e.message}")
                        imageState = ImageState.ERROR
                    }
                }

                when (imageState) {
                    ImageState.LOADING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(size * 0.5f),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                    ImageState.SUCCESS -> {
                        Box(
                            modifier = Modifier
                                .size(size)
                                .clip(CircleShape)
                                .background(fallbackColor.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = fallbackIcon,
                                    contentDescription = contentDescription,
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(size * 0.4f)
                                )
                                Text(
                                    text = "IMG",
                                    color = Color.White,
                                    fontSize = (size.value / 8).sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    ImageState.ERROR -> {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Erro ao carregar imagem",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(size * 0.6f)
                        )
                    }
                    ImageState.EMPTY -> {
                        Icon(
                            imageVector = fallbackIcon,
                            contentDescription = contentDescription,
                            tint = Color.White,
                            modifier = Modifier.size(size * 0.6f)
                        )
                    }
                }
            }
        }
    }
}