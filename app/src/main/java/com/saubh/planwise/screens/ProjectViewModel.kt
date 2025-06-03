package com.saubh.planwise.screens

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saubh.planwise.data.AppDatabase
import com.saubh.planwise.data.Project
import com.saubh.planwise.data.ProjectRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProjectViewModel(application: Application = Application()) : ViewModel() {

    val repository = ProjectRepository(AppDatabase.Companion.getDatabase(application).projectDao())
    var projectList = mutableStateListOf<Project>()
        private set

    init {
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            val projects = repository.getProjects()
            projectList.clear()
            projectList.addAll(projects)
        }
    }

    fun addProject(project: Project) {
        viewModelScope.launch {
            repository.insert(project)
            loadProjects()
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            repository.update(project)
            loadProjects()
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.delete(project)
            loadProjects()
        }
    }
    fun getTimeOfDay(): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hourOfDay) {
            in 0..11 -> "Morning"
            in 12..16 -> "Afternoon"
            else -> "Evening"
        }
    }

    fun calculateDaysActive(creationDate: Date): String {
        val calendar = Calendar.getInstance()
        val today = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val inputCalendar = Calendar.getInstance().apply { time = creationDate }
        inputCalendar.set(Calendar.HOUR_OF_DAY, 0)
        inputCalendar.set(Calendar.MINUTE, 0)
        inputCalendar.set(Calendar.SECOND, 0)
        inputCalendar.set(Calendar.MILLISECOND, 0)

        val daysDiff = ((today.time - inputCalendar.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()

        return when (daysDiff) {
            0 -> "Today"
            1 -> "1 day"
            else -> "$daysDiff days"
        }
    }

    fun formatRelativeDate(date: Date): String {
        val calendar = Calendar.getInstance()
        val today = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val inputCalendar = Calendar.getInstance().apply { time = date }
        inputCalendar.set(Calendar.HOUR_OF_DAY, 0)
        inputCalendar.set(Calendar.MINUTE, 0)
        inputCalendar.set(Calendar.SECOND, 0)
        inputCalendar.set(Calendar.MILLISECOND, 0)

        val daysDiff = ((today.time - inputCalendar.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()

        return when {
            daysDiff == 0 -> "Today"
            daysDiff == 1 -> "Yesterday"
            daysDiff < 7 -> "$daysDiff days ago"
            else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
        }
    }

    fun formatCurrency(amount: Double): String {
        return "₹${NumberFormat.getInstance(Locale.getDefault()).format(amount)}"
    }

    fun formatAmountAbbreviated(amount: Double): String {
        return when {
            amount >= 1_00_00_000 -> String.format(locale = null,"₹%.1fCr", amount / 1_00_00_000)
            amount >= 1_00_000 -> String.format(locale = null, "₹%.1fL", amount / 1_00_000)
            amount >= 1000 -> String.format(locale = null, "₹%.1fK", amount / 1000)
            else -> String.format(locale = null, "₹%.0f", amount)
        }
    }

}