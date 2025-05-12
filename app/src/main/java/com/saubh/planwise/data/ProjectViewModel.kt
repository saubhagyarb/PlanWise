package com.saubh.planwise.data

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class ProjectViewModel(application: Application = Application()) : ViewModel() {

    val repository = ProjectRepository(AppDatabase.getDatabase(application).projectDao())
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
}
