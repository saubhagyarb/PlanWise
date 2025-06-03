package com.saubh.planwise.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.saubh.planwise.data.Project
import com.saubh.planwise.screens.ProjectViewModel
import com.saubh.planwise.navigation.Routes
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
class ProjectDetailScreenUI(
    private val viewModel: ProjectViewModel,
    private val navController: NavController
) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProjectDetailScreen(projectId: Long) {
        val project = viewModel.projectList.find { it.id == projectId }
        var showDeleteDialog by remember { mutableStateOf(false) }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val orientation = LocalConfiguration.current.orientation
        val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

        if (project == null) {
            ProjectNotFound()
            return
        }

        Scaffold(
            contentWindowInsets = WindowInsets.displayCutout,
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = project.clientName,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            AnimatedContent(targetState = project.isCompleted, label = "status") { isCompleted ->
                                Surface(
                                    color = if (isCompleted)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = if (isCompleted) "Completed" else "In Progress",
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = if (isCompleted)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = !project.isCompleted,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            FilledTonalIconButton(
                                onClick = { navController.navigate("${Routes.ADD_EDIT_PROJECT}?projectId=${project.id}") }
                            ) {
                                Icon(Icons.Default.Edit, "Edit")
                            }
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            if (isLandscape) {
                // Landscape Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        item { QuickStatsBar(project) }
                        item { PaymentSummaryCard(project) }
                        item { ClientInfoCard(project) }
                    }

                    // Right Column
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        item { PaymentDetailsCard(project) }
                        item {
                            ProjectStatusCard(project) { updatedProject ->
                                viewModel.updateProject(updatedProject)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Project status updated",
                                        withDismissAction = true
                                    )
                                }
                            }
                        }
                        item {
                            if (project.description.isNotEmpty()) {
                                ProjectDescriptionCard(project)
                            }
                        }
                        item { ProjectTimelineCard(project) }
                    }
                }
            } else {
                // Portrait Layout
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item { QuickStatsBar(project) }
                    item { PaymentSummaryCard(project) }
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ClientInfoCard(project)
                            PaymentDetailsCard(project)
                            ProjectStatusCard(project) { updatedProject ->
                                viewModel.updateProject(updatedProject)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Project status updated",
                                        withDismissAction = true
                                    )
                                }
                            }
                            if (project.description.isNotEmpty()) {
                                ProjectDescriptionCard(project)
                            }
                            ProjectTimelineCard(project)
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    viewModel.deleteProject(project)
                    navController.navigateUp()
                }
            )
        }
    }

    @Composable
    fun ProjectNotFound() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Project not found",
                    style = MaterialTheme.typography.titleLarge
                )
                Button(onClick = { navController.navigateUp() }) {
                    Text("Go Back")
                }
            }
        }
    }
    @Composable
    fun QuickStatsBar(project: Project) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatColumn(
                        icon = Icons.Default.Timer,
                        value = viewModel.calculateDaysActive(project.creationDate),
                        label = "Days Active"
                    )
                    StatColumn(
                        icon = Icons.Default.Payment,
                        value = "${(project.paymentProgress * 100).toInt()}%",
                        label = "Payment Progress",
                        tint = if (project.isPaid)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.tertiary
                    )
                    StatColumn(
                        icon = Icons.Default.AccountBalance,
                        value = viewModel.formatAmountAbbreviated(project.totalPayment),
                        label = "Total Value"
                    )
                }
            }
        }
    }
    @Composable
    fun PaymentSummaryCard(project: Project) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PaymentInfo(
                        label = "Advance",
                        amount = project.advancePayment,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    PaymentInfo(
                        label = "Remaining",
                        amount = project.totalPayment - project.advancePayment,
                        color = if (project.isPaid)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }

                LinearProgressIndicator(
                    progress = { project.paymentProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (project.isPaid)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }

    @Composable
    fun PaymentInfo(
        label: String,
        amount: Double,
        color: Color
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = viewModel.formatCurrency(amount),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    }

    @Composable
    fun DeleteConfirmationDialog(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Delete Project?")
            },
            text = {
                Text("This action cannot be undone. Are you sure you want to delete this project?")
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
    @Composable
    fun TimelineRow(label: String, date: Date) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = formatDate(date),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    @Composable
    fun StatusToggleRow(
        label: String,
        isChecked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                thumbContent = if (isChecked) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else null
            )
        }
    }

    @Composable
    fun DetailCard(title: String, content: @Composable () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                content()
            }
        }
    }

    @Composable
    private fun StatColumn(
        icon: ImageVector,
        value: String,
        label: String,
        tint: Color = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = tint
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }

    @Composable
    private fun ClientInfoCard(project: Project) {
        DetailCard(title = "Client Information") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Client Name")
                    Text(
                        text = project.clientName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 32.dp)
                    )
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Phone Number")
                    Text(
                        text = project.phoneNumber,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    @Composable
    private fun PaymentDetailsCard(project: Project) {
        DetailCard(title = "Payment Details") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentDetailRow("Total Amount", project.totalPayment)
                HorizontalDivider()
                PaymentDetailRow("Advance Payment", project.advancePayment)
                HorizontalDivider()
                PaymentDetailRow(
                    "Remaining Amount",
                    project.totalPayment - project.advancePayment,
                    color = if (project.isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }

    @Composable
    private fun PaymentDetailRow(
        label: String,
        amount: Double,
        color: Color = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            Text(
                text = viewModel.formatCurrency(amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }

    @Composable
    private fun ProjectStatusCard(
        project: Project,
        onProjectUpdate: (Project) -> Unit
    ) {
        var showPaidConfirmDialog by remember { mutableStateOf(false) }

        DetailCard(title = "Project Status") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                StatusToggleRow(
                    label = "Mark as Completed",
                    isChecked = project.isCompleted
                ) { isCompleted ->
                    onProjectUpdate(project.copy(isCompleted = isCompleted))
                }
                HorizontalDivider()
                StatusToggleRow(
                    label = "Mark as Paid",
                    isChecked = project.isPaid
                ) { isPaid ->
                    if (isPaid) {
                        showPaidConfirmDialog = true
                    } else {
                        onProjectUpdate(project.copy(isPaid = false))
                    }
                }
            }
        }

        if (showPaidConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showPaidConfirmDialog = false },
                title = { Text("Mark as Paid?") },
                text = {
                    Text("This will set the advance payment equal to total payment. Continue?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onProjectUpdate(project.copy(
                                isPaid = true,
                                advancePayment = project.totalPayment
                            ))
                            showPaidConfirmDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPaidConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    @Composable
    private fun ProjectDescriptionCard(project: Project) {
        DetailCard(title = "Description") {
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    @Composable
    private fun ProjectTimelineCard(project: Project) {
        DetailCard(title = "Timeline") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TimelineRow("Created", project.creationDate)
                HorizontalDivider()
                TimelineRow("Last Modified", project.lastModifiedDate)
            }
        }
    }

}