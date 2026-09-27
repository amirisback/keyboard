package com.frogobox.appkeyboard.ui.keyboard.autotext

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.TemplateText
import com.frogobox.appkeyboard.ui.keyboard.common.KeyboardFeatureToolbar
import com.frogobox.appkeyboard.ui.keyboard.templatetext.TemplateTextUtils
import com.frogobox.appkeyboard.ui.templatetext.TemplateTextActivity
import com.frogobox.appkeyboard.ui.theme.compose.FrogoEmptyView

@Composable
fun AutoTextKeyboardScreen(
    autoTextList: List<AutoTextEntity>,
    onCommitText: (String) -> Unit,
    onBackClick: () -> Unit,
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialCategory: AutoTextCategory = AutoTextCategory.MY_CUSTOM,
    isAlwaysShow: Boolean = false,
    onToggleAlwaysShow: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory) }

    val currentSubtitle = when (selectedCategory) {
        AutoTextCategory.MY_CUSTOM -> "Tap any snippet to paste into chat"
        AutoTextCategory.GAME -> "Tap any game review template to paste into chat"
        AutoTextCategory.APP -> "Tap any app review template to paste into chat"
        AutoTextCategory.SALE -> "Tap any sale admin template to paste into chat"
        AutoTextCategory.GREETING -> "Tap any greeting template to paste into chat"
        AutoTextCategory.LOVE -> "Tap any love template to paste into chat"
    }

    var templateList by remember(selectedCategory) {
        mutableStateOf(
            when (selectedCategory) {
                AutoTextCategory.GAME -> TemplateTextUtils.getTextGame(context)
                AutoTextCategory.APP -> TemplateTextUtils.getTextApp(context)
                AutoTextCategory.SALE -> TemplateTextUtils.getTextSale(context)
                AutoTextCategory.GREETING -> TemplateTextUtils.getTextGreeting(context)
                AutoTextCategory.LOVE -> TemplateTextUtils.getTextLove(context)
                AutoTextCategory.MY_CUSTOM -> emptyList()
            }
        )
    }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory != AutoTextCategory.MY_CUSTOM) {
            val featureType = when (selectedCategory) {
                AutoTextCategory.GAME -> KeyboardFeatureType.TEMPLATE_TEXT_GAME
                AutoTextCategory.APP -> KeyboardFeatureType.TEMPLATE_TEXT_APP
                AutoTextCategory.SALE -> KeyboardFeatureType.TEMPLATE_TEXT_SALE
                AutoTextCategory.GREETING -> KeyboardFeatureType.TEMPLATE_TEXT_GREETING
                AutoTextCategory.LOVE -> KeyboardFeatureType.TEMPLATE_TEXT_LOVE
                AutoTextCategory.MY_CUSTOM -> null
            }
            if (featureType != null) {
                templateList = TemplateTextUtils.getTemplatesForTypeSuspend(context, featureType)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        KeyboardFeatureToolbar(
            title = "Auto Text",
            subtitle = currentSubtitle,
            onBackClick = onBackClick,
            isAlwaysShow = isAlwaysShow,
            onToggleAlwaysShow = onToggleAlwaysShow,
            action = {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable {
                            if (selectedCategory == AutoTextCategory.MY_CUSTOM) {
                                onManageClick()
                            } else {
                                context.startActivity(Intent(context, TemplateTextActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    putExtra(TemplateTextActivity.EXTRA_INITIAL_CATEGORY, selectedCategory.name)
                                })
                            }
                        }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Manage Auto Text",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )

        // Sub-menu horizontal chip row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(AutoTextCategory.entries) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            text = "${category.icon} ${category.title}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }
        }

        HorizontalDivider(
            thickness = 0.8.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )

        // Content Area
        if (selectedCategory == AutoTextCategory.MY_CUSTOM) {
            if (autoTextList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FrogoEmptyView(
                        title = "No Auto Text Found",
                        subtitle = "Create reusable snippets to save time while typing"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = autoTextList,
                        key = { it.id }
                    ) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onCommitText(item.body) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.body,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    items = templateList,
                    key = { "${selectedCategory.id}_${it.id}" }
                ) { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCommitText(template.text) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
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
}
