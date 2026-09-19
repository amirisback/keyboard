package com.frogobox.appkeyboard.ui.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Scaffold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme
import com.frogobox.appkeyboard.ui.theme.compose.FrogoTopAppBar

/**
 * TestScreen Composable
 * Migrated from activity_test.xml
 * Provides testing input fields (multiline text, numeric input, and auto-complete dropdown).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    dummyOptions: List<String> = emptyList(),
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var fullText by remember { mutableStateOf("") }
    var numberText by remember { mutableStateOf("") }
    var autoText by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val filteredOptions = remember(autoText, dummyOptions) {
        if (autoText.isBlank()) {
            dummyOptions
        } else {
            dummyOptions.filter { it.contains(autoText, ignoreCase = true) }
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FrogoTopAppBar(
                title = "Test Area",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)
        ) {
        Text(
            text = "Let's Do Some Test",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Full Text (Multiline)
        OutlinedTextField(
            value = fullText,
            onValueChange = { fullText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Full Text") },
            placeholder = { Text("Full Text") },
            minLines = 3,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Number Input
        OutlinedTextField(
            value = numberText,
            onValueChange = { numberText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Number") },
            placeholder = { Text("Number") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // AutoComplete Dropdown
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = autoText,
                onValueChange = {
                    autoText = it
                    dropdownExpanded = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true),
                label = { Text("Full Text Auto") },
                placeholder = { Text("Start typing city...") },
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    filteredOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                autoText = option
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
}

@Preview(showBackground = true, name = "TestScreen Light")
@Composable
fun TestScreenPreview() {
    FrogoKeyboardTheme(darkTheme = false) {
        TestScreen(
            dummyOptions = listOf("Kuningan", "Menteng", "Pegangsaan")
        )
    }
}

@Preview(showBackground = true, name = "TestScreen Dark")
@Composable
fun TestScreenDarkPreview() {
    FrogoKeyboardTheme(darkTheme = true) {
        TestScreen(
            dummyOptions = listOf("Kuningan", "Menteng", "Pegangsaan")
        )
    }
}
