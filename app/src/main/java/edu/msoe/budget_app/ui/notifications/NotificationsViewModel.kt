package edu.msoe.budget_app.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {
    private val _budget = MutableLiveData<Int>()
    val budget: LiveData<Int> get() = _budget

    fun updateBudget(newBudget: Int) {
        _budget.value = newBudget
    }
}