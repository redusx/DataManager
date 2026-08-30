package com.example.datamanager.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.datamanager.R
import com.example.datamanager.data.model.Category
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.ui.component.CategoryCard
import com.example.datamanager.ui.component.EntryCard
import com.example.datamanager.ui.component.SearchBar
import com.example.datamanager.ui.theme.AccountGradientEnd
import com.example.datamanager.ui.theme.AccountGradientStart
import com.example.datamanager.ui.theme.CustomGradientEnd
import com.example.datamanager.ui.theme.CustomGradientStart
import com.example.datamanager.ui.theme.FinancialGradientEnd
import com.example.datamanager.ui.theme.FinancialGradientStart
import com.example.datamanager.ui.theme.PersonalGradientEnd
import com.example.datamanager.ui.theme.PersonalGradientStart
import com.example.datamanager.ui.viewmodel.HomeViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun HomeScreen(
    onCategoryClick: (String) -> Unit,
    onEntryClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.home)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                    label = { Text(stringResource(R.string.favorites)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2; onSettingsClick() },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.settings)) }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> HomeContent(
                uiState = uiState,
                onCategoryClick = onCategoryClick,
                onEntryClick = onEntryClick,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onToggleFavorite = viewModel::toggleFavorite,
                modifier = Modifier.padding(paddingValues)
            )
            1 -> FavoritesContent(
                uiState = uiState,
                onEntryClick = onEntryClick,
                onToggleFavorite = viewModel::toggleFavorite,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun HomeContent(
    uiState: com.example.datamanager.ui.viewmodel.HomeUiState,
    onCategoryClick: (String) -> Unit,
    onEntryClick: (Long) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onToggleFavorite: (com.example.datamanager.data.model.DataEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val gson = remember { Gson() }
    val fieldListType = remember { object : TypeToken<List<FieldItem>>() {}.type }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.welcome_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.welcome_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search
        item {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChanged
            )
        }

        // Search results
        if (uiState.isSearching) {
            items(uiState.searchResults) { entry ->
                val subtitle = try {
                    val fields: List<FieldItem> = gson.fromJson(entry.fieldsJson, fieldListType)
                    fields.firstOrNull()?.let { "${it.key}: ${it.value}" } ?: ""
                } catch (e: Exception) { "" }

                EntryCard(
                    title = entry.title,
                    subtitle = subtitle,
                    isFavorite = entry.isFavorite,
                    onClick = { onEntryClick(entry.id) },
                    onFavoriteClick = { onToggleFavorite(entry) }
                )
            }
        } else {
            // Categories grid
            item {
                Text(
                    text = stringResource(R.string.categories),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryCard(
                        title = stringResource(R.string.category_personal),
                        count = uiState.categoryCounts[Category.PERSONAL.id] ?: 0,
                        icon = Icons.Filled.AccountCircle,
                        gradientStart = PersonalGradientStart,
                        gradientEnd = PersonalGradientEnd,
                        onClick = { onCategoryClick(Category.PERSONAL.id) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                    CategoryCard(
                        title = stringResource(R.string.category_financial),
                        count = uiState.categoryCounts[Category.FINANCIAL.id] ?: 0,
                        icon = Icons.Filled.CreditCard,
                        gradientStart = FinancialGradientStart,
                        gradientEnd = FinancialGradientEnd,
                        onClick = { onCategoryClick(Category.FINANCIAL.id) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryCard(
                        title = stringResource(R.string.category_accounts),
                        count = uiState.categoryCounts[Category.ACCOUNT.id] ?: 0,
                        icon = Icons.Filled.Lock,
                        gradientStart = AccountGradientStart,
                        gradientEnd = AccountGradientEnd,
                        onClick = { onCategoryClick(Category.ACCOUNT.id) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                    CategoryCard(
                        title = stringResource(R.string.category_custom),
                        count = uiState.categoryCounts[Category.CUSTOM.id] ?: 0,
                        icon = Icons.Filled.NoteAdd,
                        gradientStart = CustomGradientStart,
                        gradientEnd = CustomGradientEnd,
                        onClick = { onCategoryClick(Category.CUSTOM.id) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }

            // Favorites section
            if (uiState.favorites.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.favorites),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(uiState.favorites.take(5)) { entry ->
                    val subtitle = try {
                        val fields: List<FieldItem> = gson.fromJson(entry.fieldsJson, fieldListType)
                        fields.firstOrNull()?.let { "${it.key}: ${it.value}" } ?: ""
                    } catch (e: Exception) { "" }

                    EntryCard(
                        title = entry.title,
                        subtitle = subtitle,
                        isFavorite = entry.isFavorite,
                        onClick = { onEntryClick(entry.id) },
                        onFavoriteClick = { onToggleFavorite(entry) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesContent(
    uiState: com.example.datamanager.ui.viewmodel.HomeUiState,
    onEntryClick: (Long) -> Unit,
    onToggleFavorite: (com.example.datamanager.data.model.DataEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val gson = remember { Gson() }
    val fieldListType = remember { object : TypeToken<List<FieldItem>>() {}.type }

    if (uiState.favorites.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.no_favorites),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.favorites),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            items(uiState.favorites) { entry ->
                val subtitle = try {
                    val fields: List<FieldItem> = gson.fromJson(entry.fieldsJson, fieldListType)
                    fields.firstOrNull()?.let { "${it.key}: ${it.value}" } ?: ""
                } catch (e: Exception) { "" }

                EntryCard(
                    title = entry.title,
                    subtitle = subtitle,
                    isFavorite = true,
                    onClick = { onEntryClick(entry.id) },
                    onFavoriteClick = { onToggleFavorite(entry) }
                )
            }
        }
    }
}
