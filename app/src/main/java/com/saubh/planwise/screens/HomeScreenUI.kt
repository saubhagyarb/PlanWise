package com.saubh.planwise.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.saubh.planwise.data.Project
import com.saubh.planwise.data.ProjectFilter
import com.saubh.planwise.data.ProjectViewModel
import com.saubh.planwise.navigation.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class HomeScreenUI(
    private val viewModel: ProjectViewModel,
    private val navController: NavController
) {
    @Composable
    fun HomeScreen() {
        var selectedFilter by remember { mutableStateOf(ProjectFilter.ALL) }
        val scope = rememberCoroutineScope()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val listState = rememberLazyListState()


        Scaffold(
            topBar = { AppTopBar(scrollBehavior) },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Routes.ADD_EDIT_PROJECT) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, "Add Project")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Project")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                // Dashboard Section
                DashboardSection(viewModel.projectList)

                // Filter Section
                FilterSection(selectedFilter, onFilterSelected = { filter ->
                    selectedFilter = filter
                    scope.launch {
                        // Animate scroll to top when filter changes
                        listState.scrollToItem(0)
                    }
                })

                // Projects List
                AnimatedContent(
                    targetState = selectedFilter,
                    transitionSpec = {
                        fadeIn() + slideInVertically() togetherWith fadeOut()
                    }
                ) { filter ->
                    ProjectList(
                        projects = viewModel.projectList.filter {
                            when (filter) {
                                ProjectFilter.ALL -> true
                                ProjectFilter.ONGOING -> !it.isCompleted
                                ProjectFilter.COMPLETED -> it.isCompleted
                                ProjectFilter.UNPAID -> !it.isPaid
                            }
                        }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppTopBar(scrollBehavior: TopAppBarScrollBehavior) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Good ${viewModel.getTimeOfDay()}!",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Track your projects easily",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { navController.navigate(Routes.SEARCH) }
                ) {
                    Icon(Icons.Default.Search, "Search")
                }
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
            scrollBehavior = scrollBehavior
        )
    }

    @Composable
    private fun DashboardSection(projects: List<Project>) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DashboardItem(
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        label = "Total Projects",
                        value = projects.size.toString()
                    )
                    DashboardItem(
                        icon = Icons.Default.Pending,
                        label = "Ongoing",
                        value = projects.count { !it.isCompleted }.toString()
                    )
                    DashboardItem(
                        icon = Icons.Default.AccountBalance,
                        label = "Total Value",
                        value = viewModel.formatCurrency(projects.sumOf { it.totalPayment })
                    )
                }
            }
        }
    }

    @Composable
    private fun DashboardItem(
        icon: ImageVector,
        label: String,
        value: String
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }

    @Composable
    private fun FilterSection(
        selectedFilter: ProjectFilter,
        onFilterSelected: (ProjectFilter) -> Unit
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedFilter.ordinal,
            edgePadding = 16.dp,
            divider = {},
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            ProjectFilter.entries.forEachIndexed { index, filter ->
                Tab(
                    selected = selectedFilter == filter,
                    onClick = { onFilterSelected(filter) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterSelected(filter) },
                        label = {
                            Text(
                                text = "${filter.title} (${
                                    viewModel.projectList.count {
                                        when (filter) {
                                            ProjectFilter.ALL -> true
                                            ProjectFilter.ONGOING -> !it.isCompleted
                                            ProjectFilter.COMPLETED -> it.isCompleted
                                            ProjectFilter.UNPAID -> !it.isPaid
                                        }
                                    }
                                })"
                            )
                        },
                        leadingIcon = if (selectedFilter == filter) {
                            { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ProjectList(projects: List<Project>) {
        if (projects.isEmpty()) {
            EmptyStateMessage()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = projects,
                    key = { it.id }
                ) { project ->
                    ProjectCard(project = project)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProjectCard(project: Project) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            onClick = { navController.navigate(Routes.projectDetailRoute(project.id)) }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.clientName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = viewModel.formatRelativeDate(project.creationDate),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (project.isCompleted) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(4.dp)
                                )
                            }
                        }

                        Text(
                            text = viewModel.formatCurrency(project.totalPayment),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                PaymentProgressBar(
                    progress = project.paymentProgress,
                    showLabel = true,
                    isPaid = project.isPaid
                )
            }
        }
    }

    @Composable
    fun PaymentProgressBar(
        progress: Float,
        showLabel: Boolean = false,
        isPaid: Boolean
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (showLabel) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Payment Progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isPaid)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.tertiary
            )
        }
    }


    @Composable
    private fun EmptyStateMessage() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "No projects found",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Tap the + button to add your first project",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}