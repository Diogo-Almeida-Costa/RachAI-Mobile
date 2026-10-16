package org.example.rachai.ui.expense

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.rachai.theme.RachaiSpacing

@Composable
fun StepIndicator(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val step1Completed = currentStep > 1
    val step1Active = currentStep == 1
    val step2Active = currentStep == 2

    val a11yDescription = "Etapa $currentStep de 2: " + if (currentStep == 1) "Detalhes" else "Rateio"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = RachaiSpacing.lg, vertical = RachaiSpacing.sm)
            .semantics { contentDescription = a11yDescription },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Step 1: Detalhes
        StepCircle(
            stepNumber = 1,
            isCompleted = step1Completed,
            isActive = step1Active
        )
        Spacer(modifier = Modifier.width(RachaiSpacing.xs))
        Text(
            text = "Detalhes",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (step1Active || step1Completed) FontWeight.SemiBold else FontWeight.Normal,
            color = if (step1Active || step1Completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Conector intermediário
        val dividerColor by animateColorAsState(
            if (currentStep >= 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = RachaiSpacing.md),
            thickness = 2.dp,
            color = dividerColor
        )

        // Step 2: Rateio
        StepCircle(
            stepNumber = 2,
            isCompleted = false,
            isActive = step2Active
        )
        Spacer(modifier = Modifier.width(RachaiSpacing.xs))
        Text(
            text = "Rateio",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (step2Active) FontWeight.SemiBold else FontWeight.Normal,
            color = if (step2Active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StepCircle(
    stepNumber: Int,
    isCompleted: Boolean,
    isActive: Boolean
) {
    val backgroundColor = when {
        isCompleted || isActive -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    val contentColor = when {
        isCompleted || isActive -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (!isActive && !isCompleted) {
                    Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
        } else {
            Text(
                text = stepNumber.toString(),
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
