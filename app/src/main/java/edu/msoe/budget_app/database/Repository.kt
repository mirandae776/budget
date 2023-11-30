package edu.msoe.budget_app.database

import edu.msoe.budget_app.database.BudgetDao
import edu.msoe.budget_app.entities.BudgetDetail
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class Repository(private val budgetDao: BudgetDao) {
    fun getBudgetDetails(): Flow<List<BudgetDetail>> {
        return budgetDao.getBudgetDetails()
    }

    fun getBudgetDetail(id: UUID): BudgetDetail {
        return budgetDao.getBudgetDetail(id)
    }

    fun deleteBudgetDetails() {
        budgetDao.deleteBudgetDetails()
    }

    fun deleteThisClass(id: UUID) {
        budgetDao.deleteThisClass(id)
    }

    fun addBudgetDetail(budgetDetail: BudgetDetail) {
        budgetDao.addBudgetDetail(budgetDetail)
    }

    fun updateBudgetDetail(budgetDetail: BudgetDetail) {
        budgetDao.updateBudgetDetail(budgetDetail)
    }

    suspend fun getBudget(): BudgetDetail? {
        return budgetDao.getBudget()
    }
}
