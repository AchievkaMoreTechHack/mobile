package com.achievka.moretech3.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import com.achievka.moretech3.extentions.dpToPx

class GameDialog @JvmOverloads constructor(context: Context?,
                 attrs: AttributeSet? = null,
                 defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {


    companion object{
        private const val TAG = "GameDialog"
        private const val DEFAULT_WIDTH = 40
        private const val DEFAULT_HEIGHT = 40
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e(TAG, "onAttachedToWindow: ", )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(TAG, "onMeasure: $widthMeasureSpec $heightMeasureSpec" )

        val initWidth = resolveDefaultSize(widthMeasureSpec, DEFAULT_WIDTH)
        val initHeight = resolveDefaultSize(widthMeasureSpec, DEFAULT_HEIGHT)
        setMeasuredDimension(initWidth, initHeight)
        Log.e(TAG, "onMeasure after set size: $widthMeasureSpec $heightMeasureSpec" )

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.e(TAG, "onLayout: ", )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.e(TAG, "onDraw: ", )
    }

    fun resolveDefaultSize(spec: Int, defaultSize: Int): Int{
        return when(MeasureSpec.getMode(spec)){
            MeasureSpec.UNSPECIFIED -> context.dpToPx(defaultSize).toInt()
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

}