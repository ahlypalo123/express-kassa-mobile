package com.hlypalo.express_kassa.ui.settings

import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.*
import com.hlypalo.express_kassa.util.*
import kotlinx.android.synthetic.main.fragment_edit_template.*
import kotlinx.android.synthetic.main.fragment_edit_template.toolbar
import kotlinx.android.synthetic.main.fragment_free_sale.*

class EditTemplateFragment(
    private var data: ReceiptTemplateData = mutableListOf(),
    private var isParent: Boolean = true
) : Fragment(), VariableDelegate {

    private var draggingElement: ReceiptElement? = null
    private var updatedElementRow: Int = 0
    private var updatedElementCol: Int = 0

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("template", Gson().toJson(data))
        outState.putBoolean("isParent", isParent)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_template, container, false)
    }

    private fun startDragging(v: View) {
        val maskShadow = DragShadowBuilder(v)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            v.startDrag(null, maskShadow, v, 0)
        } else {
            v.startDragAndDrop(null, maskShadow, v, 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.getString("template")?.let {
            Gson().fromJson<ReceiptTemplateData>(it, (object : TypeToken<ReceiptTemplateData>() {}).type)
        }
        savedInstanceState?.getBoolean("isParent")?.let {
            isParent = it
        }

        btn_add_list?.visibility = if (isParent) View.VISIBLE else View.GONE

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.title = if (isParent) "Редактирование шаблона" else "Редактирование группы элементов"
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        layout_drop_area?.layoutParams?.width = getPreviewLayoutWidth(context)

        layout_drop_area?.setOnDragListener { _, e ->
            when (e.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    layout_drop_area?.setBackgroundResource(
                        R.drawable.shape_dashed_lines_highlighted
                    )
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    layout_drop_area?.setBackgroundResource(
                        R.drawable.shape_dashed_lines
                    )
                    true
                }
                DragEvent.ACTION_DROP -> {
                    layout_drop_area?.setBackgroundResource(
                        R.drawable.shape_dashed_lines
                    )

                    data.add(mutableListOf(draggingElement!!))
                    updateUi()

                    true
                }
                else -> {
                    false
                }
            }
        }

        btn_add_text?.setOnClickListener {
            val el = ReceiptTextElement(text = "Text")
            data.add(mutableListOf(el))
            updateElement(el, col = 0, row = data.size - 1)
            updateUi()
        }

        btn_add_variable?.setOnClickListener {
            VariableDialog(this).show(childFragmentManager, null)
        }

        btn_add_list?.setOnClickListener {
            val el = ReceiptListElement(
                data = mutableListOf(),
                groupType = ReceiptElementGroupType.LIST
            )
            data.add(mutableListOf(el))
            updateElement(el, col = 0, row = data.size - 1)
            updateUi()
        }

        btn_done?.setOnClickListener {
            val caller = parentFragmentManager.getCallerFragment()
            if (caller is EditTemplateFragment) {
                caller.updateUi()
            }
            if (caller is TemplatesFragment) {
                caller.saveTemplate(data)
            }
            parentFragmentManager.popBackStack()
        }

        updateUi()
    }

    fun updateUi() {
        layout_drop_area?.removeAllViews()

        data.forEachIndexed { rowInd, row ->
            val tr = createRow(context, layout_drop_area)
            row.forEachIndexed { colInd, el ->
                val v = getElementView(el)
                addViewToRow(v, tr)
                addDragListener(v, rowInd, colInd)
                v?.setOnClickListener {
                    updateElement(el, rowInd, colInd)
                }
            }
        }
    }

    override fun onVariableSelected(variable: String) {
        val el = ReceiptTextElement(text = "{$variable}")
        data.add(mutableListOf(el))
        updateElement(el, col = 0, row = data.size - 1)
        updateUi()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun removeUpdatedElement() {
        deleteElementByPosition(updatedElementRow, updatedElementCol)
    }

    private fun getElementView(el: ReceiptElement) : View? {
        return when(el) {
            is ReceiptTextElement -> getViewForTextElement(el, context)
            is ReceiptListElement -> getViewForListElement(el)
            is ReceiptImageElement -> {
                ImageView(context)
            }
            else -> null
        }
    }

    private fun getViewForListElement(el: ReceiptListElement) : View {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_dashed_lines)
        el.data.forEach { row ->
            val tr = createRow(context, layout)
            row.forEach { el ->
                val v = getElementView(el)
                addViewToRow(v, tr)
            }
        }
        return layout
    }

    private fun updateElement(el: ReceiptElement, row: Int, col: Int) {
        updatedElementCol = col
        updatedElementRow = row
        when (el) {
            is ReceiptTextElement -> {
                TextFormatDialog(el).show(childFragmentManager, null)
            }
            is ReceiptListElement -> {
                parentFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.content_navigation, EditTemplateFragment(el.data, false))
                    .addToBackStack(null).commit()
            }
        }
    }

    private fun deleteElementByPosition(row: Int, col: Int) {
        data[row].removeAt(col)
        if (data[row].isEmpty()) {
            data.removeAt(row)
        }
        updateUi()
    }

    private fun addDragListener(view: View?, row: Int, col: Int) {
        view ?: return
        var position = Position.BOTTOM

        view.setOnLongClickListener {
            draggingElement = data[row][col]
            startDragging(view)
            deleteElementByPosition(row, col)
            true
        }

        view.setOnDragListener { v, e ->
            val parent = view.parent as? LinearLayout
            when (e.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    layout_drop_area?.setBackgroundResource(
                        R.drawable.shape_dashed_lines_highlighted
                    )
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    layout_drop_area?.setBackgroundResource(
                        R.drawable.shape_dashed_lines
                    )
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    parent?.setBackgroundResource(0)
                    v.setBackgroundResource(0)

                    val part = view.width / 100 * 20
                    when {
                        e.x < part -> {
                            position = Position.LEFT
                            view.setBackgroundResource(R.drawable.shape_border_left)
                        }
                        e.x > (view.width - part) -> {
                            position = Position.RIGHT
                            view.setBackgroundResource(R.drawable.shape_border_right)
                        }
                        e.y > view.height / 2 -> {
                            position = Position.BOTTOM
                            parent?.setBackgroundResource(R.drawable.shape_border_bottom)
                        }
                        else -> {
                            position = Position.TOP
                            parent?.setBackgroundResource(R.drawable.shape_border_top)
                        }
                    }
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    parent?.setBackgroundResource(0)
                    v.setBackgroundResource(0)
                    true
                }
                DragEvent.ACTION_DROP -> {
                    parent?.setBackgroundResource(0)
                    v.setBackgroundResource(0)

                    when (position) {
                        Position.LEFT -> {
                            data[row].add(col, draggingElement!!)
                        }
                        Position.RIGHT -> {
                            data[row].add(col + 1, draggingElement!!)
                        }
                        Position.TOP -> {
                            data.add(row, mutableListOf(draggingElement!!))
                        }
                        Position.BOTTOM -> {
                            data.add(row + 1, mutableListOf(draggingElement!!))
                        }
                    }

                    updateUi()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private enum class Position {
        LEFT, RIGHT, TOP, BOTTOM
    }

    private class DragShadowBuilder(private val v: View?) : View.DragShadowBuilder(v) {
        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            v?.let {
                size.set(it.width, it.height)
                touch.set(it.width / 2, it.height / 2)
            }
        }

        override fun onDrawShadow(canvas: Canvas) {
            v?.draw(canvas)
        }
    }

}