package com.example.datamanager.ui.screen

import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.example.datamanager.data.model.TemplateType
import com.example.datamanager.service.OverlayService
import com.example.datamanager.ui.component.CategoryChipRow
import com.example.datamanager.ui.component.EmptyVaultState
import com.example.datamanager.ui.component.EntryCard
import com.example.datamanager.ui.component.OverlayLaunchCard
import com.example.datamanager.ui.component.SearchEmptyState
import com.example.datamanager.ui.component.TemplateSelectorBottomSheet
import com.example.datamanager.ui.component.VaultSearchBar
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.ui.viewmodel.HomeViewModel
import com.example.datamanager.util.ClipboardHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEntryClick: (Long) -> Unit,
    onAddClick: (category: String?, templateId: String?) -> Unit,
    onSettingsClick: () -> Unit,
    onLockClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showTemplateSheet by remember { mutableStateOf(false) }

    val onLaunchFloatingAccess: () -> Unit = {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }

        if (hasPermission) {
            OverlayService.start(context)
            (context as? Activity)?.moveTaskToBack(true)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.overlay_permission_desc),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    if (showTemplateSheet) {
        TemplateSelectorBottomSheet(
            onDismiss = { showTemplateSheet = false },
            onSelectTemplate = { selectedType ->
                showTemplateSheet = false
                onAddClick(selectedType.category.id, selectedType.id)
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.datamanager.R.drawable.app_icon),
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(7.dp))
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = "MyVault",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onLockClick,
                        modifier = Modifier.size(Spacing.touchTargetMin)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Kasayı Kilitle",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(Spacing.touchTargetMin)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState.totalCount > 0,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { showTemplateSheet = true },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.add_entry),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Zone 3: Search Anchor
            VaultSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onClear = viewModel::clearSearch,
                modifier = Modifier.padding(horizontal = Spacing.m, vertical = Spacing.xs)
            )

            // Zone 4: Utility Overlay Strip
            OverlayLaunchCard(
                onClick = onLaunchFloatingAccess,
                modifier = Modifier.padding(horizontal = Spacing.m, vertical = Spacing.xxs)
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Zone 5: Category Filter Chips
            CategoryChipRow(
                selectedCategoryId = uiState.selectedCategory,
                onSelectCategory = viewModel::onCategorySelected,
                categoryCounts = uiState.categoryCounts,
                totalCount = uiState.totalCount
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Zone 6: Main Content List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    // Empty Vault (0 records in DB)
                    uiState.totalCount == 0 && !uiState.isLoading -> {
                        EmptyVaultState(
                            onAddClick = { showTemplateSheet = true },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Search Yields No Results
                    uiState.entries.isEmpty() && uiState.searchQuery.isNotEmpty() -> {
                        SearchEmptyState(
                            query = uiState.searchQuery,
                            onClearSearch = viewModel::clearSearch,
                            onAddWithQuery = { showTemplateSheet = true },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Content List
                    else -> {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(
                                start = Spacing.m,
                                end = Spacing.m,
                                top = Spacing.xs,
                                bottom = 80.dp // Padding for FAB
                            ),
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.entries,
                                key = { it.id }
                            ) { entry ->
                                EntryCard(
                                    entry = entry,
                                    onCardClick = { onEntryClick(entry.id) },
                                    onCopyClick = { secret ->
                                        ClipboardHelper.copyToClipboard(
                                            context = context,
                                            label = entry.title,
                                            text = secret,
                                            isSensitive = true
                                        )
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "${entry.title} kopyalandı",
                                                withDismissAction = true
                                            )
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
}
