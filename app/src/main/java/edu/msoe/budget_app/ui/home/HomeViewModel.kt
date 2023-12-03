package edu.msoe.budget_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.msoe.budget_app.entities.SpendingDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _spendingDetails = MutableStateFlow<List<SpendingDetail>>(emptyList())
    val spendingDetails: Flow<List<SpendingDetail>> = _spendingDetails

    // Coroutine function to add a budget detail
    fun addBudgetDetail(spendingDetail: SpendingDetail) {
        // Get the current list of budget details
        val currentList = _spendingDetails.value ?: emptyList()

        // Create a new list with the added budget detail
        val newList = currentList + spendingDetail

        // Update the MutableLiveData
        _spendingDetails.value = newList
    }
}