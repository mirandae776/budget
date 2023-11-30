package edu.msoe.budget_app

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import edu.msoe.budget_app.database.AppDatabase
import edu.msoe.budget_app.database.Repository
import edu.msoe.budget_app.databinding.ActivityMainBinding
import edu.msoe.budget_app.entities.BudgetDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    companion object {
        lateinit var database: AppDatabase
        lateinit var budgetRepository: Repository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            // Move the database initialization to the main dispatcher
            database = withContext(Dispatchers.IO) {
                Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "budget-database"
                ).build()
            }

            // Initialize the repository using the database
            budgetRepository = Repository(database.budgetDao())

            // Perform database operations on a background thread
            withContext(Dispatchers.IO) {
                val initialBudget = BudgetDetail(
                    id = UUID.randomUUID(),
                    amountSpent = 0.0,
                    date = Date()
                )
                budgetRepository.addBudgetDetail(initialBudget)
                Log.d("BudgetApp", "Initial budget added: $initialBudget")
            }
        }

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
}