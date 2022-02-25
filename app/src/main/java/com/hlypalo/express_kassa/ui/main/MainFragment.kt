package com.hlypalo.express_kassa.ui.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.ui.check.CheckFragment
import com.hlypalo.express_kassa.ui.product.BarCodeFragment
import com.hlypalo.express_kassa.ui.product.ProductFragment
import com.hlypalo.express_kassa.util.inflate
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_cart.view.*

class MainFragment : Fragment(), CartView {

    private val presenter: CartPresenter by lazy { CartPresenter(this, this) }
    private val cartAdapter: CartAdapter by lazy { CartAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_cart?.layoutManager = LinearLayoutManager(context)
        list_cart?.adapter = cartAdapter

        presenter.init()

        btn_check_pay?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content_navigation, CheckFragment())
                ?.addToBackStack(null)?.commit()
        }

        pager_main?.adapter = MainPagerAdapter(this)
        TabLayoutMediator(tab_layout_main, pager_main) { tab, position ->
            tab.text = if(position == 0) {
                "Список"
            } else {
                "Штрих код"
            }
        }.attach()
    }

    override fun updateCart() {
        cartAdapter.notifyDataSetChanged()
    }

    override fun updateTotal(total: Float) {
        btn_check_pay?.isEnabled = total != 0F
        btn_check_pay?.text = "Оплатить $total"
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        presenter.deleteFromCart(item.groupId)
        return super.onContextItemSelected(item)
    }

    class MainPagerAdapter(owner: Fragment) : FragmentStateAdapter(owner) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return if(position == 0) {
                ProductFragment()
            } else {
                BarCodeFragment()
            }
        }

    }

    inner class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent.inflate(R.layout.item_cart))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(presenter.getProductListForCart()[position])
        }

        override fun getItemCount(): Int = presenter.getProductListForCart().size

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