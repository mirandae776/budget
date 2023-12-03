package edu.msoe.budget_app.database

import android.database.sqlite.SQLiteDatabase
import edu.msoe.budget_app.entities.SpendingDetail
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/*
class Repository(private val budgetDao: SQLiteDatabase) {
    fun getSpendingDetails(): Flow<List<SpendingDetail>> {
        return budgetDao.getSpendingDetails()
    }

    fun getSpendingDetail(id: UUID): SpendingDetail {
        return budgetDao.getSpendingDetail(id)
    }

    fun deleteSpendingDetails() {
        budgetDao.deleteSpendingDetails()
    }

    fun deleteThisClass(id: UUID) {
        budgetDao.deleteThisClass(id)
    }

    fun addSpendingDetail(spendingDetail: SpendingDetail) {
        budgetDao.addSpendingDetail(spendingDetail)
    }

    fun updateSpendingDetail(spendingDetail: SpendingDetail) {
        budgetDao.updateSpendingDetail(spendingDetail)
    }

    suspend fun getSpending(): SpendingDetail? {
        return budgetDao.getSpendingDetail()
    }
}
*/