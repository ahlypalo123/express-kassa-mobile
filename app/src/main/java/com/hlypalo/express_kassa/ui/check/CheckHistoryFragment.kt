package com.hlypalo.express_kassa.ui.check

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.model.OrderColumn
import com.hlypalo.express_kassa.data.repository.CheckRepository
import com.hlypalo.express_kassa.ui.main.NavigationFragment
import com.hlypalo.express_kassa.util.inflate
import kotlinx.android.synthetic.main.fragment_check_history.*
import kotlinx.android.synthetic.main.fragment_check_history.toolbar
import kotlinx.android.synthetic.main.item_check_history.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class CheckHistoryFragment : Fragment() {

    private val adapter: Adapter by lazy { Adapter() }
    private val repo: CheckRepository by lazy { CheckRepository() }
    private val items: MutableList<Check> by lazy { mutableListOf() }
    private var column: OrderColumn = OrderColumn.DATE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)

        list_order_history?.layoutManager = LinearLayoutManager(context)
        list_order_history?.adapter = adapter

        text_date?.setOnClickListener {
            updateColumn(OrderColumn.DATE)
        }
        text_total?.setOnClickListener {
            updateColumn(OrderColumn.TOTAL)
        }
        text_history_employee?.setOnClickListener {
            updateColumn(OrderColumn.EMPLOYEE_NAME)
        }

        updateList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val fragment = activity?.supportFragmentManager
                    ?.findFragmentByTag(NavigationFragment::class.java.simpleName)
                (fragment as? NavigationFragment)?.openDrawer()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateColumn(orderColumn: OrderColumn) {
        column = orderColumn
        updateList()
    }

    private fun updateList() {
        listOf<TextView?>(text_history_employee, text_total, text_date).forEach {
            it?.typeface = Typeface.DEFAULT
        }
        val field = when (column) {
            OrderColumn.EMPLOYEE_NAME -> text_history_employee
            OrderColumn.TOTAL -> text_total
            OrderColumn.DATE -> text_date
        }
        field?.typeface = Typeface.DEFAULT_BOLD

        CoroutineScope(Dispatchers.IO).launch {
            items.clear()
            repo.getCheckHistory(column)?.let { items.addAll(it) }
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_check_history))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: Check) = with(itemView) {
                text_item_date?.text = DateTime(item.date).toString("yyyy-MM-dd hh:ss")
                text_item_employee?.text = item.employeeName
                text_item_total?.text = item.total.toString()
                setOnClickListener {
                    activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        ?.replace(R.id.container, CheckDetailsFragment(item))
                        ?.addToBackStack(null)?.commit()
                }
            }
        }

    }

}