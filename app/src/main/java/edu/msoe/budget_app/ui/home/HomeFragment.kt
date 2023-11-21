package edu.msoe.budget_app.ui.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import edu.msoe.budget_app.DataViewModel
import edu.msoe.budget_app.R
import edu.msoe.budget_app.databinding.FragmentHomeBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val df = DecimalFormat("#.##")
    lateinit var dateAndMoneyArray: Array<String>
    private lateinit var root: View
    private lateinit var selectedDateTextView: TextView





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



        val testing = getLinesForMonth(viewModel.data,monthNameToNumber(viewModel.selectedMonth))



        dateAndMoneyArray = testing.toTypedArray()

        Log.d("AH", viewModel.data.toString())
        // on below line we are creating and initializing
        // variable for simple date format.
        val sdf = SimpleDateFormat("MM-dd-yyyy")

        // on below line we are creating a variable for
        // current date and time and calling a simple
        // date format in it.
        val currentDate = sdf.format(Date())
        var dateParts = currentDate.split("-")
        val currentMonth = dateParts[0]
        val currentMonthName = viewModel.selectedMonth


//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        root = binding.root


        val tableLayout = root.findViewById<TableLayout>(R.id.tableLayout) // Assuming you have a TableLayout in your layout file with the ID 'tableLayout'
        val addButton = root.findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            showAddPopup()
        }
        recreateTable()
//        val totalView = root.findViewById<TextView>(R.id.totalText)
//        totalView.text =  "Total Spending is: " + df.format(total) + " dollars"
        val currentMonthText = root.findViewById<TextView>(R.id.currentMonthText)
        currentMonthText.text = "Current Spending For: $currentMonthName"

        val budgetText = root.findViewById<TextView>(R.id.budgetText)
        budgetText.text = "You're budget is: " + viewModel.budget


        //val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }


    private fun showAddPopup() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.popup_add, null)
        builder.setView(dialogView)

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
            viewModel.data.add("$formattedDate,$amount")

            // Dismiss the popup
            alertDialog.dismiss()

            // Recreate the table with updated data
            recreateTable()
        }

        cancelButton.setOnClickListener {
            // Dismiss the popup
            alertDialog.dismiss()
        }

        alertDialog.show()
    }



    private fun recreateTable() {
        // Clear existing views from the table
        println("CALLED")
        val tableLayout = root.findViewById<TableLayout>(R.id.tableLayout)
        tableLayout.removeAllViews()

        // Get the updated data
        val testing = getLinesForMonth(
            activityViewModels<DataViewModel>().value.data,
            monthNameToNumber(activityViewModels<DataViewModel>().value.selectedMonth)
        )

        var total = 0.0 // Declare the total variable here

        // Get the budget from the ViewModel
        val budget = activityViewModels<DataViewModel>().value.budget.toDouble()

        // Re-create the table with the updated data
        for (item in testing) {
            val parts = item.split(",") // Split the string into date and money parts
            if (parts.size == 2) {
                val date = parts[0]
                val moneySpent = parts[1]

                val row = TableRow(root.context) // Create a new TableRow

                val dateTextView = TextView(root.context) // Create a TextView for the date
                dateTextView.text = date

                val dateLayoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                dateLayoutParams.weight = 1f // Set the layout weight to 1
                dateTextView.layoutParams = dateLayoutParams

                row.addView(dateTextView)

                val moneySpentTextView = TextView(root.context) // Create a TextView for the money spent
                val formattedMoneySpent = df.format(moneySpent.toDouble())
                moneySpentTextView.text = formattedMoneySpent

                val moneySpentLayoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                moneySpentLayoutParams.weight = 1f // Set the layout weight to 1
                moneySpentTextView.layoutParams = moneySpentLayoutParams

                row.addView(moneySpentTextView) // Add the money spent TextView to the TableRow

                // Update the total with the formattedMoneySpent value
                total += formattedMoneySpent.toDouble()

                // Check if the total exceeds the budget and highlight the row if needed
                if (total > budget) {
                    row.setBackgroundColor(root.resources.getColor(android.R.color.holo_red_light))
                }

                tableLayout.addView(row) // Add the TableRow to the TableLayout
            }
            val blankView = TextView(root.context)
            blankView.text = ""
            val blankRow = TableRow(root.context)
            blankRow.addView(blankView)
            tableLayout.addView(blankRow)
        }

        // Update the totalView
        val totalView = root.findViewById<TextView>(R.id.totalText)
        println(total)
        totalView.text = "Total Spending is: " + df.format(total) + " dollars"
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