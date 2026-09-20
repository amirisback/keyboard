package com.frogobox.appkeyboard.ui.keyboard.root

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frogobox.appkeyboard.R
import com.frogobox.appkeyboard.suggestion.SuggestionResult

/**
 * Candidate types corresponding to the 3 predictive word strip sections.
 */
enum class CandidateType {
    USER_WORD,
    PREDICTED_WORD,
    AUTOCORRECT_WORD
}

/**
 * Modern Jetpack Compose Candidate Suggestion Bar.
 * Presents 3 distinct candidate sections (literal user input, hero predicted word, auto-correct alternative)
 * along with quick switch and dismiss action controls.
 */
@Composable
fun KeyboardSuggestionBar(
    result: SuggestionResult,
    onCandidateSelected: (String, CandidateType) -> Unit,
    onSwitchMenu: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Switch Menu Button (Far Left)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onSwitchMenu)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_suggestion_switch),
                    contentDescription = "Switch to Feature Menu",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            // Section 1: User's Word (Literal Raw Input)
            val hasUserWord = result.userWord.isNotEmpty()
            if (hasUserWord) {
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCandidateSelected(result.userWord, CandidateType.USER_WORD) }
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "“${result.userWord}”",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1.0f))
            }

            // Divider 1
            if (hasUserWord && result.predictedWord.isNotEmpty()) {
                VerticalDivider(
                    modifier = Modifier
                        .height(20.dp)
                        .width(1.dp)
                        .padding(horizontal = 1.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }

            // Section 2: Predicted Word (Center Hero Candidate Pill)
            val hasPredicted = result.predictedWord.isNotEmpty()
            if (hasPredicted) {
                Surface(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onCandidateSelected(result.predictedWord, CandidateType.PREDICTED_WORD) },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    shadowElevation = 1.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = result.predictedWord,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1.2f))
            }

            // Divider 2
            val hasAutocorrect = result.autoCorrectWord.isNotEmpty() &&
                    result.autoCorrectWord != result.predictedWord
            if (hasAutocorrect && (hasPredicted || hasUserWord)) {
                VerticalDivider(
                    modifier = Modifier
                        .height(20.dp)
                        .width(1.dp)
                        .padding(horizontal = 1.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }

            // Section 3: Auto-Correct Word (Alternative Fix)
            if (hasAutocorrect) {
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCandidateSelected(result.autoCorrectWord, CandidateType.AUTOCORRECT_WORD) }
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.autoCorrectWord,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1.0f))
            }

            Spacer(modifier = Modifier.width(2.dp))

            // Close Button (Far Right)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClose)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_suggestion),
                    contentDescription = "Clear Suggestions",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        HorizontalDivider(
            thickness = 0.8.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    }
}
