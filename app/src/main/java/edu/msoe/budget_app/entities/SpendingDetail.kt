package edu.msoe.budget_app.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class SpendingDetail(
    @PrimaryKey val id: UUID,
    val amountSpent: Double,
    val date: Date,
    val description: String
) {
}

