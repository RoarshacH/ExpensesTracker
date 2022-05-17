package com.example.expesestracker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expesestracker.models.PopulateContentForList
import com.example.expesestracker.models.SQLUtilities

/**
 * A fragment representing a list of Items.
 */
class DataItemFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                val pageContext = view.getContext().javaClass.toString()
                var sharedPrefs =  activity?.getSharedPreferences("test", Context.MODE_PRIVATE)


                if (pageContext == "class com.example.expesestracker.ExpensesActivity") {
                    sharedPrefs =  activity?.getSharedPreferences("test-expenses", Context.MODE_PRIVATE)
                } else if (pageContext == "class com.example.expesestracker.IncomeActivity") {
                    sharedPrefs =  activity?.getSharedPreferences("test_income", Context.MODE_PRIVATE)
                }

                val dbUtilities = SQLUtilities(view.context)
                PopulateContentForList.loadList(dbUtilities)

//                PopulateContentForList.loadList(sharedPrefs!!)
                adapter = MyDataItemRecyclerViewAdapter(PopulateContentForList.ITEMS)
            }
        }
        return view
    }



    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            DataItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}