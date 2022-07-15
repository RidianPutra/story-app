package com.ridianputra.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import com.ridianputra.storyapp.R

class EmailEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val scale: Float = resources.displayMetrics.density
        val paddingDp = 8.0f
        val paddingPixel = (paddingDp * scale + 0.5f).toInt()
        compoundDrawablePadding = paddingPixel
        updatePadding(left = paddingPixel, right = paddingPixel)

        hint = resources.getString(R.string.email)
    }

    private fun init() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        val emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable
        setDrawable(emailIcon, "navy")
        setBackground("navy")

        doAfterTextChanged {
            when {
                text.isNullOrBlank() -> {
                    setDrawable(emailIcon, "red")
                    setBackground("red")
                    error = resources.getString(R.string.empty_email)
                }
                !Patterns.EMAIL_ADDRESS.matcher(text).matches() -> {
                    setDrawable(emailIcon, "red")
                    setBackground("red")
                    error = resources.getString(R.string.invalid_email)
                }
                else -> {
                    setDrawable(emailIcon, "green")
                    setBackground("green")
                    error = null
                }
            }
        }
    }

    private fun setDrawable(icon: Drawable, color: String) {
        val wrapIcon = DrawableCompat.wrap(icon)
        when (color) {
            "navy" -> DrawableCompat.setTint(
                wrapIcon,
                ContextCompat.getColor(context, R.color.navy)
            )
            "red" -> DrawableCompat.setTint(
                wrapIcon,
                ContextCompat.getColor(context, R.color.red)
            )
            "green" -> DrawableCompat.setTint(
                wrapIcon,
                ContextCompat.getColor(context, R.color.green)
            )
        }
        DrawableCompat.setTintMode(wrapIcon, PorterDuff.Mode.SRC_IN)
        setCompoundDrawablesWithIntrinsicBounds(wrapIcon, null, null, null)
    }

    private fun setBackground(color: String) {
        when (color) {
            "navy" -> background =
                ContextCompat.getDrawable(context, R.drawable.bg_edit_text)
            "red" -> background =
                ContextCompat.getDrawable(context, R.drawable.bg_edit_text_error)
            "green" -> background =
                ContextCompat.getDrawable(context, R.drawable.bg_edit_text_accepted)
        }
    }
}