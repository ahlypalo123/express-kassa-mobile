package com.hlypalo.express_kassa.ui.settings

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.ui.main.NavigationFragment
import com.squareup.moshi.JsonClass
import kotlinx.android.synthetic.main.fragment_receipt_layout.*
import java.io.Serializable

class ReceiptLayoutFragment : Fragment() {

    private val template: ReceiptTemplate = ReceiptTemplate(mutableListOf())
    private var draggingElement: ReceiptElement? = null
    private var updatedElementRow: Int = 0
    private var updatedElementCol: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_receipt_layout, container, false)
    }

    private fun startDragging(v: TextView) {
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

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)

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

                    template.lines.add(mutableListOf(draggingElement!!))
                    updateUi()

                    true
                }
                else -> {
                    false
                }
            }
        }

        btn_add_settings_variable?.setOnClickListener {
            val el = ReceiptElement(text = "Text")
            template.lines.add(mutableListOf(el))
            updateElement(el, col = 0, row = template.lines.size - 1)
            updateUi()
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

    fun removeUpdatedElement() {
        deleteElementByPosition(updatedElementRow, updatedElementCol)
    }

    fun updateUi() {
        layout_drop_area?.removeAllViews()

        template.lines.forEachIndexed { iRow, line ->
            val tr = TableRow(context)
            layout_drop_area?.addView(tr)

            line.forEachIndexed { iCol, el ->
                val v = getElementView(el)
                addDragListener(v, iRow, iCol)
                v.setOnClickListener {
                    updateElement(el, iCol, iRow)
                }
                tr.addView(v)
            }
        }
    }

    private fun updateElement(el: ReceiptElement, col: Int, row: Int) {
        updatedElementCol = col
        updatedElementRow = row
        TextFormatDialog(el).show(childFragmentManager, null)
    }

    private fun deleteElementByPosition(row: Int, col: Int) {
        template.lines[row].removeAt(col)
        if (template.lines[row].isEmpty()) {
            template.lines.removeAt(row)
        }
        updateUi()
    }

    private fun addDragListener(view: View, row: Int, col: Int) {
        var position = Position.BOTTOM

        view.setOnLongClickListener {
            draggingElement = template.lines[row][col]
            startDragging(view as TextView)
            deleteElementByPosition(row, col)
            true
        }

        view.setOnDragListener { v, e ->
            val parent = view.parent as? TableRow
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
                            template.lines[row].add(col, draggingElement!!)
                        }
                        Position.RIGHT -> {
                            template.lines[row].add(col + 1, draggingElement!!)
                        }
                        Position.TOP -> {
                            template.lines.add(row, mutableListOf(draggingElement!!))
                        }
                        Position.BOTTOM -> {
                            template.lines.add(row + 1, mutableListOf(draggingElement!!))
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

    private fun getElementView(el: ReceiptElement) : TextView {
        val tv = TextView(context)
        tv.layoutParams = TableRow.LayoutParams().apply {
            weight = 1F
        }
        tv.textAlignment = el.alignment

        val span = SpannableStringBuilder(el.text)
        el.style.forEach {
            when (it) {
                ReceiptStyle.BOLD -> {
                    span.setSpan(StyleSpan(Typeface.BOLD), 0, span.length, 0)
                }
                ReceiptStyle.UNDERLINED -> {
                    span.setSpan(UnderlineSpan(), 0, span.length, 0)
                }
                ReceiptStyle.ITALIC -> {
                    span.setSpan(StyleSpan(Typeface.ITALIC), 0, span.length, 0)
                }
            }
        }
        tv.text = span
        tv.textSize = 18F + (el.size.offset)
        return tv
    }

    private enum class Position {
        LEFT, RIGHT, TOP, BOTTOM
    }

    private class DragShadowBuilder(private val view: TextView?) : View.DragShadowBuilder(view) {
        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            view?.let {
                size.set(it.width, it.height)
                touch.set(it.width / 2, it.height / 2)
            }
        }

        override fun onDrawShadow(canvas: Canvas) {
            view?.draw(canvas)
        }
    }

}

@JsonClass(generateAdapter = true)
data class ReceiptTemplate(
    val lines: MutableList<MutableList<ReceiptElement>>
) : Serializable

@JsonClass(generateAdapter = true)
data class ReceiptElement(
    var text: String,
    var alignment: Int = TextView.TEXT_ALIGNMENT_CENTER,
    var style: MutableList<ReceiptStyle> = mutableListOf(),
    var size: ReceiptTextSize = ReceiptTextSize.NORMAL
) : Serializable

enum class ReceiptTextSize(val offset: Int, val nameRes: String) {
    SMALL(-4, "Мелкий"), NORMAL(0, "Обычный"), BIG(4, "Большой"), LARGE(8, "Огромный")
}

enum class ReceiptStyle {
    BOLD, UNDERLINED, ITALIC
}