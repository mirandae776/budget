package edu.msoe.budget_app.ui.notifications

import edu.msoe.budget_app.database.BudgetDatabaseHelper
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import edu.msoe.budget_app.DataViewModel
import edu.msoe.budget_app.R
import edu.msoe.budget_app.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var budgetDatabaseHelper: BudgetDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val viewModel by activityViewModels<DataViewModel>()
        println(viewModel.getBudgets().get(viewModel.getBudgets().size-1))

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val changeButton = root.findViewById<Button>(R.id.changeBudgetButton)
        val budgetText = root.findViewById<TextView>(R.id.budgetTextView)
        val needsText = root.findViewById<TextView>(R.id.needsTextView)
        val wantsText = root.findViewById<TextView>(R.id.wantsTextView)
        val savingsText = root.findViewById<TextView>(R.id.savingsTextView)
        val basedOnText = root.findViewById<TextView>(R.id.basedOnText)
        var budget = viewModel.selectedBudget
        budgetText.text = budget.toString()

        var changeBudgetEditText = root.findViewById<EditText>(R.id.changeBudgetText)
        var needsAllocation = getString(R.string.needs_allocation, (budget * 0.50).toString())
        var wantsAllocation = getString(R.string.wants_allocation, (budget * 0.30).toString())
        var savingsAllocation = getString(R.string.savings_allocation, (budget * 0.20).toString())
        var basedOn = getString(R.string.basedOn, budget)
        needsText.text = needsAllocation;
        wantsText.text = wantsAllocation
        savingsText.text = savingsAllocation
        basedOnText.text = basedOn

        budgetDatabaseHelper = BudgetDatabaseHelper(requireContext())
        val forbesLinkTextView: TextView = root.findViewById(R.id.forbesLink)




        changeButton.setOnClickListener {
            val newBudgetValueString = changeBudgetEditText.text.toString()



            // Validate if the entered value is a valid integer
            if (newBudgetValueString.isNotEmpty()) {
                println("did this run")
                val newBudgetValue = newBudgetValueString.toInt()

                // Insert the new budget value into the database
                //insertBudgetData(newBudgetValue)
                viewModel.addBudgetToDatabase(newBudgetValue)

                println("Farm view model: " + viewModel.selectedBudget)
                println(viewModel.getBudgets()[viewModel.getBudgets().size-1])
                viewModel.selectedBudget = viewModel.getBudgets()[viewModel.getBudgets().size-1]




                // Update viewModel.budget and UI with the new budget value
                budget = viewModel.selectedBudget
                needsAllocation = getString(R.string.needs_allocation, (budget * 0.50).toString())
                wantsAllocation = getString(R.string.wants_allocation, (budget * 0.30).toString())
                savingsAllocation = getString(R.string.savings_allocation, (budget * 0.20).toString())
                basedOn = getString(R.string.basedOn, budget)
                needsText.text = needsAllocation;
                wantsText.text = wantsAllocation
                savingsText.text = savingsAllocation
                basedOnText.text = basedOn
                budgetText.text = viewModel.selectedBudget.toString()

            } else {
                // Show a toast message for an invalid budget value
                Toast.makeText(
                    context,
                    "Invalid budget value entered. Please enter a valid number.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


//        val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }


        fun setUnderlinedText(textView: TextView, text: String) {
            val content = SpannableString(text)
            content.setSpan(UnderlineSpan(), 0, text.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            textView.text = content
        }

        fun openForbesLink(view: View) {
            val forbesUrl = "https://www.forbes.com/advisor/banking/guide-to-50-30-20-budget/"

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(forbesUrl))
            startActivity(browserIntent)
        }

        setUnderlinedText(forbesLinkTextView, "More info on Budget Split")
        forbesLinkTextView.setOnClickListener {
            openForbesLink(it)
        }
        return root
    }







    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}