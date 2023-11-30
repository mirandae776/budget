package edu.msoe.budget_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.msoe.budget_app.MainActivity
import edu.msoe.budget_app.entities.BudgetDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

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