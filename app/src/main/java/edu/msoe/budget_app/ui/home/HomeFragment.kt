package edu.msoe.budget_app.ui.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import edu.msoe.budget_app.DataViewModel
import edu.msoe.budget_app.MainActivity.Companion.spendingDbHelper
//import edu.msoe.budget_app.MainActivity.Companion.budgetRepository
import edu.msoe.budget_app.R
import edu.msoe.budget_app.databinding.FragmentHomeBinding
import edu.msoe.budget_app.entities.SpendingDetail
import edu.msoe.budget_app.database.BudgetDatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import edu.msoe.budget_app.MainActivity
import edu.msoe.budget_app.database.SpendingDatabaseHelper
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val df = DecimalFormat("#.##")
    lateinit var dateAndMoneyArray: Array<String>
    private lateinit var root: View
    private lateinit var selectedDateTextView: TextView
    private lateinit var SpendingDetails: List<SpendingDetail>




    private  fun calculateTotal(): Double {
        var total = 0.0;

        for(item in dateAndMoneyArray){
            val parts = item.split(",")
            val amount = parts[1]
            total += amount.toDouble()

        }
        return total
    }
    fun monthNameToNumber(monthName: String): String {
        val monthNumber = when (monthName.uppercase()) {
            "JANUARY" -> 1
            "FEBRUARY" -> 2
            "MARCH" -> 3
            "APRIL" -> 4
            "MAY" -> 5
            "JUNE" -> 6
            "JULY" -> 7
            "AUGUST" -> 8
            "SEPTEMBER" -> 9
            "OCTOBER" -> 10
            "NOVEMBER" -> 11
            "DECEMBER" -> 12
            else -> -1 // or any other value to represent an invalid month name
        }

        // Convert the month number to a string with leading zeros for single digits
        return if (monthNumber in 1..9) {
            "0$monthNumber"
        } else {
            monthNumber.toString()
        }
    }

    fun getLinesForMonth(data: MutableList<String>, targetMonth: String): List<String> {
        val result = mutableListOf<String>()
        for (entry in data) {
            val parts = entry.split(",")
            if (parts.size == 2) {
                val date = parts[0]
                val month = date.split("-")[1]
                if (month == targetMonth) {
                    result.add(entry)
                }
            }
        }
        return result
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //var viewModel: DataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        val viewModel by activityViewModels<DataViewModel>()
        var spendingEntries = viewModel.getSpendingDetails(this.requireContext())
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentMonthString = currentMonth.toString()


        //val testing = getLinesForMonth(viewModel.data,monthNameToNumber(viewModel.selectedMonth))

        //dateAndMoneyArray = testing.toTypedArray()
        // on below line we are creating and initializing
        // variable for simple date format.
        val sdf = SimpleDateFormat("MM-dd-yyyy")

        // on below line we are creating a variable for
        // current date and time and calling a simple
        // date format in it.
        val currentDate = sdf.format(Date())
        var dateParts = currentDate.split("-")
//        val currentMonth = dateParts[0]
//        val currentMonthName = viewModel.selectedMonth


//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        root = binding.root

        val tableLayout = root.findViewById<TableLayout>(R.id.tableLayout) // Assuming you have a TableLayout in your layout file with the ID 'tableLayout'

        val addButton = root.findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            showAddPopup(spendingEntries)
        }
        updateUI(viewModel)



        viewModel.spendingDetails.observe(viewLifecycleOwner) { spendingDetails ->
            if (spendingDetails.isNotEmpty()) {

                updateUI(viewModel)
            }
        }


