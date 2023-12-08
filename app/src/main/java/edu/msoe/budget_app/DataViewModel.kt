package edu.msoe.budget_app

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.util.Log
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
    var selectedBudget: Int = getBudgets()[getBudgets().size-1];
    var selectedSorting: String = "date"

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
                SpendingDatabaseHelper.COLUMN_DATE,
                SpendingDatabaseHelper.COLUMN_DESCRIPTION
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
            val description = cursor.getString(cursor.getColumnIndexOrThrow(SpendingDatabaseHelper.COLUMN_DESCRIPTION))
            Log.d("DATABASE DESCRIPTION TYPE", "Description from Database: $description")
            val id = UUID.fromString(idString)
            val date = convertStringToDate(dateString)

            val spendingDetail = date?.let { SpendingDetail(id, amountSpent, it, description) }
            if (spendingDetail != null) {
                spendingDetails.add(spendingDetail)
            }
        }

        cursor.close()

        return spendingDetails
    }

    fun getBudgets(): List<Int> {
        val database = budgetDbHelper.readableDatabase

        // Query the database to get all entries
        val cursor = database.query(
            BudgetDatabaseHelper.TABLE_NAME,
            arrayOf(BudgetDatabaseHelper.COLUMN_BUDGET),
            null,
            null,
            null,
            null,
            null
        )

        val budgetList = mutableListOf<Int>()

        // Retrieve budget values from the cursor
        while (cursor.moveToNext()) {
            val budget = cursor.getInt(cursor.getColumnIndexOrThrow(BudgetDatabaseHelper.COLUMN_BUDGET))
            budgetList.add(budget)
        }

        cursor.close()

        return budgetList
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

    fun addBudgetToDatabase(budgetAmount: Int) {
        val database = budgetDbHelper.writableDatabase

        // Create a ContentValues object to hold the values to be inserted
        val values = ContentValues().apply {
            put(BudgetDatabaseHelper.COLUMN_BUDGET, budgetAmount)
        }

        // Insert the budget into the database
        val newRowId = database.insert(BudgetDatabaseHelper.TABLE_NAME, null, values)

        // Optionally, you can log the result or perform any other necessary actions
        if (newRowId != -1L) {
            // Insert successful
            // Log or handle success as needed
        } else {
            // Insert failed
            // Log or handle failure as needed
        }
    }

    fun getMostRecentBudget(): Int {
        val database = budgetDbHelper.readableDatabase

        // Query the database to get the most recent entry
        val cursor = database.query(
            BudgetDatabaseHelper.TABLE_NAME,
            arrayOf(BudgetDatabaseHelper.COLUMN_BUDGET),
            null,
            null,
            null,
            null,
            "${BudgetDatabaseHelper.COLUMN_BUDGET} DESC", // Order by ID in descending order
            null // Limit the result to one row
        )

        var recentBudget = 0

        // Retrieve the budget value from the cursor
        if (cursor.moveToFirst()) {
            recentBudget = cursor.getInt(cursor.getColumnIndexOrThrow(BudgetDatabaseHelper.COLUMN_BUDGET))
        }

        cursor.close()

        return recentBudget
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
        return dateFormat.format(calendar.time)
    }
}