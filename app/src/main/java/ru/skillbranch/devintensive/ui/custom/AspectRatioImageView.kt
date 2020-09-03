package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import ru.skillbranch.devintensive.R

class AspectRatioImageView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): AppCompatImageView(context, attrs, defStyleAttrs){

    companion object {
        private const val DEFAULT_ASPECT_RATIO = 1.78f
    }

    private var aspectRatio = DEFAULT_ASPECT_RATIO

    init {
        if (attrs != null) {
            val a:TypedArray = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView)
            aspectRatio = a.getFloat(R.styleable.AspectRatioImageView_aspectRatio, DEFAULT_ASPECT_RATIO)
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val newHeight = (measuredWidth/aspectRatio).toInt()
        setMeasuredDimension(measuredWidth, newHeight)
    }

}
