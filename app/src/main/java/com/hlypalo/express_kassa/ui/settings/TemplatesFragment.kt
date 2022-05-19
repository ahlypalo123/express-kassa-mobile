package com.hlypalo.express_kassa.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.ReceiptTemplate
import com.hlypalo.express_kassa.data.model.ReceiptTemplateData
import com.hlypalo.express_kassa.data.repository.MerchantRepository
import com.hlypalo.express_kassa.ui.main.NavigationFragment
import com.hlypalo.express_kassa.util.CheckBuilder
import com.hlypalo.express_kassa.util.enqueue
import com.hlypalo.express_kassa.util.inflate
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_edit_template.*
import kotlinx.android.synthetic.main.fragment_template.*
import kotlinx.android.synthetic.main.fragment_template.toolbar
import kotlinx.android.synthetic.main.item_image.*
import kotlinx.android.synthetic.main.item_image.view.*

class TemplatesFragment : Fragment() {

    private val repo: MerchantRepository by lazy { MerchantRepository.instance }
    private val api: ApiService by lazy { ApiService.getInstance() }
    private val list: MutableList<ReceiptTemplate> = mutableListOf()
    private val adapter = Adapter()
    private var lastActive = -1
    private lateinit var updatedTemplate: ReceiptTemplate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_template, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)

        // pager_templates?.adapter =
        pager_templates?.adapter = adapter
        pager_templates?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val item = list[position]
                toggleButtonEnabled(!item.active)
                btn_edit?.setOnClickListener {
                    edit(item)
                }
                btn_apply?.setOnClickListener {
                    updateActive(item, position)
                }
            }
        })

        TabLayoutMediator(tab_templates, pager_templates) { tab, _ ->
            tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.shape_gray_circle)
        }.attach()

        btn_create?.setOnClickListener {
            create()
        }
        updateList()
    }

    private fun toggleButtonEnabled(f: Boolean) {
        btn_apply?.isEnabled = f
        btn_apply?.text = if (f) "Использовать этот шаблон" else "Этот шаблон используется"
    }

    private fun updateActive(item: ReceiptTemplate, position: Int) {
        repo.setActiveTemplate(item, callback = {
            if (lastActive != -1) {
                list[lastActive].active = false
                adapter.notifyItemChanged(lastActive)
            }
            item.active = true
            adapter.notifyItemChanged(position)
            toggleButtonEnabled(false)
        }, error = {
            activity?.showError(it)
        })
    }

    private fun create() {
        updatedTemplate = ReceiptTemplate()
        parentFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
            .replace(R.id.content_navigation, EditTemplateFragment())
            .addToBackStack(null).commit()
    }

    private fun edit(item: ReceiptTemplate) {
        updatedTemplate = item
        parentFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
            .replace(R.id.content_navigation, EditTemplateFragment(item.data))
            .addToBackStack(null).commit()
    }

    private fun updateList() {
        api.getReceiptTemplateList().enqueue {
            onResponse = func@{
                it ?: return@func
                list.clear()
                list.addAll(it)
                btn_edit?.isEnabled = list.isNotEmpty()
                btn_apply?.isEnabled = list.isNotEmpty()
                adapter.notifyDataSetChanged()
            }
            onError = {
                activity?.showError(it)
            }
        }
    }

    fun saveTemplate(data: ReceiptTemplateData) {
        updatedTemplate.data = data
        api.saveReceiptTemplate(updatedTemplate).enqueue {
            onResponse = {
                updateList()
            }
            onError = {
                activity?.showError(it)
            }
        }
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

    inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.item_image))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(list[position])

        override fun getItemCount() = list.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: ReceiptTemplate) {
                if (item.active) {
                    lastActive = adapterPosition
                }
                val bmp = CheckBuilder(context, null).build(item.data)
                itemView.image_check?.setImageBitmap(bmp)
            }

        }
    }

}