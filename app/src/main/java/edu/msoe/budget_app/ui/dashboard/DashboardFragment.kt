package edu.msoe.budget_app.ui.dashboard

import edu.msoe.budget_app.database.BudgetDatabaseHelper
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import edu.msoe.budget_app.DataViewModel
import edu.msoe.budget_app.R
import edu.msoe.budget_app.databinding.FragmentDashboardBinding
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.Month

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var dateAndMoneyArray: MutableList<String>
    lateinit var tableLayout: TableLayout
    private val df = DecimalFormat("#.##")
    private lateinit var budgetDatabaseHelper: BudgetDatabaseHelper


    fun calculateTotalValuesByMonth(data: MutableList<String>): Array<String> {
        val monthTotalMap = mutableMapOf<String, Double>()

        for (entry in data) {
            val parts = entry.split(",")
            if (parts.size == 2) {
                val date = parts[0]
                val localDate = LocalDate.parse(date)
                val month = localDate.month.toString()
                val value = parts[1].toDouble()

                // Add the value to the total for the corresponding month
                if (monthTotalMap.containsKey(month)) {
                    monthTotalMap[month] = monthTotalMap[month]!! + value
                } else {
                    monthTotalMap[month] = value
                }
            }
        }

        // Create an array of "Month, total" strings
        val result = monthTotalMap.entries.map { entry ->
            val monthName = Month.valueOf(entry.key).toString()
            "${monthName}, ${entry.value}"
        }.toTypedArray()

        return result
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        //var viewModel: DataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        val viewModel by activityViewModels<DataViewModel>()
        //dateAndMoneyArray = context?.let { viewModel.getSpendingDetails(it) }
        //val testing = calculateTotalValuesByMonth(dateAndMoneyArray)





        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tableLayout = root.findViewById(R.id.yearTable)

        budgetDatabaseHelper = BudgetDatabaseHelper(requireContext())

        val dataButton = root.findViewById<Button>(R.id.testDataButton)
        var spendingDetails = context?.let { viewModel.getSpendingDetails(it) }


        if (spendingDetails != null) {
            for (item in spendingDetails) {


                val row = TableRow(root.context) // Create a new TableRow


                val dateTextView = TextView(root.context) // Create a TextView for the date
                dateTextView.text = item.date.toString()
                dateTextView.setTextColor(Color.WHITE)

                val dateLayoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                dateLayoutParams.weight = 1f // Set the layout weight to 1
                dateTextView.layoutParams = dateLayoutParams

                row.addView(dateTextView)

                val moneySpentTextView = TextView(root.context) // Create a TextView for the money spent
                moneySpentTextView.text = df.format(item.amountSpent)
                moneySpentTextView.setTextColor(Color.WHITE)

                val moneySpentLayoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                moneySpentLayoutParams.weight = 1f // Set the layout weight to 1
                moneySpentTextView.layoutParams = moneySpentLayoutParams

                row.addView(moneySpentTextView) // Add the money spent TextView to the TableRow
                // Add the money spent TextView to the TableRow

                row.setOnClickListener(View.OnClickListener {
                    // Handle the row click event here
                    val clickedRow = it as TableRow
                    val date = (clickedRow.getChildAt(0) as TextView).text.toString()
                    val value = (clickedRow.getChildAt(1) as TextView).text.toString()
                    println("Clicked Row: Date = $date, Value = $value")
                    viewModel.selectedMonth = date
                    println(viewModel.selectedMonth)
                    val navController = Navigation.findNavController(it)
                    navController.navigate(R.id.action_navigation_dashboard_to_navigation_home)
                })

                tableLayout.addView(row) // Add the TableRow to the TableLayout
            }
            val blankView = TextView(root.context)
            blankView.text = "";
            val blankRow = TableRow(root.context)
            blankRow.addView(blankView)
            tableLayout.addView(blankRow)

        }

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }




    private fun getTotalBudgetData(): Double {
        val db = budgetDatabaseHelper.readableDatabase

        val projection = arrayOf(BudgetDatabaseHelper.COLUMN_BUDGET)
        val cursor = db.query(
            BudgetDatabaseHelper.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        var totalBudget = 0.0

        with(cursor) {
            while (moveToNext()) {
                val budgetValue = getDouble(getColumnIndexOrThrow(BudgetDatabaseHelper.COLUMN_BUDGET))
                totalBudget += budgetValue
            }
            close()
        }

        return totalBudget
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}