package edu.msoe.budget_app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import edu.msoe.budget_app.entities.BudgetDetail
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgetDetail")
    fun getBudgetDetails(): Flow<List<BudgetDetail>>

    @Query("SELECT * FROM budgetDetail WHERE id=(:id)")
    fun getBudgetDetail(id: UUID): BudgetDetail

    @Query("Delete FROM budgetDetail")
    fun deleteBudgetDetails()

    @Query("DELETE FROM budgetDetail WHERE id=(:id)")
    fun deleteThisClass(id: UUID)

    @Insert
    fun addBudgetDetail(budgetDetail: BudgetDetail)

    @Update
    fun updateBudgetDetail(budgetDetail: BudgetDetail)
}