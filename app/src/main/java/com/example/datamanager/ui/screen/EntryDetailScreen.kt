package com.example.datamanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.datamanager.R
import com.example.datamanager.data.model.Category
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.data.model.TemplateType
import com.example.datamanager.ui.component.CopyButton
import com.example.datamanager.ui.component.SensitiveField
import com.example.datamanager.ui.component.getEntryVisuals
import com.example.datamanager.ui.component.templates.PhysicalCardPreview
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.ui.viewmodel.EntryViewModel
import com.example.datamanager.util.ClipboardHelper
import com.example.datamanager.util.FieldFormatter.formatFieldLabel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    entryId: Long,
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(entryId) {
        viewModel.loadEntryForViewing(entryId)
    }

    val entry = uiState.viewEntry
    val fields = uiState.viewFields
    val templateType = uiState.viewTemplateType

    fun getFieldValue(key: String): String = fields.firstOrNull { it.key == key }?.value ?: ""

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = entry?.title ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(Spacing.touchTargetMin)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Geri",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (entry != null) {
                        IconButton(
                            onClick = { onEditClick(entryId) },
                            modifier = Modifier.size(Spacing.touchTargetMin)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = stringResource(R.string.edit_entry),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.size(Spacing.touchTargetMin)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = stringResource(R.string.delete_entry),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (entry != null) {
            val category = Category.fromId(entry.category)
            val (categoryIcon, categoryTint) = getEntryVisuals(category, fields)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(Spacing.m),
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                // Category badge & Template type header
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(ShapeTokens.BadgeRadius)
                                .background(categoryTint.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                tint = categoryTint,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(Spacing.xs))

                        Text(
                            text = templateType.title,
                            style = MaterialTheme.typography.labelMedium,
                            color = categoryTint
                        )
                    }
                }

                // If Payment Card: Show Interactive Physical Card Preview
                if (templateType == TemplateType.CARD) {
                    item {
                        val cardNumber = getFieldValue("card_number")
                        val expiryDate = getFieldValue("expiry_date")
                        val cvv = getFieldValue("cvv")
                        val cardHolder = getFieldValue("card_holder")
                        val cardBrand = when {
                            cardNumber.startsWith("4") -> "VISA"
                            cardNumber.startsWith("5") || cardNumber.startsWith("2") -> "MASTERCARD"
                            cardNumber.startsWith("9792") -> "TROY"
                            cardNumber.startsWith("34") || cardNumber.startsWith("37") -> "AMEX"
                            else -> "KART"
                        }

                        PhysicalCardPreview(
                            title = entry.title,
                            cardNumber = cardNumber,
                            expiryDate = expiryDate,
                            cvv = cvv,
                            cardHolder = cardHolder,
                            cardBrand = cardBrand
                        )
                    }
                }

                // Field Rows with Monospace & 30s auto-remask
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.s),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        fields.forEach { field ->
                            if (field.value.isNotEmpty()) {
                                val readableLabel = formatFieldLabel(field.key)
                                SensitiveField(
                                    label = readableLabel,
                                    value = field.value,
                                    isSensitive = field.isSensitive,
                                    onCopy = { secret ->
                                        ClipboardHelper.copyToClipboard(
                                            context = context,
                                            label = readableLabel,
                                            text = secret,
                                            isSensitive = field.isSensitive
                                        )
                                        scope.launch {
                                            snackbarHostState.showSnackbar("$readableLabel kopyalandı")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Destructive Delete Confirmation Dialog
    if (showDeleteDialog && entry != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = ShapeTokens.DialogRadius,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    text = stringResource(R.string.delete_entry),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_entry_confirm),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEntry(entry)
                        showDeleteDialog = false
                        onBackClick()
                    },
                    shape = ShapeTokens.ButtonRadius,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    shape = ShapeTokens.ButtonRadius
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
    }
}
