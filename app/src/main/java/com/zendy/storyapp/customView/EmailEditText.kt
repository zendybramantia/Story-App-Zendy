package com.zendy.storyapp.customView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.zendy.storyapp.R

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.email)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        inputType = 0x00000021

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                error =
                    if (email.matches(emailPattern.toRegex()) && s.isNotEmpty()) null else context.getString(
                        R.string.invalid_email
                    )

            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    companion object {
        const val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    }
}