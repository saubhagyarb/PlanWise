package com.saubh.planwise.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.saubh.planwise.data.Project
import com.saubh.planwise.data.ProjectViewModel
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

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = if (isEditMode) "Edit Project" else "Add New Project") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormCard {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Client Name
                        OutlinedTextField(
                            value = clientName,
                            onValueChange = {
                                clientName = it
                                clientNameError = ""
                            },
                            label = { Text("Project Name") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Project Name"
                                )
                            },
                            isError = clientNameError.isNotEmpty(),
                            supportingText = if (clientNameError.isNotEmpty()) {
                                { Text(clientNameError) }
                            } else null
                        )

                        // Phone Number
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                phoneNumber = it
                                phoneNumberError = ""
                            },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Phone Number"
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Phone
                            ),
                            isError = phoneNumberError.isNotEmpty(),
                            supportingText = if (phoneNumberError.isNotEmpty()) {
                                { Text(phoneNumberError) }
                            } else null
                        )

                        // Advance Payment
                        OutlinedTextField(
                            value = advancePayment,
                            onValueChange = {
                                advancePayment = it
                                advancePaymentError = ""
                            },
                            label = { Text("Advance Payment") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = "Advance Payment"
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Decimal
                            ),
                            isError = advancePaymentError.isNotEmpty(),
                            supportingText = if (advancePaymentError.isNotEmpty()) {
                                { Text(advancePaymentError) }
                            } else null
                        )

                        // Total Payment
                        OutlinedTextField(
                            value = totalPayment,
                            onValueChange = {
                                totalPayment = it
                                totalPaymentError = ""
                            },
                            label = { Text("Total Payment") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = "Total Payment"
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Decimal
                            ),
                            isError = totalPaymentError.isNotEmpty(),
                            supportingText = if (totalPaymentError.isNotEmpty()) {
                                { Text(totalPaymentError) }
                            } else null
                        )

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "Description"
                                )
                            },
                            minLines = 3,
                            maxLines = 5
                        )

                        // Checkbox Options
                        if (isEditMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isCompleted,
                                    onCheckedChange = { isCompleted = it }
                                )
                                Text("Project Completed")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isPaid,
                                    onCheckedChange = { isPaid = it }
                                )
                                Text("Payment Completed")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                            )) {
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

                            // Navigate back after short delay to allow user to see the success message
                            scope.launch {
                                kotlinx.coroutines.delay(800)
                                navController.navigateUp()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                        Text(
                            text = if (isEditMode) "Update Project" else "Save Project",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun FormCard(content: @Composable () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
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

        if (phoneNumber.isBlank()) {
            setPhoneNumberError("Phone number is required")
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