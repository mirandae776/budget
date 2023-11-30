package edu.msoe.budget_app.ui.notifications

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val viewModel by activityViewModels<DataViewModel>()

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val changeButton = root.findViewById<Button>(R.id.changeBudgetButton)
        val budgetText = root.findViewById<TextView>(R.id.budgetTextView)
        val needsText = root.findViewById<TextView>(R.id.needsTextView)
        val wantsText = root.findViewById<TextView>(R.id.wantsTextView)
        val savingsText = root.findViewById<TextView>(R.id.savingsTextView)
        val basedOnText = root.findViewById<TextView>(R.id.basedOnText)
        val infoTextView = root.findViewById<TextView>(R.id.infoTextView)
        budgetText.text = viewModel.budget.toString()

        val changeBudgetEditText = root.findViewById<EditText>(R.id.changeBudgetText)
        val needsAllocation = getString(R.string.needs_allocation, (viewModel.budget * 0.50).toString())
        val wantsAllocation = getString(R.string.wants_allocation, (viewModel.budget * 0.30).toString())
        val savingsAllocation = getString(R.string.savings_allocation, (viewModel.budget * 0.20).toString())
        val basedOn = getString(R.string.basedOn, viewModel.budget)
        needsText.text = needsAllocation;
        wantsText.text = wantsAllocation
        savingsText.text = savingsAllocation
        println("HAI" + savingsAllocation)
        basedOnText.text = basedOn
        val infoText = "This budget split is called the 50/30/20 rule\n " +
                "a popular split where 50% of your budget goes to your needs\n" +
                "30% goes to your wants,\n" +
                "and 20% goes to savings\n" +
                "for more info;"
        infoTextView.text =  infoText

        val forbesLinkTextView: TextView = root.findViewById(R.id.forbesLink)




        changeButton.setOnClickListener{
            budgetText.text = changeBudgetEditText.text
            viewModel.budget = changeBudgetEditText.text.toString().toInt()
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

        setUnderlinedText(forbesLinkTextView, "Click Here")
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