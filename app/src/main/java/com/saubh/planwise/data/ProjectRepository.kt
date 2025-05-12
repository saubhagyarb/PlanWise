package com.saubh.planwise.data

class ProjectRepository(private val dao: ProjectDao) {
    suspend fun getProjects(): List<Project> = dao.getAllProjects()
    suspend fun insert(project: Project) = dao.insertProject(project)
    suspend fun update(project: Project) = dao.updateProject(project)
    suspend fun delete(project: Project) = dao.deleteProject(project)
}