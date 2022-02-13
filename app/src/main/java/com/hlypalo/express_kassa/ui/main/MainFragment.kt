package com.hlypalo.express_kassa.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.ui.check.CheckFragment
import com.hlypalo.express_kassa.ui.view.ProductAdapter
import com.hlypalo.express_kassa.util.inflate
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_cart.view.*

class MainFragment : Fragment(), MainView {

    private val presenter: MainPresenter by lazy { MainPresenter(this, this) }
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter(presenter.getProductList()) {
            presenter.addProductToCart(it)
        }
    }
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

        list_products?.layoutManager = LinearLayoutManager(context)
        list_products?.adapter = productAdapter

        list_cart?.layoutManager = LinearLayoutManager(context)
        list_cart?.adapter = cartAdapter

        presenter.init()

        input_main_filter?.addTextChangedListener {
            presenter.updateFilteredList()
        }

        btn_check_pay?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content_navigation, CheckFragment())
                ?.addToBackStack(null)?.commit()
        }
    }

    override fun updateProductList() {
        productAdapter.notifyDataSetChanged()
    }

    override fun updateCart() {
        cartAdapter.notifyDataSetChanged()
    }

    override fun updateTotal(total: Float) {
        btn_check_pay?.isEnabled = total != 0F
        btn_check_pay?.text = "Оплатить $total"
    }

    override fun getFilter(): String? {
        return input_main_filter?.text?.toString()
    }

    inner class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent.inflate(R.layout.item_cart))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(presenter.getProductListForCart()[position])
        }

        override fun getItemCount(): Int = presenter.getProductListForCart().size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: CartDto) = with(itemView) {
                text_cart_name?.text = item.name
                text_cart_price?.text = (item.price * item.count).toString()
                text_cart_count?.text = item.count.toString()
            }
        }
    }

}