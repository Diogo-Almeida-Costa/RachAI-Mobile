package org.example.rachai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.rachai.theme.RachaiRadius
import org.example.rachai.theme.RachaiSpacing

/** Card padrão com suporte a clique e rótulo de acessibilidade. */
@Composable
fun RachaiCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    semanticsLabel: String? = null,
    content: @Composable () -> Unit
) {
    val semanticsMod = if (semanticsLabel != null) {
        Modifier.semantics {
            contentDescription = semanticsLabel
            if (onClick != null) role = Role.Button
        }
    } else Modifier

    val clickMod = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Card(
        shape = RoundedCornerShape(RachaiRadius.card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .then(semanticsMod)
            .then(clickMod)
    ) {
        Column(modifier = Modifier.padding(RachaiSpacing.md)) {
            content()
        }
    }
}

/** Texto de valor monetário */
@Composable
fun AmountText(
    label: String,
    positive: Boolean,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium
) {
    Text(
        text = label,
        style = style,
        fontWeight = FontWeight.SemiBold,
        color = if (positive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    )
}

/** Card de um grupo na lista do Dashboard. */
@Composable
fun GroupCard(
    name: String,
    description: String,
    memberCount: Int,
    onClick: () -> Unit
) {
    val a11yText = "Grupo $name. " + (if (description.isNotBlank()) "$description. " else "") +
            "$memberCount ${if (memberCount == 1) "membro" else "membros"}"

    RachaiCard(onClick = onClick, semanticsLabel = a11yText) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.sizeIn(minHeight = 48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Group,
                    contentDescription = "Ícone de grupo",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = RachaiSpacing.md)
            ) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (description.isNotBlank()) description else "$memberCount ${if (memberCount == 1) "membro" else "membros"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Abrir grupo",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Linha de uma despesa dentro do grupo. */
@Composable
fun ExpenseRow(
    description: String,
    payerName: String,
    amountLabel: String,
    dateLabel: String,
    onClick: () -> Unit
) {
    val a11yText = "Despesa $description, valor $amountLabel, pago por $payerName em $dateLabel"

    RachaiCard(onClick = onClick, semanticsLabel = a11yText) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.sizeIn(minHeight = 48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Receipt,
                    contentDescription = "Ícone de recibo",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = RachaiSpacing.md)
            ) {
                Text(description, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(
                    "Pago por $payerName · $dateLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(amountLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Linha de saldo entre duas pessoas. */
@Composable
fun BalanceRow(
    debtorName: String,
    creditorName: String,
    amountLabel: String,
    settled: Boolean,
    onSettle: (() -> Unit)? = null
) {
    val a11yText = if (settled) {
        "Dívida de $debtorName para $creditorName no valor de $amountLabel: Quitado"
    } else {
        "$debtorName deve $amountLabel para $creditorName"
    }

    RachaiCard(semanticsLabel = a11yText) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.sizeIn(minHeight = 48.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "$debtorName deve para $creditorName",
                    style = MaterialTheme.typography.bodyMedium
                )
                AmountText(
                    label = amountLabel,
                    positive = settled,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            if (!settled && onSettle != null) {
                RachaiTextLink(text = "Quitar", onClick = onSettle)
            } else if (settled) {
                Text(
                    "Quitado",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/** Linha de um membro do grupo, com avatar e nome — usada em listas de membros. */
@Composable
fun MemberRow(
    firstName: String,
    lastName: String,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 48.dp)
            .padding(vertical = RachaiSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RachaiAvatar(firstName = firstName, lastName = lastName, size = 40.dp)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = RachaiSpacing.md)
        ) {
            Text("$firstName $lastName".trim(), style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        trailing?.invoke()
    }
}

/** Chip acessível usado para escolher quem participa da divisão de uma despesa. */
@Composable
fun SelectableMemberChip(
    firstName: String,
    lastName: String,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val displayName = firstName.ifBlank { lastName }
    Row(
        modifier = Modifier
            .sizeIn(minHeight = 48.dp)
            .clip(RoundedCornerShape(RachaiRadius.pill))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onToggle)
            .semantics {
                role = Role.Checkbox
                contentDescription = "$displayName, ${if (selected) "selecionado" else "desmarcado"}"
                stateDescription = if (selected) "selecionado" else "desmarcado"
            }
            .padding(horizontal = RachaiSpacing.md, vertical = RachaiSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(RachaiSpacing.xs)
    ) {
        RachaiAvatar(firstName = firstName, lastName = lastName, size = 24.dp)
        Text(
            displayName,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
