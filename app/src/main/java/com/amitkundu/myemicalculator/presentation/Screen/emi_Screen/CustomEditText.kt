import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun CustomEditText(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String = "Required!",
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Number,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newTextFieldValue ->
            // Remove commas to get the raw number input
            val rawInput = newTextFieldValue.text.replace(",", "")

            // Parse the input as a number if it's valid
            val parsedNumber = rawInput.toDoubleOrNull()

            // If the input is valid, format it with commas
            val formattedInput = if (parsedNumber != null) {
                formatNumber(parsedNumber)
            } else {
                rawInput
            }

            // Calculate the new cursor position
            val cursorPosition = newTextFieldValue.selection.start
            val newCursorPosition = cursorPosition + (formattedInput.length - newTextFieldValue.text.length)

            // Update the TextFieldValue with the formatted text and the new cursor position
            onValueChange(
                TextFieldValue(
                    text = formattedInput,
                    selection = TextRange(newCursorPosition)
                )
            )
        },
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        supportingText = {
            if (isError) Text(text = errorMessage) else null
        },
        shape = RoundedCornerShape(16.dp),
    )
}



