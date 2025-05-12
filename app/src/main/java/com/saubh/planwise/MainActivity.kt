package com.saubh.planwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.saubh.planwise.data.ProjectViewModel
import com.saubh.planwise.screens.HomeScreenUI
import com.saubh.planwise.ui.theme.PlanWiseTheme

class MainActivity : ComponentActivity() {
    lateinit var viewModel : ProjectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel = ProjectViewModel(application)
        val homeScreen = HomeScreenUI(viewModel)

        setContent {
            PlanWiseTheme {
                homeScreen.HomeScreen(viewModel)
                }
            }
        }
    }
