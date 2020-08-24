package ru.henridellal.dialer

import android.widget.RelativeLayout
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.app.Activity
import ru.henridellal.dialer.R
import android.content.res.TypedArray
import android.view.View
import android.widget.TextView

class NumpadButton : RelativeLayout {
    private var mChar: String? = null
    private var mLetters: String? = null

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        initButton(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initButton(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initButton(attrs)
    }

    private fun initButton(attrs: AttributeSet) {
        val inflater: LayoutInflater
        inflater = if (context is Activity) {
            (context as Activity).layoutInflater
        } else {
            LayoutInflater.from(context)
        }
        inflater.inflate(R.layout.numpad_button, this, true)
        val arr = context.obtainStyledAttributes(attrs, R.styleable.NumpadButton, 0, 0)
        try {
            mChar = arr.getString(R.styleable.NumpadButton_number)
            mLetters = arr.getString(R.styleable.NumpadButton_letters)
        } finally {
            arr.recycle()
        }
        (findViewById<View>(R.id.numpad_number) as TextView).text = mChar
        (findViewById<View>(R.id.numpad_letters) as TextView).text = mLetters
    }

    fun setLetters(letters: String?) {
        mLetters = letters
        (findViewById<View>(R.id.numpad_letters) as TextView).text = mLetters
    }
}