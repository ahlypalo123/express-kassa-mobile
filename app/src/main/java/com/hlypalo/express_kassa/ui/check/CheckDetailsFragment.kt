package com.hlypalo.express_kassa.ui.check

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.util.inflate
import kotlinx.android.synthetic.main.item_cart.view.*

class CheckDetailsFragment(private var check: Check) : Fragment() {

    companion object {
        private const val ARG_CHECK = "check"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (savedInstanceState?.getSerializable(ARG_CHECK) as? Check)?.let {
            check = it
        }
        return inflater.inflate(R.layout.fragment_check_details, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(ARG_CHECK, check)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    inner class Adapter(private val items: List<CartDto>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent.inflate(R.layout.item_cart))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
            fun bind(item: CartDto) = with(itemView) {
                text_cart_name?.text = item.name
                text_cart_price?.text = (item.price * item.count).toString()
                text_cart_count?.text = item.count.toString()
                setOnCreateContextMenuListener(this@ViewHolder)
            }

            override fun onCreateContextMenu(
                menu: ContextMenu?,
                v: View?,
                menuInfo: ContextMenu.ContextMenuInfo?
            ) {
                v?.id?.let {
                    menu?.add(this.adapterPosition, it, 0, R.string.delete)
                }
            }
        }
    }

}