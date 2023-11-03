package edu.msoe.budget_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.msoe.budget_app.R
import edu.msoe.budget_app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val dateAndMoneyArray = arrayOf(
        "2023-11-01,25.50",
        "2023-10-30,19.99",
        "2023-10-28,35.75",
        "2023-10-27,42.00",
        "2023-10-26,15.25",
        "2023-10-25,50.75",
        "2023-10-24,10.00",
        "2023-10-23,30.50",
        "2023-10-22,27.75",
        "2023-10-21,18.99",
        "2023-10-20,12.35",
        "2023-10-19,22.00",
        "2023-10-18,8.75",
        "2023-10-17,75.60",
        "2023-10-16,11.25"
    )





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val tableLayout = root.findViewById<TableLayout>(R.id.tableLayout) // Assuming you have a TableLayout in your layout file with the ID 'tableLayout'
        for (item in dateAndMoneyArray) {
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
                moneySpentTextView.text = moneySpent

                val moneySpentLayoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                moneySpentLayoutParams.weight = 1f // Set the layout weight to 1
                moneySpentTextView.layoutParams = moneySpentLayoutParams

                row.addView(moneySpentTextView) // Add the money spent TextView to the TableRow
                // Add the money spent TextView to the TableRow

                tableLayout.addView(row) // Add the TableRow to the TableLayout
            }
            val blankView = TextView(root.context)
            blankView.text = "";
            val blankRow = TableRow(root.context)
            blankRow.addView(blankView)
            tableLayout.addView(blankRow)


        }


        //val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}