//        val totalView = root.findViewById<TextView>(R.id.totalText)
//        totalView.text =  "Total Spending is: " + df.format(total) + " dollars"
        val currentMonthText = root.findViewById<TextView>(R.id.currentMonthText)
        //currentMonthText.text = "Current Spending For: $currentMonthName"

        val budgetText = root.findViewById<TextView>(R.id.budgetText)
        val budget = viewModel.getBudget()
        budgetText.text = "You're budget is: " + budget


        //val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    private fun updateUI(viewModel: DataViewModel) {
        val spendingDetails = viewModel.getSpendingDetails(requireContext())
        val tableLayout = root.findViewById<TableLayout>(R.id.tableLayout)
        tableLayout.removeAllViews()

        var total = 0.0
        val budget = viewModel.getBudget()

        val headerRow = TableRow(requireContext())

        val headerDateTextView = TextView(requireContext())
        headerDateTextView.text = "Date"
        headerDateTextView.setTextColor(Color.WHITE)
        headerDateTextView.layoutParams = getTableRowLayoutParams()
        headerRow.addView(headerDateTextView)

        val headerAmountSpentTextView = TextView(requireContext())
        headerAmountSpentTextView.text = "Amount Spent"
        headerAmountSpentTextView.setTextColor(Color.WHITE)
        headerAmountSpentTextView.layoutParams = getTableRowLayoutParams()
        headerRow.addView(headerAmountSpentTextView)

        val headerDeleteIcon = ImageView(root.context)
        headerDeleteIcon.setImageResource(R.drawable.ic_delete) // Use your delete icon resource
        headerDeleteIcon.layoutParams = getTableRowLayoutParams()
        headerRow.addView(headerDeleteIcon)

        tableLayout.addView(headerRow)

        val selectedMonth = viewModel.selectedMonth

        for (item in spendingDetails) {
            val calendar = Calendar.getInstance()
            calendar.time = item.date
            val spendingDetailMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(item.date)

            if (spendingDetailMonth.equals(selectedMonth, ignoreCase = true)) {
                val date = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(item.date)
                val formattedMoneySpent = df.format(item.amountSpent)

                val row = TableRow(root.context)

                val dateTextView = TextView(root.context)
                dateTextView.text = date
                dateTextView.setTextColor(Color.WHITE)
                dateTextView.layoutParams = getTableRowLayoutParams()
                row.addView(dateTextView)

                val moneySpentTextView = TextView(root.context)
                moneySpentTextView.text = formattedMoneySpent
                moneySpentTextView.setTextColor(Color.WHITE)
                moneySpentTextView.layoutParams = getTableRowLayoutParams()
                row.addView(moneySpentTextView)

                val deleteIcon = ImageView(root.context)
                deleteIcon.setImageResource(R.drawable.ic_delete) // Use your delete icon resource
                deleteIcon.layoutParams = getTableRowLayoutParams()
                deleteIcon.setOnClickListener {
                    deleteSpendingDetailByUUID(item.id.toString())
                    updateUI(viewModel)
                }
                row.addView(deleteIcon)

                total += item.amountSpent

                if (total > budget) {
                    row.setBackgroundColor(root.resources.getColor(android.R.color.holo_red_light))
                }

                tableLayout.addView(row)

                val blankView = TextView(root.context)
                blankView.text = ""
                blankView.layoutParams = getTableRowLayoutParams()
                val blankRow = TableRow(root.context)
                blankRow.addView(blankView)
                tableLayout.addView(blankRow)
            }
        }

        val totalText = root.findViewById<TextView>(R.id.totalText)
        totalText.text = "Total Spending is: $total"
    }





    // Helper function to get TableRow.LayoutParams with equal weight
    private fun getTableRowLayoutParams(): TableRow.LayoutParams {
        return TableRow.LayoutParams(
            0,
            TableRow.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1f
        }
    }







    private fun showAddPopup(spendingDetails: List<SpendingDetail>) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.popup_add, null)
        builder.setView(dialogView)
        val tableLayout = root.findViewById<TableLayout>(R.id.tableLayout)
        selectedDateTextView = dialogView.findViewById(R.id.selectedDateTextView)

        // Set the initial value of selectedDateTextView to the current date
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
        selectedDateTextView.text = "Selected Date: $currentDate"

        val datePickerButton = dialogView.findViewById<Button>(R.id.datePickerButton)
        val amountEditText = dialogView.findViewById<EditText>(R.id.amountEditText)
        val addButton = dialogView.findViewById<Button>(R.id.addButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)


        val cal = Calendar.getInstance()
        var selectedDate = cal.time

        datePickerButton.setOnClickListener {
            val datePicker = DatePickerFragment { year, month, day ->
                cal.set(year, month, day)
                selectedDate = cal.time

                // Update selectedDateTextView when a new date is selected
                val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(selectedDate)
                selectedDateTextView.text = "Selected Date: $formattedDate"
            }
            datePicker.show(childFragmentManager, "datePicker")
        }

        val alertDialog = builder.create()

        addButton.setOnClickListener {
            val viewModel = activityViewModels<DataViewModel>().value
            val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(selectedDate)

            val amount = amountEditText.text.toString().trim()

            if (amount.isEmpty()) {
                // Show warning message if the amount is empty
                amountEditText.error = "Amount must be entered"
                return@setOnClickListener
            }

            // Add the new entry to the data array
            //viewModel.data.add("$formattedDate,$amount")

            val amountDouble = amount.toDouble()
            val spendingDetail = SpendingDetail(
                id = UUID.randomUUID(),
                amountSpent = amountDouble,
                date = SimpleDateFormat("yyyy-MM-dd").parse(formattedDate) ?: Date()
            )

            addSpendingDetail(spendingDetail)
            Log.d("BudgetApp", "Initial budget added: $spendingDetail")

            // Dismiss the popup
            alertDialog.dismiss()

            // Recreate the table with updated data
            //recreateTable(viewModel, spendingDetails)

            updateUI(viewModel)


            cancelButton.setOnClickListener {
                // Dismiss the popup
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
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

    private fun deleteSpendingDetailByUUID(uuid: String) {
        val db = spendingDbHelper?.writableDatabase

        // Specify the WHERE clause to delete the record with the given UUID
        val whereClause = "${SpendingDatabaseHelper.COLUMN_ID} = ?"

        // Specify the value for the WHERE clause
        val whereArgs = arrayOf(uuid)

        // Perform the delete operation
        db?.delete(SpendingDatabaseHelper.TABLE_NAME, whereClause, whereArgs)
    }



    private fun recreateTable(viewModel: DataViewModel, spendingDetails: List<SpendingDetail>) {
        val tableLayout = root.findViewById<TableLayout>(R.id.tableLayout)
        tableLayout.removeAllViews()

        var total = 0.0 // Declare the total variable here
        val budget = viewModel.getBudget()
        val spend = viewModel.getSpendingDetails(requireContext())



        // Add header row
        val headerRow = TableRow(requireContext())

        val headerDateTextView = TextView(requireContext())
        headerDateTextView.text = "Date"
        headerDateTextView.setTextColor(Color.WHITE)
        headerDateTextView.layoutParams = getTableRowLayoutParams()
        headerRow.addView(headerDateTextView)

        val headerAmountSpentTextView = TextView(requireContext())
        headerAmountSpentTextView.text = "Amount Spent"
        headerAmountSpentTextView.setTextColor(Color.WHITE)
        headerAmountSpentTextView.layoutParams = getTableRowLayoutParams()
        headerRow.addView(headerAmountSpentTextView)

        tableLayout.addView(headerRow)

        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

        for (item in spend) {
                val calendar = Calendar.getInstance()
                calendar.time = item.date
                val spendingDetailMonth = calendar.get(Calendar.MONTH) + 1

                if (spendingDetailMonth == currentMonth) {
                    val date = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(item.date)
                    val formattedMoneySpent = df.format(item.amountSpent)

                    val row = TableRow(root.context) // Create a new TableRow

                    val dateTextView = TextView(root.context) // Create a TextView for the date
                    dateTextView.text = date
                    dateTextView.setTextColor(Color.WHITE) // Set text color to white
                    dateTextView.layoutParams = getTableRowLayoutParams()

                    row.addView(dateTextView)

                    val moneySpentTextView = TextView(root.context) // Create a TextView for the money spent
                    moneySpentTextView.text = formattedMoneySpent
                    moneySpentTextView.setTextColor(Color.WHITE) // Set text color to white
                    moneySpentTextView.layoutParams = getTableRowLayoutParams()

                    row.addView(moneySpentTextView) // Add the money spent TextView to the TableRow

                    // Update the total with the formattedMoneySpent value
                    total += item.amountSpent

                    // Check if the total exceeds the budget and highlight the row if needed
                    if (total > budget) {
                        row.setBackgroundColor(root.resources.getColor(android.R.color.holo_red_light))
                    }

                    tableLayout.addView(row) // Add the TableRow to the TableLayout

                    val blankView = TextView(root.context)
                    blankView.text = ""
                    blankView.layoutParams = getTableRowLayoutParams()
                    val blankRow = TableRow(root.context)
                    blankRow.addView(blankView)
                    tableLayout.addView(blankRow)
                }
            }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    class DatePickerFragment(private val onDateSetListener: (Int, Int, Int) -> Unit) :
        DialogFragment(), DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // Create a new instance of DatePickerDialog and return it
            return DatePickerDialog(requireActivity(), this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            onDateSetListener(year, month, day)
        }
    }

}