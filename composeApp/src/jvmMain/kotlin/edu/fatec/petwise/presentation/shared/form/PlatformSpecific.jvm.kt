package edu.fatec.petwise.presentation.shared.form

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
actual class PlatformFormBehavior {
    actual fun shouldShowKeyboardSpacer(): Boolean = false
    actual fun shouldUseNativePickerFor(fieldType: FormFieldType): Boolean = false
    actual fun getSafeAreaInsets(): PaddingValues = PaddingValues(0.dp)
    actual fun getOptimalFieldHeight(): Dp = 56.dp
    actual fun supportsHapticFeedback(): Boolean = false
    actual fun supportsSystemDarkMode(): Boolean = true
}

actual object PlatformFormStyling {
    actual fun getFieldShape(): Shape = RoundedCornerShape(8.dp)
    actual fun getFieldColors(colorScheme: ColorScheme): FieldColors = FieldColors(
        background = colorScheme.surface,
        border = colorScheme.outline,
        focusedBorder = colorScheme.primary,
        errorBorder = colorScheme.error,
        text = colorScheme.onSurface,
        label = colorScheme.onSurfaceVariant,
        placeholder = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )
    actual fun getTypography(): PlatformTypography = PlatformTypography(
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
    actual fun getSpacing(): PlatformSpacing = PlatformSpacing(
        fieldPadding = PaddingValues(16.dp),
        fieldSpacing = 16.dp,
        sectionSpacing = 24.dp
    )
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
            FormFieldType.EMAIL -> {
                value.contains("@") && value.contains(".")
            }
            FormFieldType.PHONE -> {
                value.replace(Regex("[^0-9]"), "").length >= 10
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
        return Result.failure(Exception("File upload not implemented for JVM"))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformDatePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (kotlinx.datetime.LocalDate) -> Unit,
    modifier: Modifier
) {
    val maxDate = fieldDefinition.validators.find { it.type == ValidationType.MAX_DATE }
        ?.value?.jsonPrimitive?.content?.toLongOrNull()
        ?: System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000 * 10)
        
    val minDate = fieldDefinition.validators.find { it.type == ValidationType.MIN_DATE }
        ?.value?.jsonPrimitive?.content?.toLongOrNull()
        ?: System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000 * 100)

    val isBirthDate = fieldDefinition.id.contains("birth", ignoreCase = true) || 
                      fieldDefinition.label?.contains("nascimento", ignoreCase = true) == true ||
                      fieldDefinition.label?.contains("birth", ignoreCase = true) == true
    
    val effectiveMaxDate = if (isBirthDate) {
        System.currentTimeMillis()
    } else {
        maxDate
    }
    
    val datePickerState = rememberDatePickerState(
        yearRange = IntRange(
            start = java.util.Calendar.getInstance().apply { timeInMillis = minDate }.get(java.util.Calendar.YEAR),
            endInclusive = java.util.Calendar.getInstance().apply { timeInMillis = effectiveMaxDate }.get(java.util.Calendar.YEAR)
        ),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in minDate..effectiveMaxDate
            }
            
            override fun isSelectableYear(year: Int): Boolean {
                val calendar = java.util.Calendar.getInstance()
                calendar.set(year, 0, 1)
                val yearStart = calendar.timeInMillis
                calendar.set(year, 11, 31)
                val yearEnd = calendar.timeInMillis
                
                return yearStart <= effectiveMaxDate && yearEnd >= minDate
            }
        }
    )
    
    DatePickerDialog(
        onDismissRequest = { 
            // Dismiss without changing value
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        if (millis in minDate..effectiveMaxDate) {
                            val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(kotlinx.datetime.TimeZone.UTC).date
                            onValueChange(localDate)
                        }
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { 
                    // Dismiss without changing value
                }
            ) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = fieldDefinition.label ?: "Selecione a Data",
                    modifier = Modifier.padding(16.dp)
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformTimePicker(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = true
    )
    
    Dialog(
        onDismissRequest = { 
            onValueChange(fieldState.displayValue)
        }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = fieldDefinition.label ?: "Selecione o Horário",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                TimePicker(
                    state = timePickerState
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { 
                            onValueChange(fieldState.displayValue)
                        }
                    ) {
                        Text("Cancelar")
                    }
                    
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    
                    TextButton(
                        onClick = {
                            val hour = timePickerState.hour.toString().padStart(2, '0')
                            val minute = timePickerState.minute.toString().padStart(2, '0')
                            onValueChange("$hour:$minute")
                        }
                    ) {
                        Text("OK")
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
    Button(
        onClick = {  },
        modifier = modifier
    ) {
        Text("Pick File")
    }
}

@Composable
actual fun PlatformCameraCapturer(
    fieldDefinition: FormFieldDefinition,
    fieldState: FieldState,
    onValueChange: (PlatformFile?) -> Unit,
    modifier: Modifier
) {
    Button(
        onClick = {  },
        modifier = modifier
    ) {
        Text("Take Photo")
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

        return accountNumber.isNotBlank() && bankCode.isNotBlank()
    }

    actual fun validateGovernmentId(id: String, type: String): Boolean {
        return when (type.uppercase()) {
            "CPF" -> validateCPF(id)
            "CNPJ" -> validateCNPJ(id)
            else -> true
        }
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

    private fun validateCPF(cpf: String): Boolean {
        val cleanCpf = cpf.replace(Regex("[^0-9]"), "")
        if (cleanCpf.length != 11) return false


        if (cleanCpf.all { it == cleanCpf[0] }) return false


        val digits = cleanCpf.map { it.toString().toInt() }

        val sum1 = (0..8).sumOf { digits[it] * (10 - it) }
        val checkDigit1 = 11 - (sum1 % 11)
        val validDigit1 = if (checkDigit1 >= 10) 0 else checkDigit1

        if (digits[9] != validDigit1) return false

        val sum2 = (0..9).sumOf { digits[it] * (11 - it) }
        val checkDigit2 = 11 - (sum2 % 11)
        val validDigit2 = if (checkDigit2 >= 10) 0 else checkDigit2

        return digits[10] == validDigit2
    }

    private fun validateCNPJ(cnpj: String): Boolean {
        val cleanCnpj = cnpj.replace(Regex("[^0-9]"), "")
        if (cleanCnpj.length != 14) return false


        if (cleanCnpj.all { it == cleanCnpj[0] }) return false


        val digits = cleanCnpj.map { it.toString().toInt() }

        val weights1 = listOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        val sum1 = (0..11).sumOf { digits[it] * weights1[it] }
        val checkDigit1 = 11 - (sum1 % 11)
        val validDigit1 = if (checkDigit1 >= 10) 0 else checkDigit1

        if (digits[12] != validDigit1) return false

        val weights2 = listOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        val sum2 = (0..12).sumOf { digits[it] * weights2[it] }
        val checkDigit2 = 11 - (sum2 % 11)
        val validDigit2 = if (checkDigit2 >= 10) 0 else checkDigit2

        return digits[13] == validDigit2
    }
}