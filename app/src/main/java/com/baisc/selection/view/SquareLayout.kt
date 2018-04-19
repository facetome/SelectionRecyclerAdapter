package com.baisc.selection.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * Created by basic on 2018/4/18.
 */
open class SquareLayout : LinearLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr:Int) : super(context, attributes, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var height = measuredWidth
        var heightMeasure = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasure)
    }
}