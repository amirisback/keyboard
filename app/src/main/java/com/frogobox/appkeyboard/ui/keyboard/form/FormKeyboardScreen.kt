package com.frogobox.appkeyboard.ui.keyboard.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.ui.keyboard.common.KeyboardFeatureToolbar
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard

/**
 * Structured form fields supported by [FormKeyboardScreen].
 */
enum class FormField {
    SUBJECT,
    DETAILS,
    REF_NUMBER
}

@Composable
fun FormKeyboardScreen(
    onCommitText: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRegisterKeyHandler: (((Int, Boolean) -> Boolean)?) -> Unit = {}
) {
    var subject by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var refNumber by remember { mutableStateOf("") }
    var activeField by remember { mutableStateOf(FormField.SUBJECT) }

    val scrollState = rememberScrollState()

    val handleKeyPress: (Int, Boolean) -> Boolean = { code, isShifted ->
        when (code) {
            ItemMainKeyboard.KEYCODE_DELETE -> {
                when (activeField) {
                    FormField.SUBJECT -> if (subject.isNotEmpty()) subject = subject.dropLast(1)
                    FormField.DETAILS -> if (details.isNotEmpty()) details = details.dropLast(1)
                    FormField.REF_NUMBER -> if (refNumber.isNotEmpty()) refNumber = refNumber.dropLast(1)
                }
                true
            }
            ItemMainKeyboard.KEYCODE_ENTER -> {
                when (activeField) {
                    FormField.SUBJECT -> activeField = FormField.DETAILS
                    FormField.DETAILS -> {
                        details += "\n"
                    }
                    FormField.REF_NUMBER -> {
                        val sb = StringBuilder()
                        if (subject.isNotBlank()) sb.append("Subject: ${subject.trim()}\n")
                        if (details.isNotBlank()) sb.append("Details: ${details.trim()}\n")
                        if (refNumber.isNotBlank()) sb.append("Ref/No: ${refNumber.trim()}\n")

                        if (sb.isNotEmpty()) {
                            onCommitText(sb.toString())
                            onBackClick()
                        }
                    }
                }
                true
            }
            ItemMainKeyboard.KEYCODE_TAB -> {
                activeField = when (activeField) {
                    FormField.SUBJECT -> FormField.DETAILS
                    FormField.DETAILS -> FormField.REF_NUMBER
                    FormField.REF_NUMBER -> FormField.SUBJECT
                }
                true
            }
            ItemMainKeyboard.KEYCODE_SPACE -> {
                when (activeField) {
                    FormField.SUBJECT -> subject += " "
                    FormField.DETAILS -> details += " "
                    FormField.REF_NUMBER -> refNumber += " "
                }
                true
            }
            ItemMainKeyboard.KEYCODE_SHIFT,
            ItemMainKeyboard.KEYCODE_MODE_CHANGE,
            ItemMainKeyboard.KEYCODE_EMOJI -> {
                false
            }
            else -> {
                if (code > 0) {
                    var ch = code.toChar()
                    if (ch.isLetter() && isShifted) {
                        ch = ch.uppercaseChar()
                    }
                    when (activeField) {
                        FormField.SUBJECT -> subject += ch
                        FormField.DETAILS -> details += ch
                        FormField.REF_NUMBER -> refNumber += ch
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    DisposableEffect(handleKeyPress) {
        onRegisterKeyHandler(handleKeyPress)
        onDispose {
            onRegisterKeyHandler(null)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        KeyboardFeatureToolbar(
            title = "Quick Form Snippet",
            subtitle = "Ketik via keyboard lalu klik Insert to Chat",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // 1. Subject Field Box
            FormFieldItem(
                label = "Title / Subject",
                value = subject,
                placeholder = "Tap to type title / subject",
                isActive = activeField == FormField.SUBJECT,
                onClick = { activeField = FormField.SUBJECT },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Details Field Box
            FormFieldItem(
                label = "Details / Description",
                value = details,
                placeholder = "Tap to type full details",
                isActive = activeField == FormField.DETAILS,
                minHeight = 56.dp,
                onClick = { activeField = FormField.DETAILS },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Ref Number Field Box
            FormFieldItem(
                label = "Number / Amount / Code",
                value = refNumber,
                placeholder = "Tap to type number or code",
                isActive = activeField == FormField.REF_NUMBER,
                onClick = { activeField = FormField.REF_NUMBER },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        subject = ""
                        details = ""
                        refNumber = ""
                        activeField = FormField.SUBJECT
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        modifier = Modifier.height(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear", fontSize = 11.5.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val sb = StringBuilder()
                        if (subject.isNotBlank()) sb.append("Subject: ${subject.trim()}\n")
                        if (details.isNotBlank()) sb.append("Details: ${details.trim()}\n")
                        if (refNumber.isNotBlank()) sb.append("Ref/No: ${refNumber.trim()}\n")

                        if (sb.isNotEmpty()) {
                            onCommitText(sb.toString())
                            onBackClick()
                        }
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Insert",
                        modifier = Modifier.height(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Insert to Chat",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FormFieldItem(
    label: String,
    value: String,
    placeholder: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    minHeight: androidx.compose.ui.unit.Dp = 42.dp
) {
    val borderColor = if (isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }

    val borderWidth = if (isActive) 1.5.dp else 1.dp

    val containerColor = if (isActive) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 10.5.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isActive) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "[Aktif Mengetik]",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeight),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            } else {
                Text(
                    text = if (isActive) "$value|" else value,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
