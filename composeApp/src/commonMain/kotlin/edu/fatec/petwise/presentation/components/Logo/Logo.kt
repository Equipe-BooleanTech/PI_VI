package edu.fatec.petwise.presentation.components.Logo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import edu.fatec.petwise.presentation.colors.Branco
import edu.fatec.petwise.presentation.colors.VerdeMenta

/**
 * Logo estilizada "PetWise".
 */
@Composable
private fun PetWiseLogo() {
    Text(
        text = buildAnnotatedString {
            append("Pet")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = VerdeMenta)) {
                append("Wise")
            }
        },
        style = MaterialTheme.typography.titleLarge,
        color = Branco
    )
}
