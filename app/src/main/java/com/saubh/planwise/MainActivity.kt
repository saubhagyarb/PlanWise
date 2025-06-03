package com.saubh.planwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.saubh.planwise.screens.ProjectViewModel
import com.saubh.planwise.navigation.AppNavigation
import com.saubh.planwise.ui.theme.PlanWiseTheme

class MainActivity : ComponentActivity() {
    lateinit var viewModel: ProjectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ProjectViewModel(application)
        //

        setContent {
            PlanWiseTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}