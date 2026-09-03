package com.example.datamanager.ui.screen

import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButtonDefaults
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
import android.content.res.Configuration
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.Button
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.datamanager.ui.component.CategoryCard
import com.example.datamanager.ui.theme.CategoryCardsTint
import com.example.datamanager.ui.theme.CategoryIdentityTint
import com.example.datamanager.ui.theme.CategoryLoginsTint
import com.example.datamanager.ui.theme.CategoryNotesTint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Settings
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
import com.example.datamanager.ui.theme.ShapeTokens
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

    BackHandler(enabled = uiState.isViewingEntries) {
        viewModel.returnToCategoryGrid()
    }

    val categoryGridItems = remember(uiState.categoryCounts) {
        listOf(
            CategoryGridItem(
                id = "financial",
                titleRes = R.string.category_financial,
                subtitleRes = R.string.category_financial_desc,
                icon = Icons.Rounded.CreditCard,
                tint = CategoryCardsTint,
                count = uiState.categoryCounts["financial"] ?: 0
            ),
            CategoryGridItem(
                id = "personal",
                titleRes = R.string.category_personal,
                subtitleRes = R.string.category_personal_desc,
                icon = Icons.Rounded.AccountCircle,
                tint = CategoryIdentityTint,
                count = uiState.categoryCounts["personal"] ?: 0
            ),
            CategoryGridItem(
                id = "account",
                titleRes = R.string.category_accounts,
                subtitleRes = R.string.category_accounts_desc,
                icon = Icons.Rounded.Lock,
                tint = CategoryLoginsTint,
                count = uiState.categoryCounts["account"] ?: 0
            ),
            CategoryGridItem(
                id = "custom",
                titleRes = R.string.category_custom,
                subtitleRes = R.string.category_custom_desc,
                icon = Icons.Rounded.Description,
                tint = CategoryNotesTint,
                count = uiState.categoryCounts["custom"] ?: 0
            )
        )
    }

    val currentCategoryTitle = when {
        uiState.searchQuery.isNotEmpty() -> stringResource(R.string.search_placeholder)
        uiState.selectedCategory == "financial" -> stringResource(R.string.category_financial)
        uiState.selectedCategory == "personal" -> stringResource(R.string.category_personal)
        uiState.selectedCategory == "account" -> stringResource(R.string.category_accounts)
        uiState.selectedCategory == "custom" -> stringResource(R.string.category_custom)
        else -> stringResource(R.string.category_all_title)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    AnimatedVisibility(
                        visible = uiState.isViewingEntries,
                        enter = fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.8f),
                        exit = fadeOut(tween(180)) + scaleOut(tween(180), targetScale = 0.8f)
                    ) {
                        IconButton(
                            onClick = viewModel::returnToCategoryGrid,
                            modifier = Modifier.size(Spacing.touchTargetMin)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                title = {
                    AnimatedContent(
                        targetState = if (uiState.isViewingEntries) currentCategoryTitle else "MyVault",
                        transitionSpec = {
                            (fadeIn(tween(240)) + slideInVertically(tween(240)) { it / 3 })
                                .togetherWith(fadeOut(tween(180)) + slideOutVertically(tween(180)) { -it / 3 })
                        },
                        label = "TopBarTitleAnim"
                    ) { titleText ->
                        if (uiState.isViewingEntries) {
                            Text(
                                text = titleText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
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
                                    text = titleText,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
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
                    shape = RoundedCornerShape(18.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 5.dp
                    ),
                    modifier = Modifier
                        .size(56.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(18.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.add_entry),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        val gridState = rememberLazyGridState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Zone 3: Search Anchor (Present on both pages)
            VaultSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onClear = viewModel::clearSearch,
                modifier = Modifier.padding(horizontal = Spacing.m, vertical = Spacing.xs)
            )

            // Zone 4: Utility Overlay Strip (Shown on both main screen and category pages)
            OverlayLaunchCard(
                onClick = onLaunchFloatingAccess,
                modifier = Modifier.padding(horizontal = Spacing.m, vertical = Spacing.xxs)
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Zone 5: Category Filter Chips (Only on category pages, animated expand/collapse)
            AnimatedVisibility(
                visible = uiState.isViewingEntries,
                enter = expandVertically(tween(240)) + fadeIn(tween(220)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(180))
            ) {
                CategoryChipRow(
                    selectedCategoryId = uiState.selectedCategory,
                    onSelectCategory = { categoryId ->
                        viewModel.openCategory(categoryId)
                    },
                    categoryCounts = uiState.categoryCounts,
                    totalCount = uiState.totalCount,
                    modifier = Modifier.padding(bottom = Spacing.xs)
                )
            }

            // Zone 6: Content Area (Category Grid OR Entry List with smooth slide/fade transition)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = uiState.isViewingEntries,
                    transitionSpec = {
                        if (targetState) {
                            // Forward: Grid -> Entries
                            (slideInHorizontally(
                                initialOffsetX = { fullWidth -> (fullWidth * 0.15f).toInt() },
                                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(durationMillis = 240)))
                            .togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -(fullWidth * 0.12f).toInt() },
                                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                                ) + fadeOut(animationSpec = tween(durationMillis = 180))
                            )
                        } else {
                            // Backward: Entries -> Grid
                            (slideInHorizontally(
                                initialOffsetX = { fullWidth -> -(fullWidth * 0.12f).toInt() },
                                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(durationMillis = 240)))
                            .togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> (fullWidth * 0.15f).toInt() },
                                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                                ) + fadeOut(animationSpec = tween(durationMillis = 180))
                            )
                        }
                    },
                    label = "CategoryTransitionAnim",
                    modifier = Modifier.fillMaxSize()
                ) { isViewingEntries ->
                    if (!isViewingEntries) {
                        // Page 1: 2-Column Category Grid
                        LazyVerticalGrid(
                        columns = GridCells.Fixed(if (isLandscape) 3 else 2),
                        contentPadding = PaddingValues(
                            start = Spacing.m,
                            end = Spacing.m,
                            top = Spacing.xs,
                            bottom = 80.dp // Padding for FAB
                        ),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        verticalArrangement = Arrangement.spacedBy(Spacing.s),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Top Summary Card: Tüm Kayıtlar (Spans full width)
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            CategoryCard(
                                title = stringResource(R.string.category_all_title),
                                subtitle = stringResource(R.string.category_all_desc),
                                count = uiState.totalCount,
                                icon = Icons.Rounded.GridView,
                                iconTint = MaterialTheme.colorScheme.primary,
                                onClick = { viewModel.openCategory(null) }
                            )
                        }

                        // 4 Categories (2 columns per row in portrait, 3 in landscape)
                        items(categoryGridItems, key = { it.id ?: "all" }) { item ->
                            CategoryCard(
                                title = stringResource(item.titleRes),
                                subtitle = stringResource(item.subtitleRes),
                                count = item.count,
                                icon = item.icon,
                                iconTint = item.tint,
                                onClick = { viewModel.openCategory(item.id) }
                            )
                        }
                    }
                } else {
                    // Page 2: Filtered Entry List
                    when {
                        // Search Yields No Results
                        uiState.entries.isEmpty() && uiState.searchQuery.isNotEmpty() -> {
                            SearchEmptyState(
                                query = uiState.searchQuery,
                                onClearSearch = viewModel::clearSearch,
                                onAddWithQuery = { showTemplateSheet = true },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Empty Category
                        uiState.entries.isEmpty() -> {
                            EmptyCategoryState(
                                categoryTitle = currentCategoryTitle,
                                onAddClick = { showTemplateSheet = true },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Content List / Grid
                        else -> {
                            if (isLandscape) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    state = gridState,
                                    contentPadding = PaddingValues(
                                        start = Spacing.m,
                                        end = Spacing.m,
                                        top = Spacing.xs,
                                        bottom = 80.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
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
                            } else {
                                LazyColumn(
                                    state = listState,
                                    contentPadding = PaddingValues(
                                        start = Spacing.m,
                                        end = Spacing.m,
                                        top = Spacing.xs,
                                        bottom = 80.dp
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
    }
}
}

private data class CategoryGridItem(
    val id: String?,
    val titleRes: Int,
    val subtitleRes: Int,
    val icon: ImageVector,
    val tint: Color,
    val count: Int
)

@Composable
private fun EmptyCategoryState(
    categoryTitle: String,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Spacing.xl),
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
                imageVector = Icons.Rounded.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.m))
        Text(
            text = categoryTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.xxs))
        Text(
            text = stringResource(R.string.empty_category_entries),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Spacing.l))
        Button(
            onClick = onAddClick,
            shape = ShapeTokens.ButtonRadius
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(text = stringResource(R.string.add_entry))
        }
    }
}
