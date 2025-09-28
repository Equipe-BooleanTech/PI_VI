package edu.fatec.petwise


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import edu.fatec.petwise.features.auth.presentation.LoginScreen
import edu.fatec.petwise.presentation.components.NavigationBar.ResponsiveNavigationBar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth()) {
            LoginScreen()
        }
    }
}