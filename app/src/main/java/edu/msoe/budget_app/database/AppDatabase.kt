package edu.msoe.budget_app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.msoe.budget_app.entities.SpendingDetail
import edu.msoe.budget_app.entities.Converter

@Database(entities = [SpendingDetail::class], version = 1)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
}
