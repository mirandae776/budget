package edu.msoe.budget_app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is where we can display each month and the total spending for each month"
    }
    val text: LiveData<String> = _text
}