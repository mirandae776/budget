package edu.msoe.budget_app

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.msoe.budget_app.database.BudgetDatabaseHelper
import edu.msoe.budget_app.database.SpendingDatabaseHelper
import edu.msoe.budget_app.entities.SpendingDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.util.Calendar
import java.util.Locale

//This wil be used when developing locally, data will be stored here.
//In the final project everything should be database calls

class DataViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetDbHelper: BudgetDatabaseHelper = BudgetDatabaseHelper(application)
    var selectedMonth: String = getFormattedCurrentMonth()

    private val _spendingDetails = MutableLiveData<List<SpendingDetail>>()
    val spendingDetails: LiveData<List<SpendingDetail>> get() = _spendingDetails

    // ... other properties and functions ...

    fun updateSpendingDetails(newDetails: List<SpendingDetail>) {
        // Use MutableLiveData's setValue (or postValue for background thread) to update the LiveData
        _spendingDetails.value = newDetails
    }
    // Coroutine function to add a budget detail
    fun getSpendingDetails(context: Context): List<SpendingDetail> {
        val spendingDetails = mutableListOf<SpendingDetail>()

        // Assuming you have access to the SQLiteDatabase instance
        val database = SpendingDatabaseHelper(context).readableDatabase

        // Execute a query to fetch spending details from the database
        val cursor = database.query(
            SpendingDatabaseHelper.TABLE_NAME,
            arrayOf(
                SpendingDatabaseHelper.COLUMN_ID,
                SpendingDatabaseHelper.COLUMN_AMOUNT_SPENT,
                SpendingDatabaseHelper.COLUMN_DATE
            ),
            null,
            null,
            null,
            null,
            null
        )

        // Parse the cursor and populate the spendingDetails list
        while (cursor.moveToNext()) {
            val idString = cursor.getString(cursor.getColumnIndexOrThrow(SpendingDatabaseHelper.COLUMN_ID))
            val amountSpent = cursor.getDouble(cursor.getColumnIndexOrThrow(SpendingDatabaseHelper.COLUMN_AMOUNT_SPENT))
            val dateString = cursor.getString(cursor.getColumnIndexOrThrow(SpendingDatabaseHelper.COLUMN_DATE))

            val id = UUID.fromString(idString)
            val date = convertStringToDate(dateString)

            val spendingDetail = date?.let { SpendingDetail(id, amountSpent, it) }
            if (spendingDetail != null) {
                spendingDetails.add(spendingDetail)
            }
        }

        cursor.close()
        for(i in spendingDetails){
            print("i = " + i)
        }
        return spendingDetails
    }

    fun getBudget(): Int {
        val database = budgetDbHelper.readableDatabase
        val cursor = database.query(
            BudgetDatabaseHelper.TABLE_NAME,
            arrayOf(BudgetDatabaseHelper.COLUMN_BUDGET),
            null,
            null,
            null,
            null,
            null
        )

        var budget = 0

        if (cursor.moveToFirst()) {
            budget = cursor.getInt(cursor.getColumnIndexOrThrow(BudgetDatabaseHelper.COLUMN_BUDGET))
        }

        cursor.close()

        return budget
    }

    private fun convertStringToDate(dateString: String): Date? {
        val pattern = "EEE MMM dd HH:mm:ss zzz yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)

        try {
            return simpleDateFormat.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        // Return a default date if parsing fails
        return Date()
    }

    private fun getFormattedCurrentMonth(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
        return dateFormat.format(calendar.time).toUpperCase(Locale.US)
    }
}

