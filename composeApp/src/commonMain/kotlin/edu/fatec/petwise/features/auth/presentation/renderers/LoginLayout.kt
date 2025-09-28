package edu.fatec.petwise.features.auth.presentation.renderers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Alignment

/**
 * LoginForm composable for user authentication.
 * - Uses Material3 components and theme colors for consistency.
 * - Includes username/email and password fields with validation and error handling.
 * - Shows loading indicator and feedback on submission.
 * - Modular and well-documented for collaborative development.
 */
@Composable
fun LoginForm(
    onLogin: (String, String) -> Unit = { _, _ -> }, // TODO: Implementar função de login
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if (error != null) error = null
            },
            label = { Text("Username or Email") },
            singleLine = true,
            enabled = !loading,
            isError = error != null && username.isBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (error != null) error = null
            },
            label = { Text("Password") },
            singleLine = true,
            enabled = !loading,
            isError = error != null && password.isBlank(),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                focusManager.clearFocus()
                if (username.isBlank() || password.isBlank()) {
                    error = "Please enter both username/email and password."
                } else {
                    loading = true
                    // Simulate login process
                    onLogin(username, password)
                    // For demonstration, we'll just reset loading after a delay
                    // In real implementation, handle success/error accordingly
                    loading = false
                    error = "Login function not implemented."
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Login")
            }
        }
    }
}
