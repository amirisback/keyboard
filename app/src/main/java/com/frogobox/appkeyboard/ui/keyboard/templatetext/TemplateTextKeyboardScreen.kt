package com.frogobox.appkeyboard.ui.keyboard.templatetext

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_APP
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_GAME
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_GREETING
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_LOVE
import com.frogobox.appkeyboard.model.KeyboardFeatureType.TEMPLATE_TEXT_SALE
import com.frogobox.appkeyboard.ui.keyboard.common.KeyboardFeatureToolbar

data class TemplateCategory(
    val type: KeyboardFeatureType,
    val icon: String,
    val title: String
)

val CATEGORIES = listOf(
    TemplateCategory(TEMPLATE_TEXT_GAME, "🎮", "Game"),
    TemplateCategory(TEMPLATE_TEXT_APP, "📱", "App"),
    TemplateCategory(TEMPLATE_TEXT_SALE, "💰", "Sale"),
    TemplateCategory(TEMPLATE_TEXT_GREETING, "👋", "Greeting"),
    TemplateCategory(TEMPLATE_TEXT_LOVE, "❤️", "Love")
)

@Composable
fun TemplateTextKeyboardScreen(
    onCommitText: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialType: KeyboardFeatureType = TEMPLATE_TEXT_GAME
) {
    val context = LocalContext.current
    var selectedType by remember(initialType) { mutableStateOf(initialType) }

    val currentCategoryTitle = when (selectedType) {
        TEMPLATE_TEXT_GAME -> "Game Templates"
        TEMPLATE_TEXT_APP -> "App Templates"
        TEMPLATE_TEXT_SALE -> "Sale / Store Templates"
        TEMPLATE_TEXT_GREETING -> "Greeting Templates"
        TEMPLATE_TEXT_LOVE -> "Love & Sweet Templates"
        else -> "Quick Templates"
    }

    val templateList = remember(selectedType) {
        when (selectedType) {
            TEMPLATE_TEXT_GAME -> TemplateTextUtils.getTextGame(context)
            TEMPLATE_TEXT_APP -> TemplateTextUtils.getTextApp(context)
            TEMPLATE_TEXT_SALE -> TemplateTextUtils.getTextSale(context)
            TEMPLATE_TEXT_GREETING -> TemplateTextUtils.getTextGreeting(context)
            TEMPLATE_TEXT_LOVE -> TemplateTextUtils.getTextLove(context)
            else -> emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        KeyboardFeatureToolbar(
            title = currentCategoryTitle,
            subtitle = "Tap any template to paste into chat",
            onBackClick = onBackClick
        )

        // Categories Chip Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(CATEGORIES) { category ->
                val isSelected = category.type == selectedType
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedType = category.type },
                    label = {
                        Text(
                            text = "${category.icon} ${category.title}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        HorizontalDivider(
            thickness = 0.8.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )

        // Template List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(
                items = templateList,
                key = { it.id }
            ) { template ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCommitText(template.text) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = template.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}
