package com.hlypalo.express_kassa.ui.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CheckProduct
import com.hlypalo.express_kassa.data.model.ErrorBody
import com.hlypalo.express_kassa.ui.check.CheckFragment
import com.hlypalo.express_kassa.ui.product.BarCodeFragment
import com.hlypalo.express_kassa.ui.product.ProductFragment
import com.hlypalo.express_kassa.util.inflate
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_check_product_main.*
import kotlinx.android.synthetic.main.item_check_product_main.view.*

class MainFragment : Fragment(), MainView {

    private val presenter: MainPresenter by lazy { MainPresenter(this) }
    private val cartAdapter: CartAdapter by lazy { CartAdapter() }

    companion object {
        private const val ORDER_DELETE_FROM_CART = 14
    }

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
            presenter.onPay()
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

    override fun onDestroyView() {
        presenter.unsubscribe()
        super.onDestroyView()
    }

    override fun showPaymentMethodDialog() {
        activity?.supportFragmentManager?.let {
            PaymentMethodDialog(this).show(it, null)
        }
    }

    override fun showChangeDialog() {
        activity?.supportFragmentManager?.let {
            ChangeDialog(this).show(it, null)
        }
    }

    override fun showError(err: ErrorBody?) {
        activity?.showError(err)
    }

    override fun toggleProgress(f: Boolean) {
        progress_bar?.visibility = if (f) View.VISIBLE else View.INVISIBLE
    }

    override fun openCheckFragment() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
            ?.replace(R.id.container, CheckFragment())
            ?.addToBackStack(null)?.commit()
    }

    override fun manageCheckEmpty(b: Boolean) {
        text_check_empty?.visibility = if (b) View.VISIBLE else View.GONE
    }

    override fun updateCheck() {
        cartAdapter.notifyDataSetChanged()
    }

    override fun updateTotal(total: Float?) {
        btn_check_pay?.isEnabled = total != null && total != 0F
        btn_check_pay?.text = "Оплатить ${total ?: ""}"
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.order == ORDER_DELETE_FROM_CART) {
            presenter.deleteFromCart(item.groupId)
        }
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
            return ViewHolder(parent.inflate(R.layout.item_check_product_main))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(presenter.getProductListForCheck()[position])
        }

        override fun getItemCount(): Int = presenter.getProductListForCheck().size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
            fun bind(item: CheckProduct) = with(itemView) {
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
                    menu?.add(this.adapterPosition, it, ORDER_DELETE_FROM_CART, R.string.delete)
                }
            }
        }
    }

}