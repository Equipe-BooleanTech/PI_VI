package edu.fatec.petwise.presentation.shared.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import kotlinx.serialization.json.jsonPrimitive
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

actual class PlatformFormBehavior {
    actual fun shouldShowKeyboardSpacer(): Boolean = true

    actual fun shouldUseNativePickerFor(fieldType: FormFieldType): Boolean {
        return when (fieldType) {
            FormFieldType.DATE, FormFieldType.TIME, FormFieldType.DATETIME -> true
            else -> false
        }
    }

    actual fun getSafeAreaInsets(): PaddingValues {

        return PaddingValues(0.dp)
    }

    actual fun getOptimalFieldHeight(): androidx.compose.ui.unit.Dp = 56.dp

    actual fun supportsHapticFeedback(): Boolean = true

    actual fun supportsSystemDarkMode(): Boolean = true
}

actual object PlatformFormStyling {
    actual fun getFieldShape(): Shape = RoundedCornerShape(8.dp)

    actual fun getFieldColors(colorScheme: ColorScheme): FieldColors {
        return FieldColors(
            background = colorScheme.surface,
            border = colorScheme.outline,
            focusedBorder = colorScheme.primary,
            errorBorder = colorScheme.error,
            text = colorScheme.onSurface,
            label = colorScheme.onSurfaceVariant,
            placeholder = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }

    actual fun getTypography(): PlatformTypography {
        return PlatformTypography(
            fieldLabel = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            fieldText = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            errorText = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            helperText = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }

    actual fun getSpacing(): PlatformSpacing {
        return PlatformSpacing(
            fieldPadding = PaddingValues(16.dp),
            fieldSpacing = 16.dp,
            sectionSpacing = 24.dp
        )
    }
}

actual class PlatformInputHandling {
    actual fun formatPhoneNumber(input: String, country: String): String {
        val digits = input.replace(Regex("[^0-9]"), "")
        return when (country) {
            "BR" -> {
                when (digits.length) {
                    11 -> "${digits.substring(0, 2)} ${digits.substring(2, 7)}-${digits.substring(7)}"
                    10 -> "${digits.substring(0, 2)} ${digits.substring(2, 6)}-${digits.substring(6)}"
                    else -> digits
                }
            }
            else -> digits
        }
    }

    actual fun formatCurrency(amount: Double, currency: String): String {
        return when (currency) {
            "BRL" -> "R$ %.2f".format(amount)
            "USD" -> "$%.2f".format(amount)
            else -> "%.2f %s".format(amount, currency)
        }
    }

    actual fun formatDate(timestamp: Long, format: String): String {
        return java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
    }

    actual fun validatePlatformSpecific(fieldType: FormFieldType, value: String): Boolean {
        return when (fieldType) {
            FormFieldType.PHONE -> {
                android.util.Patterns.PHONE.matcher(value).matches()
            }
            FormFieldType.EMAIL -> {
                android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()
            }
            else -> true
        }
    }
}

actual class PlatformFileHandling {
    actual suspend fun pickFile(mimeTypes: List<String>): PlatformFile? {
        return null
    }

    actual suspend fun pickImage(): PlatformFile? {
        return null
    }

    actual suspend fun uploadFile(file: PlatformFile, endpoint: String): Result<String> {
        return Result.failure(Exception("Not implemented"))
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformDatePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (kotlinx.datetime.LocalDate) -> Unit,
    modifier: Modifier
) {
    var showDialog by remember { mutableStateOf(true) }
    
    val maxDate = fieldDefinition.validators.find { it.type == ValidationType.MAX_DATE }
        ?.value?.jsonPrimitive?.content?.toLongOrNull()
        ?: Long.MAX_VALUE
        
    val minDate = fieldDefinition.validators.find { it.type == ValidationType.MIN_DATE }
        ?.value?.jsonPrimitive?.content?.toLongOrNull()
        ?: Long.MIN_VALUE

    val isBirthDate = fieldDefinition.id.contains("birth", ignoreCase = true) || 
                      fieldDefinition.label?.contains("nascimento", ignoreCase = true) == true ||
                      fieldDefinition.label?.contains("birth", ignoreCase = true) == true
    
    val effectiveMaxDate = if (isBirthDate && maxDate == Long.MAX_VALUE) {
        System.currentTimeMillis()
    } else {
        maxDate
    }
    
    // Calculate safe year range
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val minYear = if (minDate == Long.MIN_VALUE) {
        1900
    } else {
        java.util.Calendar.getInstance().apply { timeInMillis = minDate }.get(java.util.Calendar.YEAR)
    }
    val maxYear = if (effectiveMaxDate == Long.MAX_VALUE) {
        currentYear + 100
    } else {
        java.util.Calendar.getInstance().apply { timeInMillis = effectiveMaxDate }.get(java.util.Calendar.YEAR)
    }
    
    // Get initial selected date from field state
    val initialSelectedDateMillis: Long? = try {
        when (val value = fieldState.value) {
            is kotlinx.datetime.LocalDateTime -> {
                val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                calendar.set(value.year, value.monthNumber - 1, value.dayOfMonth, 0, 0, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            is kotlinx.datetime.LocalDate -> {
                val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                calendar.set(value.year, value.monthNumber - 1, value.dayOfMonth, 0, 0, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
    
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        yearRange = IntRange(start = minYear, endInclusive = maxYear),
        selectableDates = object : androidx.compose.material3.SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val effectiveMin = if (minDate == Long.MIN_VALUE) 0L else minDate
                val effectiveMax = if (effectiveMaxDate == Long.MAX_VALUE) Long.MAX_VALUE else effectiveMaxDate
                return utcTimeMillis in effectiveMin..effectiveMax
            }
            
            override fun isSelectableYear(year: Int): Boolean {
                return year in minYear..maxYear
            }
        }
    )
    
    if (showDialog) {
        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = { 
                showDialog = false
                // Return current field value to trigger close without change
                try {
                    when (val value = fieldState.value) {
                        is kotlinx.datetime.LocalDateTime -> onValueChange(value.date)
                        is kotlinx.datetime.LocalDate -> onValueChange(value)
                        else -> {
                            // Return current date if no value set
                            val now = kotlinx.datetime.Clock.System.now()
                            onValueChange(now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date)
                        }
                    }
                } catch (e: Exception) {
                    val now = kotlinx.datetime.Clock.System.now()
                    onValueChange(now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date)
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showDialog = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            try {
                                val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                                val localDate = instant.toLocalDateTime(kotlinx.datetime.TimeZone.UTC).date
                                onValueChange(localDate)
                            } catch (e: Exception) {
                                // Fallback to current date on error
                                val now = kotlinx.datetime.Clock.System.now()
                                onValueChange(now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date)
                            }
                        } ?: run {
                            // No date selected, use current date
                            val now = kotlinx.datetime.Clock.System.now()
                            onValueChange(now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date)
                        }
                    }
                ) {
                    androidx.compose.material3.Text("OK")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { 
                        showDialog = false
                        // Return current field value to trigger close without change
                        try {
                            when (val value = fieldState.value) {
                                is kotlinx.datetime.LocalDateTime -> onValueChange(value.date)
                                is kotlinx.datetime.LocalDate -> onValueChange(value)
                                else -> {
                                    val now = kotlinx.datetime.Clock.System.now()
                                    onValueChange(now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date)
                                }
                            }
                        } catch (e: Exception) {
                            val now = kotlinx.datetime.Clock.System.now()
                            onValueChange(now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date)
                        }
                    }
                ) {
                    androidx.compose.material3.Text("Cancelar")
                }
            }
        ) {
            androidx.compose.material3.DatePicker(
                state = datePickerState,
                title = {
                    androidx.compose.material3.Text(
                        text = fieldDefinition.label ?: "Selecione a Data",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformTimePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    var showDialog by remember { mutableStateOf(true) }
    
    // Parse initial time from field state
    val (initialHour, initialMinute) = try {
        when (val value = fieldState.value) {
            is kotlinx.datetime.LocalDateTime -> Pair(value.hour, value.minute)
            is kotlinx.datetime.LocalTime -> Pair(value.hour, value.minute)
            is String -> {
                if (value.contains(":")) {
                    val parts = value.split(":")
                    Pair(parts[0].toIntOrNull() ?: 12, parts[1].toIntOrNull() ?: 0)
                } else {
                    Pair(12, 0)
                }
            }
            else -> Pair(12, 0)
        }
    } catch (e: Exception) {
        Pair(12, 0)
    }
    
    val timePickerState = androidx.compose.material3.rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )
    
    if (showDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { 
                showDialog = false
                // Return current value or default
                val currentValue = fieldState.displayValue.ifEmpty { 
                    "${initialHour.toString().padStart(2, '0')}:${initialMinute.toString().padStart(2, '0')}"
                }
                onValueChange(currentValue)
            }
        ) {
            androidx.compose.material3.Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                tonalElevation = 6.dp
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.Text(
                        text = fieldDefinition.label ?: "Selecione o Horário",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    androidx.compose.material3.TimePicker(
                        state = timePickerState
                    )
                    
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.TextButton(
                            onClick = { 
                                showDialog = false
                                val currentValue = fieldState.displayValue.ifEmpty { 
                                    "${initialHour.toString().padStart(2, '0')}:${initialMinute.toString().padStart(2, '0')}"
                                }
                                onValueChange(currentValue)
                            }
                        ) {
                            androidx.compose.material3.Text("Cancelar")
                        }
                        
                        androidx.compose.foundation.layout.Spacer(
                            modifier = Modifier.width(8.dp)
                        )
                        
                        androidx.compose.material3.TextButton(
                            onClick = {
                                showDialog = false
                                val hour = timePickerState.hour.toString().padStart(2, '0')
                                val minute = timePickerState.minute.toString().padStart(2, '0')
                                onValueChange("$hour:$minute")
                            }
                        ) {
                            androidx.compose.material3.Text("OK")
                        }
                    }
                }
            }
        }
    }
}

@Composable
actual fun PlatformFilePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (PlatformFile?) -> Unit,
    modifier: Modifier
) {
    androidx.compose.material3.Button(
        onClick = {  },
        modifier = modifier
    ) {
        androidx.compose.material3.Text("Pick File")
    }
}

@Composable
actual fun PlatformCameraCapturer(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (PlatformFile?) -> Unit,
    modifier: Modifier
) {
    androidx.compose.material3.Button(
        onClick = {  },
        modifier = modifier
    ) {
        androidx.compose.material3.Text("Take Photo")
    }
}

actual object PlatformAccessibility {
    actual fun announceForAccessibility(message: String) {
    }

    actual fun setContentDescription(elementId: String, description: String) {
    }

    actual fun shouldUseHighContrast(): Boolean {
        return false
    }

    actual fun shouldUseLargeText(): Boolean {
        return false
    }

    actual fun shouldReduceMotion(): Boolean {
        return false
    }
}

actual object PlatformHaptics {
    actual fun performLightImpact() {
    }

    actual fun performMediumImpact() {
    }

    actual fun performHeavyImpact() {
    }

    actual fun performSelectionChanged() {
    }

    actual fun performNotificationSuccess() {
    }

    actual fun performNotificationWarning() {
    }

    actual fun performNotificationError() {
    }
}

actual object PlatformValidation {
    actual fun validateBankAccount(accountNumber: String, bankCode: String): Boolean {
        return true
    }

    actual fun validateGovernmentId(id: String, type: String): Boolean {
        return true
    }

    actual fun validatePostalCode(code: String): Boolean {
        return code.matches(Regex("^\\d{5}-?\\d{3}$"))
    }

    actual fun validateCreditCard(number: String): Boolean {
        val cleanNumber = number.replace(Regex("[^0-9]"), "")
        if (cleanNumber.length < 13 || cleanNumber.length > 19) return false

        var sum = 0
        var alternate = false

        for (i in cleanNumber.length - 1 downTo 0) {
            var n = cleanNumber[i].toString().toInt()
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = n % 10 + 1
                }
            }
            sum += n
            alternate = !alternate
        }

        return sum % 10 == 0
    }
}