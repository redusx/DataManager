package com.example.datamanager.ui.screen

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Security section
            item {
                SectionHeader(stringResource(R.string.security))
            }

            item {
                SettingsRow(
                    icon = Icons.Filled.Lock,
                    title = stringResource(R.string.change_pin),
                    onClick = { viewModel.showChangePinDialog() }
                )
            }

            item {
                SettingsToggleRow(
                    icon = Icons.Filled.Fingerprint,
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

            // Quick Access / Overlay section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(stringResource(R.string.overlay_channel_name))
            }

            item {
                SettingsToggleRow(
                    icon = Icons.Filled.Layers,
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

            // Data section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(stringResource(R.string.data))
            }

            item {
                SettingsRow(
                    icon = Icons.Filled.Delete,
                    title = stringResource(R.string.delete_all_data),
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = { viewModel.showDeleteDataDialog() }
                )
            }

            // About section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(stringResource(R.string.about))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "DataManager v1.0",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
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

    // Change PIN dialog
    if (uiState.showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideChangePinDialog() },
            title = { Text(stringResource(R.string.change_pin)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.changePinOldPin,
                        onValueChange = viewModel::onChangePinOldPinChanged,
                        label = { Text(stringResource(R.string.old_pin)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = uiState.changePinNewPin,
                        onValueChange = viewModel::onChangePinNewPinChanged,
                        label = { Text(stringResource(R.string.new_pin)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = uiState.changePinConfirmPin,
                        onValueChange = viewModel::onChangePinConfirmPinChanged,
                        label = { Text(stringResource(R.string.confirm_pin_label)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    if (uiState.changePinError != null) {
                        val errorText = when (uiState.changePinError) {
                            "pin_mismatch" -> stringResource(R.string.pin_mismatch)
                            "pin_too_short" -> stringResource(R.string.pin_too_short)
                            "wrong_old_pin" -> stringResource(R.string.wrong_old_pin)
                            else -> stringResource(R.string.error_generic)
                        }
                        Text(text = errorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.changePin() }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideChangePinDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Delete all data dialog
    if (uiState.showDeleteDataDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDataDialog() },
            icon = { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text(stringResource(R.string.delete_all_data)) },
            text = { Text(stringResource(R.string.delete_all_data_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.deleteAllData()
                            onDataDeleted()
                        }
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteDataDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp)
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
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = titleColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onClick, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
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
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
