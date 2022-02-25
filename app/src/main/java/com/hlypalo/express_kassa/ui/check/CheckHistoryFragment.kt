package com.hlypalo.express_kassa.ui.check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.repository.CheckRepository
import com.hlypalo.express_kassa.util.inflate
import kotlinx.android.synthetic.main.fragment_check_history.*
import kotlinx.android.synthetic.main.item_check.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class CheckHistoryFragment : Fragment() {

    private val adapter: Adapter by lazy { Adapter() }
    private val repo: CheckRepository by lazy { CheckRepository() }
    private val items: MutableList<Check> by lazy { mutableListOf() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_order_history?.layoutManager = LinearLayoutManager(context)
        list_order_history?.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            items.addAll(repo.getCheckHistory())
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_check))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: Check) = with(itemView) {
                text_item_date?.text = DateTime(item.date).toString("yyyy-MM-dd")
                text_item_employee?.text = item.employeeName
                text_item_total?.text = item.total.toString()
            }
        }

    }

}