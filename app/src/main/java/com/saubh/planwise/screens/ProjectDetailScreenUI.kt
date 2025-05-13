package com.saubh.planwise.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.saubh.planwise.data.Project
import com.saubh.planwise.data.ProjectViewModel
import com.saubh.planwise.navigation.Routes
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProjectDetailScreenUI(
    private val viewModel: ProjectViewModel,
    private val navController: NavController
) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProjectDetailScreen(projectId: Long) {
        val project = viewModel.projectList.find { it.id == projectId }
        var showDeleteDialog by remember { mutableStateOf(false) }

        if (project == null) {
            ProjectNotFound()
            return
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Project Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("${Routes.ADD_EDIT_PROJECT}?projectId=${project.id}") }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Project"
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Project",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ClientInfoCard(project)
                PaymentInfoCard(project)
                ProjectStatusCard(project) { updatedProject ->
                    viewModel.updateProject(updatedProject)
                }
                ProjectDescriptionCard(project)
                ProjectTimelineCard(project)
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
    fun ClientInfoCard(project: Project) {
        DetailCard(title = "Project Information") {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = project.clientName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Phone: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = project.phoneNumber,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    @Composable
    fun PaymentInfoCard(project: Project) {
        DetailCard(title = "Payment Information") {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Advance Payment",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatCurrency(project.advancePayment),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total Payment",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatCurrency(project.totalPayment),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = "Payment Progress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                PaymentProgressBar(
                    progress = project.paymentProgress,
                    isPaid = project.isPaid
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(project.paymentProgress * 100).toInt()}% Paid",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Remaining: ${formatCurrency(project.totalPayment - project.advancePayment)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (project.isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    @Composable
    fun ProjectStatusCard(project: Project, onProjectUpdated: (Project) -> Unit) {
        DetailCard(title = "Project Status") {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatusToggleRow(
                    label = "Project Completed",
                    isChecked = project.isCompleted,
                    onCheckedChange = { isCompleted ->
                        onProjectUpdated(project.copy(
                            isCompleted = isCompleted,
                            lastModifiedDate = Date()
                        ))
                    }
                )

                HorizontalDivider()

                StatusToggleRow(
                    label = "Payment Completed",
                    isChecked = project.isPaid,
                    onCheckedChange = { isPaid ->
                        onProjectUpdated(project.copy(
                            isPaid = isPaid,
                            lastModifiedDate = Date()
                        ))
                    }
                )
            }
        }
    }

    @Composable
    fun ProjectDescriptionCard(project: Project) {
        if (project.description.isNotEmpty()) {
            DetailCard(title = "Description") {
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    @Composable
    fun ProjectTimelineCard(project: Project) {
        DetailCard(title = "Timeline") {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimelineRow(
                    label = "Created on",
                    date = project.creationDate
                )

                TimelineRow(
                    label = "Last modified",
                    date = project.lastModifiedDate
                )
            }
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
    fun PaymentProgressBar(progress: Float, isPaid: Boolean) {
        androidx.compose.material3.LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = if (isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
        )
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

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance().format(amount)
    }

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }
}