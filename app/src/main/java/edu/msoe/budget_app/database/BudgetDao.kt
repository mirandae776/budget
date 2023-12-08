package edu.msoe.budget_app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import edu.msoe.budget_app.entities.SpendingDetail
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface BudgetDao {
    @Query("SELECT * FROM spendingDetail")
    fun getSpendingDetails(): Flow<List<SpendingDetail>>

    @Query("SELECT * FROM spendingDetail WHERE id=(:id)")
    fun getSpendingDetail(id: UUID): SpendingDetail

    @Query("Delete FROM spendingDetail")
    fun deleteSpendingDetails()

    @Query("SELECT * FROM spendingDetail LIMIT 1")
    fun getSpendingDetail(): SpendingDetail?

    @Query("DELETE FROM spendingDetail WHERE id=(:id)")
    fun deleteThisClass(id: UUID)

    @Insert
    fun addSpendingDetail(spendingDetail: SpendingDetail)

    @Update
    fun updateSpendingDetail(spendingDetail: SpendingDetail)
}