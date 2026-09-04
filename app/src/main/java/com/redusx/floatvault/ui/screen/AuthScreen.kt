package com.redusx.floatvault.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.redusx.floatvault.R
import com.redusx.floatvault.ui.component.PinDots
import com.redusx.floatvault.ui.component.PinKeypad
import com.redusx.floatvault.ui.theme.DarkSuccess
import com.redusx.floatvault.ui.theme.ShapeTokens
import com.redusx.floatvault.ui.theme.Spacing
import com.redusx.floatvault.ui.viewmodel.AuthMode
import com.redusx.floatvault.ui.viewmodel.AuthViewModel
import com.redusx.floatvault.util.BiometricHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    onBiometricRequested: (onSuccess: () -> Unit) -> Unit,
    onBiometricResetRequested: (onSuccess: () -> Unit, onError: () -> Unit) -> Unit,
    onDeviceCredentialRequested: (title: String, subtitle: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var hasAutoPromptedBiometric by rememberSaveable { mutableStateOf(false) }
    var showRecoveryOptionsSheet by remember { mutableStateOf(false) }
    var showWipeConfirmDialog by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(uiState.isUnlocking) {
        if (uiState.isUnlocking) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(uiState.mode) {
        if (uiState.mode == AuthMode.AUTHENTICATED) {
            onAuthenticated()
        }
    }

    LaunchedEffect(uiState.resetSuccessMessage) {
        uiState.resetSuccessMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearResetSuccessMessage()
        }
    }

    // Auto-prompt biometric once when entering PIN screen if enabled
    LaunchedEffect(uiState.mode, uiState.isBiometricAvailable) {
        if (uiState.mode == AuthMode.ENTER_PIN && uiState.isBiometricAvailable && !uiState.isLockedOut && !hasAutoPromptedBiometric) {
            hasAutoPromptedBiometric = true
            delay(250)
            onBiometricRequested {
                viewModel.onBiometricSuccess()
            }
        }
    }

    val isBiometricAvailable = remember(context) {
        BiometricHelper.canAuthenticate(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            // Landscape 2-Column Responsive Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.l, vertical = Spacing.s),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column: Logo, Title, Subtitle, Dots, Error & Forgot PIN
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "${stringResource(R.string.app_name)} Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(14.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val subtitle = when (uiState.mode) {
                        AuthMode.SETUP_PIN -> if (uiState.isResettingPinWithBiometric) "Yeni 6 Haneli PIN Belirleyin" else stringResource(R.string.setup_pin)
                        AuthMode.CONFIRM_PIN -> stringResource(R.string.confirm_pin)
                        AuthMode.ENTER_PIN -> stringResource(R.string.enter_pin)
                        else -> ""
                    }

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Spacing.s))

                    val currentPinLength = when (uiState.mode) {
                        AuthMode.SETUP_PIN, AuthMode.ENTER_PIN -> uiState.pin.length
                        AuthMode.CONFIRM_PIN -> uiState.confirmPin.length
                        else -> 0
                    }

                    PinDots(
                        pinLength = currentPinLength,
                        maxLength = 6,
                        isError = uiState.isError,
                        isSuccess = uiState.isUnlocking
                    )

                    // Error message
                    AnimatedVisibility(
                        visible = uiState.isError,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut()
                    ) {
                        val errorText = when (uiState.errorMessage) {
                            "pin_mismatch" -> stringResource(R.string.pin_mismatch)
                            "wrong_pin" -> stringResource(R.string.wrong_pin)
                            "setup_failed" -> stringResource(R.string.setup_failed)
                            else -> stringResource(R.string.error_generic)
                        }
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Lockout message
                    AnimatedVisibility(visible = uiState.isLockedOut) {
                        Text(
                            text = stringResource(R.string.locked_out),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    if (uiState.mode == AuthMode.ENTER_PIN) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        TextButton(
                            onClick = { showRecoveryOptionsSheet = true },
                            shape = ShapeTokens.ButtonRadius
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.xxs))
                            Text(
                                text = stringResource(R.string.forgot_pin),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.m))

                // Right Column: Keypad
                Column(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedVisibility(
                        visible = uiState.mode != AuthMode.LOADING && uiState.mode != AuthMode.AUTHENTICATED,
                        enter = fadeIn()
                    ) {
                        PinKeypad(
                            onDigitClick = { viewModel.onDigitEntered(it) },
                            onDeleteClick = { viewModel.onDeleteDigit() },
                            onBiometricClick = if (uiState.isBiometricAvailable && uiState.mode == AuthMode.ENTER_PIN) {
                                {
                                    onBiometricRequested {
                                        viewModel.onBiometricSuccess()
                                    }
                                }
                            } else null
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.xl, vertical = Spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(Spacing.s))

                // App Logo + Title & Instruction
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "${stringResource(R.string.app_name)} Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(20.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(Spacing.m))

                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    val subtitle = when (uiState.mode) {
                        AuthMode.SETUP_PIN -> if (uiState.isResettingPinWithBiometric) "Yeni 6 Haneli PIN Belirleyin" else stringResource(R.string.setup_pin)
                        AuthMode.CONFIRM_PIN -> stringResource(R.string.confirm_pin)
                        AuthMode.ENTER_PIN -> stringResource(R.string.enter_pin)
                        else -> ""
                    }

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // PIN indicator dots + Error message
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val currentPinLength = when (uiState.mode) {
                        AuthMode.SETUP_PIN, AuthMode.ENTER_PIN -> uiState.pin.length
                        AuthMode.CONFIRM_PIN -> uiState.confirmPin.length
                        else -> 0
                    }

                    PinDots(
                        pinLength = currentPinLength,
                        maxLength = 6,
                        isError = uiState.isError,
                        isSuccess = uiState.isUnlocking
                    )

                    Spacer(modifier = Modifier.height(Spacing.m))

                    // Error message
                    AnimatedVisibility(
                        visible = uiState.isError,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut()
                    ) {
                        val errorText = when (uiState.errorMessage) {
                            "pin_mismatch" -> stringResource(R.string.pin_mismatch)
                            "wrong_pin" -> stringResource(R.string.wrong_pin)
                            "setup_failed" -> stringResource(R.string.setup_failed)
                            else -> stringResource(R.string.error_generic)
                        }
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Lockout message
                    AnimatedVisibility(visible = uiState.isLockedOut) {
                        Text(
                            text = stringResource(R.string.locked_out),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = Spacing.xs)
                        )
                    }
                }

                // Keypad + Forgot Password Action
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = uiState.mode != AuthMode.LOADING && uiState.mode != AuthMode.AUTHENTICATED,
                        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
                    ) {
                        PinKeypad(
                            onDigitClick = { viewModel.onDigitEntered(it) },
                            onDeleteClick = { viewModel.onDeleteDigit() },
                            onBiometricClick = if (uiState.isBiometricAvailable && uiState.mode == AuthMode.ENTER_PIN) {
                                {
                                    onBiometricRequested {
                                        viewModel.onBiometricSuccess()
                                    }
                                }
                            } else null,
                            modifier = Modifier.padding(horizontal = Spacing.s)
                        )
                    }

                    if (uiState.mode == AuthMode.ENTER_PIN) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        TextButton(
                            onClick = { showRecoveryOptionsSheet = true },
                            shape = ShapeTokens.ButtonRadius
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.xxs))
                            Text(
                                text = stringResource(R.string.forgot_pin),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xs))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Modal BottomSheet with Recovery Options (Option 1 vs Option 2)
    if (showRecoveryOptionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRecoveryOptionsSheet = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = ShapeTokens.BottomSheetRadius,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.m)
                    .padding(bottom = Spacing.xxl)
            ) {
                Text(
                    text = stringResource(R.string.pin_recovery_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(Spacing.xxs))

                Text(
                    text = stringResource(R.string.pin_recovery_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.m))

                // Option 1: Biometric PIN Reset (Keeps Data)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShapeTokens.CardRadius)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(
                            width = 1.dp,
                            color = if (isBiometricAvailable) DarkSuccess.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant,
                            shape = ShapeTokens.CardRadius
                        )
                        .clickable(enabled = isBiometricAvailable) {
                            showRecoveryOptionsSheet = false
                            onBiometricResetRequested(
                                { viewModel.onBiometricResetVerified() },
                                {
                                    Toast.makeText(context, "Parmak izi doğrulanamadı", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        .padding(Spacing.m)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isBiometricAvailable) DarkSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Fingerprint,
                                contentDescription = null,
                                tint = if (isBiometricAvailable) DarkSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(Spacing.m))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.option_biometric_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(Spacing.xs))
                                Box(
                                    modifier = Modifier
                                        .clip(ShapeTokens.BadgeRadius)
                                        .background(DarkSuccess.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.badge_data_kept),
                                        style = MaterialTheme.typography.labelSmall.copy(color = DarkSuccess)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBiometricAvailable)
                                    stringResource(R.string.option_biometric_desc)
                                else
                                    stringResource(R.string.option_biometric_not_available),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.s))

                // Option 2: Factory Reset (Protected by Phone Screen Lock)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShapeTokens.CardRadius)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.4f),
                            shape = ShapeTokens.CardRadius
                        )
                        .clickable {
                            showRecoveryOptionsSheet = false
                            showWipeConfirmDialog = true
                        }
                        .padding(Spacing.m)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteForever,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(Spacing.m))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.option_wipe_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(Spacing.xs))
                                Box(
                                    modifier = Modifier
                                        .clip(ShapeTokens.BadgeRadius)
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.badge_data_wiped),
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.option_wipe_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Factory Reset Confirmation Dialog (Protected by Phone Screen Lock)
    if (showWipeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showWipeConfirmDialog = false },
            shape = ShapeTokens.DialogRadius,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.reset_vault_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        text = stringResource(R.string.reset_vault_desc_1),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.reset_vault_desc_2),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stringResource(R.string.reset_vault_desc_3),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWipeConfirmDialog = false
                        onDeviceCredentialRequested(
                            context.getString(R.string.device_credential_prompt_title),
                            context.getString(R.string.device_credential_prompt_desc),
                            {
                                viewModel.wipeAndResetVault()
                            },
                            {
                                Toast.makeText(context, context.getString(R.string.verification_failed), Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = ShapeTokens.ButtonRadius
                ) {
                    Text(
                        text = stringResource(R.string.reset_with_phone_password),
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showWipeConfirmDialog = false },
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

    // First-Time Biometric / Fingerprint Activation Dialog
    if (uiState.showBiometricPromptDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissBiometricPrompt() },
            shape = ShapeTokens.DialogRadius,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Fingerprint,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.enable_biometric_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.s)
                ) {
                    Text(
                        text = stringResource(R.string.enable_biometric_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ShapeTokens.CardRadius)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                                shape = ShapeTokens.CardRadius
                            )
                            .padding(Spacing.s)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text(
                                text = stringResource(R.string.enable_biometric_settings_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEnableBiometricConfirmed() },
                    shape = ShapeTokens.ButtonRadius
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(stringResource(R.string.enable_biometric_confirm))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.onDismissBiometricPrompt() },
                    shape = ShapeTokens.ButtonRadius
                ) {
                    Text(stringResource(R.string.enable_biometric_skip))
                }
            }
        )
    }
}
