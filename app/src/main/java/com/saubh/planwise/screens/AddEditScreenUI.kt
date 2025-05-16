package com.saubh.planwise.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.saubh.planwise.data.Project
import com.saubh.planwise.data.ProjectViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

class AddEditScreenUI(
    private val viewModel: ProjectViewModel,
    private val navController: NavController
) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddEditScreen(projectId: Long) {
        val isEditMode = projectId != -1L
        val project = if (isEditMode) {
            viewModel.projectList.find { it.id == projectId }
        } else null

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        var clientName by remember { mutableStateOf(project?.clientName ?: "") }
        var phoneNumber by remember { mutableStateOf(project?.phoneNumber ?: "") }
        var advancePayment by remember { mutableStateOf(project?.advancePayment?.toString() ?: "") }
        var totalPayment by remember { mutableStateOf(project?.totalPayment?.toString() ?: "") }
        var description by remember { mutableStateOf(project?.description ?: "") }
        var isCompleted by remember { mutableStateOf(project?.isCompleted == true) }
        var isPaid by remember { mutableStateOf(project?.isPaid == true) }

        var clientNameError by remember { mutableStateOf("") }
        var phoneNumberError by remember { mutableStateOf("") }
        var advancePaymentError by remember { mutableStateOf("") }
        var totalPaymentError by remember { mutableStateOf("") }

        val focusManager = LocalFocusManager.current
        val orientation = LocalConfiguration.current.orientation
        val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

        Scaffold(
            modifier = Modifier.
                fillMaxSize().
                nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(text = if (isEditMode) "Edit Project" else "Add New Project")
                            if (isEditMode) {
                                Text(
                                    text = "Last modified ${viewModel.formatRelativeDate(project?.lastModifiedDate ?: Date())}",
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
                    actions = {
                        if (isEditMode) {
                            FilledTonalIconButton(
                                onClick = {
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Are you sure you want to delete this project?",
                                            actionLabel = "Delete",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.deleteProject(project!!)
                                            navController.navigateUp()
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, "Delete Project")
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .imePadding(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Basic Information Section
                        FormSection(
                            title = "Basic Information",
                            icon = Icons.Default.Info,
                            isLandscape = true
                        ) {
                            OutlinedTextField(
                                value = clientName,
                                onValueChange = {
                                    clientName = it
                                    clientNameError = ""
                                },
                                label = { Text("Client Name") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                isError = clientNameError.isNotEmpty(),
                                supportingText = if (clientNameError.isNotEmpty()) {
                                    { Text(clientNameError) }
                                } else null
                            )

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = {
                                    phoneNumber = it
                                    phoneNumberError = ""
                                },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Phone, null) },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                isError = phoneNumberError.isNotEmpty(),
                                supportingText = if (phoneNumberError.isNotEmpty()) {
                                    { Text(phoneNumberError) }
                                } else null
                            )
                        }

                        // Description Section
                        FormSection(
                            title = "Additional Details",
                            icon = Icons.Default.Description,
                            isLandscape = true
                        ) {
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Description (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 5,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    autoCorrectEnabled = true,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                )
                            )

                            if (isEditMode) {
                                ProjectStatusToggle(
                                    isCompleted = isCompleted,
                                    onCompletedChange = { isCompleted = it }
                                )
                            }
                        }
                    }

                    // Right Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Payment Section
                        FormSection(
                            title = "Payment Details",
                            icon = Icons.Default.Payment,
                            isLandscape = true
                        ) {
                            OutlinedTextField(
                                value = totalPayment,
                                onValueChange = {
                                    totalPayment = it
                                    totalPaymentError = ""
                                },
                                label = { Text("Total Payment") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                isError = totalPaymentError.isNotEmpty(),
                                supportingText = if (totalPaymentError.isNotEmpty()) {
                                    { Text(totalPaymentError) }
                                } else {
                                    { Text("Enter the total project value") }
                                }
                            )

                            OutlinedTextField(
                                value = advancePayment,
                                onValueChange = {
                                    advancePayment = it
                                    advancePaymentError = ""
                                },
                                label = { Text("Advance Payment") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                isError = advancePaymentError.isNotEmpty(),
                                supportingText = if (advancePaymentError.isNotEmpty()) {
                                    { Text(advancePaymentError) }
                                } else {
                                    { Text("Enter the advance payment amount") }
                                }
                            )

                            if (isEditMode) {
                                PaymentStatusCard(
                                    advancePayment = advancePayment.toDoubleOrNull() ?: 0.0,
                                    totalPayment = totalPayment.toDoubleOrNull() ?: 0.0
                                )
                            }
                        }

                        // Save Button
                        Button(
                            onClick = {
                                if (validateInputs(
                                        clientName,
                                        phoneNumber,
                                        advancePayment,
                                        totalPayment,
                                        { clientNameError = it },
                                        { phoneNumberError = it },
                                        { advancePaymentError = it },
                                        { totalPaymentError = it }
                                    )
                                ) {
                                    handleSave(
                                        isEditMode,
                                        project,
                                        clientName,
                                        phoneNumber,
                                        advancePayment,
                                        totalPayment,
                                        description,
                                        isCompleted,
                                        isPaid,
                                        scope,
                                        snackbarHostState,
                                        navController
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (isEditMode) "Update Project" else "Save Project")
                        }
                    }
                }
            } else {
                // Portrait layout (existing code)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ... existing layout code ...
                    FormSection(
                        title = "Basic Information",
                        icon = Icons.Default.Info
                    ) {
                        OutlinedTextField(
                            value = clientName,
                            onValueChange = {
                                clientName = it
                                clientNameError = ""
                            },
                            label = { Text("Client Name") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Person, "Client Name")
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = clientNameError.isNotEmpty(),
                            supportingText = if (clientNameError.isNotEmpty()) {
                                { Text(clientNameError) }
                            } else null
                        )

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                phoneNumber = it
                                phoneNumberError = ""
                            },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Phone, "Phone Number")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = phoneNumberError.isNotEmpty(),
                            supportingText = if (phoneNumberError.isNotEmpty()) {
                                { Text(phoneNumberError) }
                            } else null
                        )
                    }

                    // Payment Section
                    FormSection(
                        title = "Payment Details",
                        icon = Icons.Default.Payment
                    ) {
                        OutlinedTextField(
                            value = totalPayment,
                            onValueChange = {
                                totalPayment = it
                                totalPaymentError = ""
                            },
                            label = { Text("Total Payment") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.CurrencyRupee, "Total Payment")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = totalPaymentError.isNotEmpty(),
                            supportingText = if (totalPaymentError.isNotEmpty()) {
                                { Text(totalPaymentError) }
                            } else {
                                { Text("Enter the total project value") }
                            }
                        )

                        OutlinedTextField(
                            value = advancePayment,
                            onValueChange = {
                                advancePayment = it
                                advancePaymentError = ""
                            },
                            label = { Text("Advance Payment") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.CurrencyRupee, "Advance Payment")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = advancePaymentError.isNotEmpty(),
                            supportingText = if (advancePaymentError.isNotEmpty()) {
                                { Text(advancePaymentError) }
                            } else {
                                { Text("Enter the advance payment amount") }
                            }
                        )

                        if (isEditMode) {
                            PaymentStatusCard(
                                advancePayment = advancePayment.toDoubleOrNull() ?: 0.0,
                                totalPayment = totalPayment.toDoubleOrNull() ?: 0.0
                            )
                        }
                    }

                    // Description Section
                    FormSection(
                        title = "Additional Details",
                        icon = Icons.Default.Description
                    ) {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences,
                                autoCorrectEnabled = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            )
                        )

                        if (isEditMode) {
                            ProjectStatusToggle(
                                isCompleted = isCompleted,
                                onCompletedChange = { isCompleted = it }
                            )
                        }
                    }

                    // Save Button
                    Button(
                        onClick = {
                            if (validateInputs(
                                    clientName,
                                    phoneNumber,
                                    advancePayment,
                                    totalPayment,
                                    { clientNameError = it },
                                    { phoneNumberError = it },
                                    { advancePaymentError = it },
                                    { totalPaymentError = it }
                                )
                            ) {
                                handleSave(
                                    isEditMode,
                                    project,
                                    clientName,
                                    phoneNumber,
                                    advancePayment,
                                    totalPayment,
                                    description,
                                    isCompleted,
                                    isPaid,
                                    scope,
                                    snackbarHostState,
                                    navController
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Check, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isEditMode) "Update Project" else "Save Project")
                    }
                }
            }
        }
    }

    @Composable
    private fun ProjectStatusToggle(
        isCompleted: Boolean,
        onCompletedChange: (Boolean) -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isCompleted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = onCompletedChange
                )
                Column {
                    Text(
                        text = if (isCompleted) "Project Completed" else "Mark as Complete",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = if (isCompleted)
                            "This project has been marked as complete"
                        else
                            "Check this when the project is finished",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    @Composable
    private fun PaymentStatusCard(
        advancePayment: Double,
        totalPayment: Double
    ) {
        val progress = if (totalPayment > 0) advancePayment / totalPayment else 0.0

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Payment Progress",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LinearProgressIndicator(
                    progress = { progress.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
        }
    }

    @Composable
    private fun FormSection(
        title: String,
        icon: ImageVector,
        isLandscape: Boolean = false,
        content: @Composable ColumnScope.() -> Unit
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = if (isLandscape) 12.dp else 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(if (isLandscape) 12.dp else 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                content()
            }
        }
    }


    private fun handleSave(
        isEditMode: Boolean,
        project: Project?,
        clientName: String,
        phoneNumber: String,
        advancePayment: String,
        totalPayment: String,
        description: String,
        isCompleted: Boolean,
        isPaid: Boolean,
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        navController: NavController
    ) {
        val advancePaymentValue = advancePayment.toDoubleOrNull() ?: 0.0
        val totalPaymentValue = totalPayment.toDoubleOrNull() ?: 0.0
        val currentDate = Date()

        val updatedProject = if (isEditMode && project != null) {
            project.copy(
                clientName = clientName,
                phoneNumber = phoneNumber,
                advancePayment = advancePaymentValue,
                totalPayment = totalPaymentValue,
                description = description,
                isCompleted = isCompleted,
                isPaid = isPaid,
                lastModifiedDate = currentDate
            )
        } else {
            Project(
                clientName = clientName,
                phoneNumber = phoneNumber,
                advancePayment = advancePaymentValue,
                totalPayment = totalPaymentValue,
                description = description,
                creationDate = currentDate,
                lastModifiedDate = currentDate
            )
        }

        if (isEditMode) {
            viewModel.updateProject(updatedProject)
            scope.launch {
                snackbarHostState.showSnackbar("Project updated successfully")
            }
        } else {
            viewModel.addProject(updatedProject)
            scope.launch {
                snackbarHostState.showSnackbar("Project added successfully")
            }
        }

        scope.launch {
            kotlinx.coroutines.delay(800)
            navController.navigateUp()
        }
    }

    private fun validateInputs(
        clientName: String,
        phoneNumber: String,
        advancePayment: String,
        totalPayment: String,
        setClientNameError: (String) -> Unit,
        setPhoneNumberError: (String) -> Unit,
        setAdvancePaymentError: (String) -> Unit,
        setTotalPaymentError: (String) -> Unit
    ): Boolean {
        var isValid = true

        if (clientName.isBlank()) {
            setClientNameError("Client name is required")
            isValid = false
        }

        if (phoneNumber.isBlank() && phoneNumber.length >= 10) {
            setPhoneNumberError("Invalid or missing phone number")
            isValid = false
        }

        val advancePaymentValue = advancePayment.toDoubleOrNull()
        if (advancePaymentValue == null) {
            setAdvancePaymentError("Enter a valid amount")
            isValid = false
        }

        val totalPaymentValue = totalPayment.toDoubleOrNull()
        if (totalPaymentValue == null) {
            setTotalPaymentError("Enter a valid amount")
            isValid = false
        } else if (totalPaymentValue <= 0) {
            setTotalPaymentError("Total payment must be greater than zero")
            isValid = false
        } else if (advancePaymentValue != null && advancePaymentValue > totalPaymentValue) {
            setAdvancePaymentError("Advance cannot exceed total payment")
            isValid = false
        }

        return isValid
    }
}