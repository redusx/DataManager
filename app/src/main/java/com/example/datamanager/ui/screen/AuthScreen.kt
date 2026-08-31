package com.example.datamanager.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.datamanager.R
import com.example.datamanager.ui.component.PinDots
import com.example.datamanager.ui.component.PinKeypad
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.ui.viewmodel.AuthMode
import com.example.datamanager.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    onBiometricRequested: (onSuccess: () -> Unit) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var hasAutoPromptedBiometric by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.mode) {
        if (uiState.mode == AuthMode.AUTHENTICATED) {
            onAuthenticated()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl, vertical = Spacing.l),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(Spacing.l))

            // Shield icon + App Title & Instruction
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.datamanager.R.drawable.app_icon),
                    contentDescription = "MyVault Logo",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                        )
                )

                Spacer(modifier = Modifier.height(Spacing.m))

                Text(
                    text = "MyVault",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                val subtitle = when (uiState.mode) {
                    AuthMode.SETUP_PIN -> stringResource(R.string.setup_pin)
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
                    isError = uiState.isError
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

            // Keypad
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

            Spacer(modifier = Modifier.height(Spacing.s))
        }
    }
}
