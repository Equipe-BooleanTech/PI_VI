package edu.fatec.petwise.features.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.fatec.petwise.presentation.shared.form.*
import edu.fatec.petwise.navigation.NavigationManager
import edu.fatec.petwise.presentation.theme.PetWiseTheme
import edu.fatec.petwise.presentation.theme.PetWiseThemeWrapper
import edu.fatec.petwise.presentation.theme.fromHex
import org.jetbrains.compose.ui.tooling.preview.Preview
import edu.fatec.petwise.features.auth.presentation.forms.registerSchema
import edu.fatec.petwise.features.auth.presentation.forms.loginSchema
import kotlinx.serialization.json.JsonPrimitive


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navigationManager: NavigationManager) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val titles = listOf("Login", "Registrar")

    val schema = if (selectedTab == 0) loginSchema else registerSchema

    val formConfiguration = remember(schema) {
        FormConfiguration(
            id = schema.id,
            title = schema.title,
            description = schema.description,
            fields = schema.fields.map { field ->
                FormFieldDefinition(
                    id = field.id,
                    label = field.label,
                    type = when (field.type) {
                        "email" -> FormFieldType.EMAIL
                        "password" -> FormFieldType.PASSWORD
                        "text" -> FormFieldType.TEXT
                        "submit" -> FormFieldType.SUBMIT
                        "select" -> FormFieldType.SELECT
                        "segmented" -> FormFieldType.SEGMENTED_CONTROL
                        else -> FormFieldType.TEXT
                    },
                    placeholder = field.placeholder,
                    options = field.options,
                    default = field.default,
                    validators = field.validators?.map { validator ->
                        ValidationRule(
                            type = when (validator.type) {
                                "required" -> ValidationType.REQUIRED
                                "email" -> ValidationType.EMAIL
                                "minLength" -> ValidationType.MIN_LENGTH
                                "password" -> ValidationType.PASSWORD_STRENGTH
                                "matchesField" -> ValidationType.MATCHES_FIELD
                                "cpf" -> ValidationType.CPF
                                "cnpj" -> ValidationType.CNPJ
                                "phone" -> ValidationType.PHONE
                                "pattern" -> ValidationType.PATTERN
                                else -> ValidationType.CUSTOM
                            },
                            message = validator.message,
                            value = validator.value,
                            field = validator.field
                        )
                    } ?: emptyList(),
                    visibility = field.visibleIf?.let { visibleIf ->
                        VisibilityRule(
                            conditions = visibleIf.map { (fieldId, value) ->
                                VisibilityCondition(
                                    fieldId = fieldId,
                                    operator = ComparisonOperator.EQUALS,
                                    value = value
                                )
                            },
                            operator = LogicalOperator.AND
                        )
                    }
                )
            },
            styling = FormStyling(
                primaryColor = "#00b942",
                errorColor = "#d32f2f",
                successColor = "#00b942"
            )
        )
    }

    val viewModel = remember(selectedTab) {
        DynamicFormViewModel(initialConfiguration = formConfiguration)
    }

    val theme = PetWiseTheme.Light

    PetWiseThemeWrapper(theme) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.fromHex(theme.palette.background)),
            contentAlignment = Alignment.Center
        ) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight

            val cardWidth = when {
                screenWidth < 600.dp -> screenWidth * 0.9f
                screenWidth < 840.dp -> screenWidth * 0.7f
                else -> 500.dp
            }

            val cardPadding = when {
                screenWidth < 400.dp -> 16.dp
                screenWidth < 600.dp -> 20.dp
                else -> 24.dp
            }

            Card(
                modifier = Modifier
                    .padding(cardPadding)
                    .widthIn(max = cardWidth)
                    .heightIn(max = screenHeight * 0.9f),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.fromHex(theme.palette.cardBackground)
                )
            ) {
                val innerPadding = when {
                    screenWidth < 400.dp -> 16.dp
                    screenWidth < 600.dp -> 24.dp
                    else -> 32.dp
                }

                Column(
                    modifier = Modifier.padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        titles.forEachIndexed { index, title ->
                            SegmentedButton(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                shape = SegmentedButtonDefaults.itemShape(index, titles.size),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = Color.Transparent,
                                    activeContentColor = Color.fromHex(theme.palette.textPrimary),
                                    inactiveContainerColor = Color.Transparent,
                                    inactiveContentColor = Color.fromHex(theme.palette.textSecondary)
                                )
                            ) {
                                Text(title)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    key(formConfiguration.id) {
                        DynamicForm(
                            viewModel = viewModel,
                            colorScheme = MaterialTheme.colorScheme.copy(
                                primary = Color.fromHex(theme.palette.primary),
                                error = Color.fromHex("#d32f2f")
                            ),
                            onSubmitSuccess = { values ->
                                navigationManager.navigateTo(NavigationManager.Screen.Dashboard)
                            }
                        )
                    }

                    if (selectedTab == 0) {
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Esqueceu sua senha?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.fromHex(theme.palette.primary),
                                fontWeight = FontWeight.Medium,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier
                                .clickable {
                                    navigationManager.navigateTo(NavigationManager.Screen.ForgotPassword)
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}