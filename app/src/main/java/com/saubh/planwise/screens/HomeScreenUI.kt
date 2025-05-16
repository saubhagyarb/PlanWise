package com.saubh.planwise.screens

import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.saubh.planwise.R
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
        val orientation = LocalConfiguration.current.orientation
        val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
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
            if (isLandscape) {
                LandscapeLayout(
                    padding = padding,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter ->
                        selectedFilter = filter
                        scope.launch {
                            listState.scrollToItem(0)
                        }
                    },
                    projects = viewModel.projectList,
                    listState = listState,
                    scrollBehavior = scrollBehavior
                )
            } else {
                PortraitLayout(
                    padding = padding,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter ->
                        selectedFilter = filter
                        scope.launch {
                            listState.scrollToItem(0)
                        }
                    },
                    projects = viewModel.projectList,
                    listState = listState,
                    scrollBehavior = scrollBehavior
                )
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
    private fun LandscapeLayout(
        padding: PaddingValues,
        selectedFilter: ProjectFilter,
        onFilterSelected: (ProjectFilter) -> Unit,
        projects: List<Project>,
        listState: LazyListState,
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left panel with dashboard
            Box(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            ) {
                DashboardSection(
                    projects = projects,
                    isLandscape = true
                )
            }

            // Right panel with filters and projects list
            Column(
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxHeight()
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filter chips at the top
                FilterSection(
                    selectedFilter = selectedFilter,
                    onFilterSelected = onFilterSelected
                )

                // Projects list below
                Box(modifier = Modifier.weight(1f)) {
                    ProjectList(
                        projects = projects.filter {
                            when (selectedFilter) {
                                ProjectFilter.ALL -> true
                                ProjectFilter.ONGOING -> !it.isCompleted
                                ProjectFilter.COMPLETED -> it.isCompleted
                                ProjectFilter.UNPAID -> !it.isPaid
                            }
                        },
                        listState = listState
                    )
                }
            }
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


    @Composable
    private fun DashboardSection(
        projects: List<Project>,
        isLandscape: Boolean = false
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = if (isLandscape) 0.dp else 8.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompactStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        value = projects.size.toString(),
                        label = "Total",
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    CompactStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Pending,
                        value = projects.count { !it.isCompleted }.toString(),
                        label = "Ongoing",
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                // Finance row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompactFinanceInfo(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AccountBalance,
                        label = "Total Amount",
                        value = viewModel.formatAmountAbbreviated(projects.sumOf { it.totalPayment })
                    )
                    CompactFinanceInfo(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Check,
                        label = "Received",
                        value = viewModel.formatAmountAbbreviated(projects.sumOf { it.advancePayment })
                    )
                }
            }
        }
    }

    @Composable
    private fun CompactStatCard(
        modifier: Modifier = Modifier,
        icon: ImageVector,
        value: String,
        label: String,
        backgroundColor: Color,
        contentColor: Color
    ) {
        Surface(
            modifier = modifier,
            color = backgroundColor,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleLarge,
                        color = contentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }

    @Composable
    private fun CompactFinanceInfo(
        modifier: Modifier = Modifier,
        icon: ImageVector,
        label: String,
        value: String
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    @Composable
    private fun PortraitLayout(
        padding: PaddingValues,
        selectedFilter: ProjectFilter,
        onFilterSelected: (ProjectFilter) -> Unit,
        projects: List<Project>,
        listState: LazyListState,
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            DashboardSection(projects)
            FilterSection(selectedFilter, onFilterSelected)
            AnimatedContent(
                targetState = selectedFilter,
                transitionSpec = {
                    fadeIn() + slideInVertically() togetherWith fadeOut()
                }
            ) { filter ->
                ProjectList(
                    projects = projects.filter {
                        when (filter) {
                            ProjectFilter.ALL -> true
                            ProjectFilter.ONGOING -> !it.isCompleted
                            ProjectFilter.COMPLETED -> it.isCompleted
                            ProjectFilter.UNPAID -> !it.isPaid
                        }
                    },
                    listState = listState
                )
            }
        }
    }

    @Composable
    private fun ProjectList(
        projects: List<Project>,
        listState: LazyListState
    ) {
        if (projects.isEmpty()) {
            EmptyStateMessage()
        } else {
            LazyColumn(
                state = listState,
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProjectCard(project: Project) {
        val context = LocalContext.current
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .shadow(2.dp, RoundedCornerShape(16.dp)),
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar section
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = project.clientName.take(2).uppercase(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.clientName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = viewModel.formatRelativeDate(project.creationDate),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "•",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(project.paymentProgress * 100).toInt()}% paid",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    // Contact Actions
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = "tel:${project.phoneNumber}".toUri()
                                }
                                try {
                                    ContextCompat.startActivity(context, intent, null)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = MaterialTheme.colorScheme.primary,
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
                                    Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.whatsapp),
                                contentDescription = "WhatsApp",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Description section
                if (project.description.isNotBlank()) {
                    Text(
                        text = project.description.take(80) + if (project.description.length > 80) "..." else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(0.6f)) {
                        PaymentProgressBar(
                            progress = project.paymentProgress,
                            showLabel = false,
                            isPaid = project.isPaid
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.4f),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (project.isCompleted) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(2.dp)
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(
                            text = viewModel.formatAmountAbbreviated(project.totalPayment),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
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
}