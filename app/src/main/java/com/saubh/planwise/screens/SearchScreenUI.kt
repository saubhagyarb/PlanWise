package com.saubh.planwise.screens

import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.saubh.planwise.R
import com.saubh.planwise.data.Project
import com.saubh.planwise.data.ProjectViewModel
import com.saubh.planwise.navigation.Routes
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
class SearchScreenUI(
    private val viewModel: ProjectViewModel,
    private val navController: NavController
) {
    @Composable
    fun SearchScreen() {
        var searchQuery by remember { mutableStateOf("") }
        var isSearchFocused by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val orientation = LocalConfiguration.current.orientation
        val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Search Projects",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            AnimatedVisibility(
                                visible = !isSearchFocused && !isLandscape,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Text(
                                    text = "Find projects by name or phone number",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
            if (isLandscape) {
                // Landscape layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .imePadding()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left column with search bar and instructions
                        Column(
                            modifier = Modifier
                                .weight(0.4f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            SearchBar(
                                query = searchQuery,
                                onQueryChange = { searchQuery = it },
                                onClearQuery = { searchQuery = "" },
                                onFocusChange = { isSearchFocused = it },
                                focusManager = focusManager
                            )
                        }

                        // Right column with search results
                        Box(
                            modifier = Modifier
                                .weight(0.6f)
                                .fillMaxHeight()
                        ) {
                            AnimatedContent(
                                targetState = searchQuery.isEmpty(),
                                label = "search_content"
                            ) { isEmpty ->
                                when {
                                    isEmpty -> EmptySearchState() // Empty box when no search
                                    else -> SearchResults(searchQuery)
                                }
                            }
                        }
                    }
                }
            } else {
                // Portrait layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClearQuery = { searchQuery = "" },
                        onFocusChange = { isSearchFocused = it },
                        focusManager = focusManager
                    )

                    AnimatedContent(
                        targetState = searchQuery.isEmpty(),
                        label = "search_content"
                    ) { isEmpty ->
                        when {
                            isEmpty -> EmptySearchState()
                            else -> SearchResults(searchQuery)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SearchResults(searchQuery: String) {
        val filteredProjects = viewModel.projectList.filter { project ->
            project.clientName.contains(searchQuery, ignoreCase = true) ||
                    project.phoneNumber.contains(searchQuery, ignoreCase = true)
        }

        if (filteredProjects.isEmpty()) {
            NoResultsFound()
        } else {
            LazyColumn(
                modifier = Modifier.imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProjects) { project ->
                    ProjectCardCompact(project = project)
                }
            }
        }
    }

    @Composable
    fun NoResultsFound() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No matching projects found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SearchBar(
        query: String,
        onQueryChange: (String) -> Unit,
        onClearQuery: () -> Unit,
        onFocusChange: (Boolean) -> Unit,
        focusManager: FocusManager
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused = interactionSource.collectIsFocusedAsState()

        LaunchedEffect(isFocused.value) {
            onFocusChange(isFocused.value)
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 2.dp
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                placeholder = {
                    Text(
                        text = "Search by name or phone",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        IconButton(onClick = onClearQuery) {
                            Icon(Icons.Default.Clear, "Clear search")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                interactionSource = interactionSource
            )
        }
    }

    @Composable
    private fun EmptySearchState() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Start typing to search",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Search by client name or phone number",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ProjectCardCompact(project: Project) {
        val context = LocalContext.current
        val isLandscape =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            onClick = { navController.navigate(Routes.projectDetailRoute(project.id)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isLandscape) 12.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = project.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    ProjectStatusChip(project)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = project.phoneNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    // Contact buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = "tel:${project.phoneNumber}".toUri()
                                }
                                try {
                                    ContextCompat.startActivity(context, intent, null)
                                } catch (_: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Unable to make call",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = "https://api.whatsapp.com/send?phone=${
                                        project.phoneNumber.replace("+", "")
                                    }".toUri()
                                }
                                try {
                                    ContextCompat.startActivity(context, intent, null)
                                } catch (_: Exception) {
                                    Toast.makeText(
                                        context,
                                        "WhatsApp not installed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.whatsapp),
                                contentDescription = "WhatsApp",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                PaymentProgressBar(project)
            }
        }
    }

    @Composable
    private fun ProjectStatusChip(project: Project) {
        Surface(
            color = if (project.isCompleted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.tertiaryContainer,
            shape = MaterialTheme.shapes.small
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (project.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = if (project.isCompleted) "Completed" else "Ongoing",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (project.isCompleted)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }

    @Composable
    private fun PaymentProgressBar(project: Project) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(project.paymentProgress * 100).toInt()}% paid",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = NumberFormat.getCurrencyInstance().format(project.totalPayment),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { project.paymentProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (project.isPaid)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.tertiary
            )
        }
    }
}


// Keep all other existing composables and functions exactly as they are
