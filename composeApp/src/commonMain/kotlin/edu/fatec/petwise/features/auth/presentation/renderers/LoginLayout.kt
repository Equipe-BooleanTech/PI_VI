package edu.fatec.petwise.features.auth.presentation.renderers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.fatec.petwise.presentation.components.Logo.PetWiseLogo

@Composable
fun LoginForm(
    onLogin: (String, String) -> Unit = { _, _ -> },
    onForgotPassword: () -> Unit = {},
    onContact: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PetWiseLogo()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Sistema de Gestão Veterinária",
                fontSize = 14.sp,
                color = Color(0xFF2C3E50)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Bem-vindo de volta", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Faça login para acessar sua conta",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; error = null },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = null },
                label = { Text("Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                ClickableText(
                    text = AnnotatedString("Esqueceu sua senha?"),
                    onClick = { onForgotPassword() },
                    style = LocalTextStyle.current.copy(color = Color(0xFF27AE60), fontSize = 13.sp)
                )
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        error = "Preencha todos os campos."
                    } else {
                        loading = true
                        onLogin(email, password)
                        loading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60))
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Entrar", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            ClickableText(
                text = AnnotatedString("Não tem uma conta? Crie uma gratuitamente!"),
                onClick = { onContact() },
                style = LocalTextStyle.current.copy(color = Color(0xFF27AE60), fontSize = 13.sp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Sistema seguro e compatível com LGPD",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text("Versão 1.0.0", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
