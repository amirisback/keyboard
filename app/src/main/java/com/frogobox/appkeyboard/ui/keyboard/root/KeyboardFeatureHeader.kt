package com.frogobox.appkeyboard.ui.keyboard.root

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.model.KeyboardFeatureModel
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.ui.theme.compose.FrogoKeyboardTheme

/**
 * Modern Jetpack Compose Feature Header Bar.
 * Renders feature shortcuts with clean, balanced positioning:
 * - When items fit comfortably (<= 5 items), items are evenly distributed across full width with equal columns.
 * - When there are more items, renders a smooth horizontal scrollable row with uniform item slots.
 */
@Composable
fun KeyboardFeatureHeader(
    features: List<KeyboardFeatureModel>,
    onFeatureClick: (KeyboardFeatureModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (features.isEmpty()) return

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val totalWidth = maxWidth
        val minComfortableWidth = 70.dp
        val canFitEvenly = features.size <= 5 && (totalWidth / features.size) >= minComfortableWidth

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (canFitEvenly) {
                // Symmetrical equal-width distribution across the full keyboard width
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 4.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    features.forEach { feature ->
                        KeyboardFeatureHeaderItem(
                            feature = feature,
                            onClick = { onFeatureClick(feature) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            } else {
                // Horizontally scrollable row with uniform item width for consistent spacing & peeking indicator
                val itemWidth = (totalWidth / 4.25f).coerceIn(72.dp, 92.dp)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(
                        items = features,
                        key = { it.id }
                    ) { feature ->
                        KeyboardFeatureHeaderItem(
                            feature = feature,
                            onClick = { onFeatureClick(feature) },
                            modifier = Modifier
                                .width(itemWidth)
                                .fillMaxHeight()
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
            )
        }
    }
}

@Composable
private fun KeyboardFeatureHeaderItem(
    feature: KeyboardFeatureModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = feature.icon),
                contentDescription = feature.text,
                modifier = Modifier.size(22.dp),
                contentScale = ContentScale.Fit
            )
        }

        Text(
            text = feature.text,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        )
    }
}

@Preview(name = "Header 4 Items Even Distribution", showBackground = true, widthDp = 360)
@Composable
private fun KeyboardFeatureHeaderFourItemsPreview() {
    FrogoKeyboardTheme {
        val sampleFeatures = listOf(
            KeyboardFeatureType.AUTO_TEXT.mapToModel(),
            KeyboardFeatureType.MOVIE.mapToModel(),
            KeyboardFeatureType.CHANGE_KEYBOARD.mapToModel(),
            KeyboardFeatureType.SETTING.mapToModel()
        )
        KeyboardFeatureHeader(
            features = sampleFeatures,
            onFeatureClick = {}
        )
    }
}

@Preview(name = "Header Scrollable Many Items", showBackground = true, widthDp = 360)
@Composable
private fun KeyboardFeatureHeaderManyItemsPreview() {
    FrogoKeyboardTheme {
        val sampleFeatures = listOf(
            KeyboardFeatureType.AUTO_TEXT.mapToModel(),
            KeyboardFeatureType.MOVIE.mapToModel(),
            KeyboardFeatureType.NEWS.mapToModel(),
            KeyboardFeatureType.WEB.mapToModel(),
            KeyboardFeatureType.FORM.mapToModel(),
            KeyboardFeatureType.CHANGE_KEYBOARD.mapToModel(),
            KeyboardFeatureType.SETTING.mapToModel()
        )
        KeyboardFeatureHeader(
            features = sampleFeatures,
            onFeatureClick = {}
        )
    }
}
