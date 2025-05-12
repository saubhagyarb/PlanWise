package com.saubh.planwise.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val phoneNumber: String,
    val advancePayment: Double,
    val totalPayment: Double,
    val isCompleted: Boolean = false,
    val isPaid: Boolean = false,
    val description: String = "",
    val creationDate: Date = Date(),
    val lastModifiedDate: Date = Date()
) {
    val paymentProgress: Float
        get() = (advancePayment / totalPayment).toFloat()
}
