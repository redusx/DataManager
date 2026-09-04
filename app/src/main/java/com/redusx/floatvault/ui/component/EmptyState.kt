package com.redusx.floatvault.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.redusx.floatvault.R
import com.redusx.floatvault.ui.theme.ShapeTokens
import com.redusx.floatvault.ui.theme.Spacing

@Composable
fun EmptyVaultState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Shield,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.l))

        Text(
            text = "Kasanız Henüz Boş",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Text(
            text = "Kişisel kimlik, kart veya şifrelerinizi güvenle ekleyerek tek tıkla kopyalayın.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.m)
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Button(
            onClick = onAddClick,
            shape = ShapeTokens.ButtonRadius,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = "İlk Kaydınızı Ekleyin",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun SearchEmptyState(
    query: String,
    onClearSearch: () -> Unit,
    onAddWithQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.m))

        Text(
            text = "Sonuç Bulunamadı",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xxs))

        Text(
            text = "\"$query\" aramasıyla eşleşen herhangi bir kayıt bulunamadı.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.m)
        )

        Spacer(modifier = Modifier.height(Spacing.l))

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            OutlinedButton(
                onClick = onClearSearch,
                shape = ShapeTokens.ButtonRadius,
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    text = "Aramayı Temizle",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Button(
                onClick = onAddWithQuery,
                shape = ShapeTokens.ButtonRadius,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.height(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xxs))
                Text(
                    text = "Bu İsimle Ekle",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
