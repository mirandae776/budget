package edu.msoe.budget_app

import androidx.lifecycle.ViewModel
import edu.msoe.budget_app.entities.BudgetDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

//This wil be used when developing locally, data will be stored here.
//In the final project everything should be database calls

class DataViewModel: ViewModel() {

    var data = mutableListOf<String>(
        "2023-11-01,25.50",
        "2023-10-30,19.99",
        "2023-10-28,35.75",
        "2023-10-27,42.00",
        "2023-10-26,15.25",
        "2023-10-25,50.75",
        "2023-10-24,10.00",
        "2023-10-23,30.50",
        "2023-10-22,27.75",
        "2023-10-21,18.99",
        "2023-10-20,12.35",
        "2023-10-19,22.00",
        "2023-10-18,8.75",
        "2023-10-17,75.60",
        "2023-10-16,11.25",
        "2023-08-31,35.25",
        "2023-08-30,42.75",
        "2023-08-29,15.00",
        "2023-08-28,55.50",
        "2023-09-30,28.50",
        "2023-09-29,17.99",
        "2023-09-28,22.75",
        "2023-09-27,30.00"
    )
    var selectedMonth = "NOVEMBER"
    var budget = 5000;

    private val _budgetDetails = MutableStateFlow<List<BudgetDetail>>(emptyList())
    val budgetDetails: Flow<List<BudgetDetail>> = _budgetDetails

    // Coroutine function to add a budget detail
    fun addBudgetDetail(budgetDetail: BudgetDetail) {
        // Get the current list of budget details
        val currentList = _budgetDetails.value ?: emptyList()

        // Create a new list with the added budget detail
        val newList = currentList + budgetDetail

        // Update the MutableLiveData
        _budgetDetails.value = newList
    }
}