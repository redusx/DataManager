package com.example.datamanager.ui.screen

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.datamanager.R
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.ui.viewmodel.SettingsViewModel
import com.example.datamanager.util.BiometricHelper
import com.example.datamanager.util.BiometricStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onDataDeleted: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    val noEnrolledMsg = stringResource(R.string.biometric_none_enrolled)
    val noHardwareMsg = stringResource(R.string.biometric_no_hardware)
    val notAvailableMsg = stringResource(R.string.biometric_not_available)
    val overlayPermMsg = stringResource(R.string.overlay_permission_desc)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            // Security Section
            item {
                SectionHeader(stringResource(R.string.security))
            }

            item {
                SettingsRow(
                    icon = Icons.Rounded.Lock,
                    title = stringResource(R.string.change_pin),
                    onClick = { viewModel.showChangePinDialog() }
                )
            }

            item {
                SettingsToggleRow(
                    icon = Icons.Rounded.Fingerprint,
                    title = stringResource(R.string.biometric_auth),
                    subtitle = stringResource(R.string.biometric_auth_desc),
                    checked = uiState.isBiometricEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            when (BiometricHelper.getBiometricStatus(context)) {
                                BiometricStatus.AVAILABLE -> {
                                    viewModel.setBiometricEnabled(true)
                                }
                                BiometricStatus.NOT_ENROLLED -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(noEnrolledMsg)
                                    }
                                }
                                BiometricStatus.NO_HARDWARE -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(noHardwareMsg)
                                    }
                                }
                                else -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(notAvailableMsg)
                                    }
                                }
                            }
                        } else {
                            viewModel.setBiometricEnabled(false)
                        }
                    }
                )
            }

            // Quick Access / Overlay Section
            item {
                Spacer(modifier = Modifier.height(Spacing.xs))
                SectionHeader(stringResource(R.string.overlay_channel_name))
            }

            item {
                SettingsToggleRow(
                    icon = Icons.Rounded.Layers,
                    title = stringResource(R.string.overlay_quick_access),
                    subtitle = stringResource(R.string.overlay_quick_access_desc),
                    checked = uiState.isOverlayEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            if (viewModel.hasOverlayPermission()) {
                                viewModel.setOverlayEnabled(true)
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(overlayPermMsg)
                                    }
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    ).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        } else {
                            viewModel.setOverlayEnabled(false)
                        }
                    }
                )
            }

            // Data Section
            item {
                Spacer(modifier = Modifier.height(Spacing.xs))
                SectionHeader(stringResource(R.string.data))
            }

            item {
                SettingsRow(
                    icon = Icons.Rounded.Delete,
                    title = stringResource(R.string.delete_all_data),
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = { viewModel.showDeleteDataDialog() }
                )
            }

            // About Section
            item {
                Spacer(modifier = Modifier.height(Spacing.xs))
                SectionHeader(stringResource(R.string.about))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShapeTokens.CardRadius)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = ShapeTokens.CardRadius
                        )
                        .padding(Spacing.m)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.s))
                        Column {
                            Text(
                                text = "MyVault v1.0",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.about_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Change PIN dialog (Numeric-only 4-6 digits)
    if (uiState.showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideChangePinDialog() },
            shape = ShapeTokens.DialogRadius,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    text = stringResource(R.string.change_pin),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    OutlinedTextField(
                        value = uiState.changePinOldPin,
                        onValueChange = viewModel::onChangePinOldPinChanged,
                        label = { Text(stringResource(R.string.old_pin)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeTokens.InputRadius,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    OutlinedTextField(
                        value = uiState.changePinNewPin,
                        onValueChange = viewModel::onChangePinNewPinChanged,
                        label = { Text(stringResource(R.string.new_pin)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeTokens.InputRadius,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    OutlinedTextField(
                        value = uiState.changePinConfirmPin,
                        onValueChange = viewModel::onChangePinConfirmPinChanged,
                        label = { Text(stringResource(R.string.confirm_pin_label)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeTokens.InputRadius,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    if (uiState.changePinError != null) {
                        val errorText = when (uiState.changePinError) {
                            "pin_mismatch" -> stringResource(R.string.pin_mismatch)
                            "pin_too_short" -> stringResource(R.string.pin_too_short)
                            "wrong_old_pin" -> stringResource(R.string.wrong_old_pin)
                            else -> stringResource(R.string.error_generic)
                        }
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.changePin() },
                    shape = ShapeTokens.ButtonRadius,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.hideChangePinDialog() },
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

    // Delete all data dialog
    if (uiState.showDeleteDataDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDataDialog() },
            shape = ShapeTokens.DialogRadius,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.delete_all_data),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_all_data_confirm),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.deleteAllData()
                            onDataDeleted()
                        }
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
                    onClick = { viewModel.hideDeleteDataDialog() },
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

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = Spacing.xxs, top = Spacing.xs, bottom = Spacing.xxs)
    )
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShapeTokens.CardRadius)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = ShapeTokens.CardRadius
            )
            .clickable(onClick = onClick)
            .padding(Spacing.m),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = titleColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.s))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShapeTokens.CardRadius)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = ShapeTokens.CardRadius
            )
            .padding(Spacing.m),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.s))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(Spacing.s))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
