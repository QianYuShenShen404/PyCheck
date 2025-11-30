package com.example.codechecker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.codechecker.ui.theme.HighRisk
import com.example.codechecker.ui.theme.MediumRisk
import com.example.codechecker.ui.theme.LowRisk

@Composable
fun CodeCard(
    fileName: String,
    similarityScore: Float,
    isHighRisk: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (color, label) = when {
        similarityScore >= 80f -> HighRisk to "高风险"
        similarityScore >= 60f -> MediumRisk to "中等风险"
        else -> LowRisk to "低风险"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color.copy(alpha = 0.1f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Code File",
                    tint = color,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "相似度: ${"%.1f".format(similarityScore)}% - $label",
                        style = MaterialTheme.typography.bodySmall,
                        color = color
                    )
                }
            }
        }
    }
}
