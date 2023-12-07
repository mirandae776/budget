package edu.msoe.budget_app

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import edu.msoe.budget_app.database.BudgetDatabaseHelper
import edu.msoe.budget_app.database.DBHelper
import edu.msoe.budget_app.database.SpendingDatabaseHelper
//import edu.msoe.budget_app.database.Repository
import edu.msoe.budget_app.databinding.ActivityMainBinding
import edu.msoe.budget_app.entities.SpendingDetail
import java.util.Date
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    companion object {
        var spendingDbHelper: SpendingDatabaseHelper? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        spendingDbHelper?.close()
        spendingDbHelper = SpendingDatabaseHelper(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    private fun addSpendingDetail(detail: SpendingDetail) {
        val db = spendingDbHelper?.writableDatabase
        val values = ContentValues()

        // Use constants from SpendingDatabaseHelper
        values.put(SpendingDatabaseHelper.COLUMN_ID, detail.id.toString())
        values.put(SpendingDatabaseHelper.COLUMN_AMOUNT_SPENT, detail.amountSpent)
        values.put(SpendingDatabaseHelper.COLUMN_DATE, detail.date.toString())

        db?.insert(SpendingDatabaseHelper.TABLE_NAME, null, values)
    }
}