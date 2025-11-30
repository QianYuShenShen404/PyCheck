package com.example.codechecker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.codechecker.ui.theme.HighRisk
import com.example.codechecker.ui.theme.MediumRisk
import com.example.codechecker.ui.theme.LowRisk

@Composable
fun RiskBadge(
    riskLevel: String,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (riskLevel.lowercase()) {
        "high", "高" -> HighRisk to "高风险"
        "medium", "中等" -> MediumRisk to "中等"
        else -> LowRisk to "低风险"
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .background(color, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
