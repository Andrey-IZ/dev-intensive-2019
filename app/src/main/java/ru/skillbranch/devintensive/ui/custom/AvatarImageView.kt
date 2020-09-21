package ru.example.myapplication.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.animation.doOnRepeat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx
import kotlin.math.max
import kotlin.math.truncate

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyledAttr: Int = 0
): AppCompatImageView(context, attrs,defStyledAttr) {

    companion object {
        private const val DEFAULT_SIZE: Int = 40
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE

        val bgColors = arrayOf(
            Color.parseColor("#7BC862"),
            Color.parseColor("#E17076"),
            Color.parseColor("#FAA774"),
            Color.parseColor("#6EC9CB"),
            Color.parseColor("#65AADD"),
            Color.parseColor("#EE7AAE"),
            Color.parseColor("#2196F3"),
        )
    }

    @Px
    var borderWidth = context.dpToPx(DEFAULT_BORDER_WIDTH)

    @ColorInt
    private var borderColor = DEFAULT_BORDER_COLOR

    private var initials: String = "??"
    private val avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val initialsPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private val borderRect = Rect()
    private var size: Int = 0

    private var isAvatarMode = true

    init {
        if ( attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView)
            borderWidth = ta.getDimension(
                R.styleable.AvatarImageView_aiv_borderWidth,
                context.dpToPx(DEFAULT_BORDER_WIDTH)
            )
            borderColor = ta.getColor(R.styleable.AvatarImageView_aiv_borderColor, DEFAULT_BORDER_COLOR)
            initials = ta.getString(R.styleable.AvatarImageView_aiv_initials)?: "??"
            ta.recycle()
        }
        scaleType = ScaleType.CENTER_CROP
        setup()
        setOnLongClickListener {
            handleLongClick()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(max(initSize, size), max(initSize, size))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w==0) return

        with(viewRect) {
            top = 0
            left = 0
            right = w
            bottom = h
        }
        prepareShader(w, h)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
        // Not allocate, only DRAW

        if (drawable != null && isAvatarMode) {
            drawAvatar(canvas)
        } else {
            drawInitials(canvas)
        }

        val half = (borderWidth/2).toInt()
        borderRect.set(viewRect)
        borderRect.inset(half, half)
        canvas.drawOval(borderRect.toRectF(), borderPaint)
    }

    override fun onSaveInstanceState(): Parcelable? {
        Log.d("M_AvatarImageView", "onSaveInstanceState $id")
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isAvatarMode = isAvatarMode
        savedState.borderWidth = borderWidth
        savedState.borderColor = borderColor
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state)
            isAvatarMode = state.isAvatarMode
            borderWidth = state.borderWidth
            borderColor = state.borderColor

            with(borderPaint) {
                color = borderColor
                strokeWidth = borderWidth
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (isAvatarMode) prepareShader(width, height)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        if (isAvatarMode) prepareShader(width, height)
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        if (isAvatarMode) prepareShader(width, height)
    }

    fun setInitials(initials: String) {
        this.initials = initials
        if (!isAvatarMode) {
            invalidate()
        }
    }

    fun setBorderColor(@ColorInt color: Int) {
        borderColor = color
        borderPaint.color = borderColor
        invalidate()
    }

    fun setBorderWidth(@Dimension width: Int) {
        borderWidth = context.dpToPx(width)
        borderPaint.strokeWidth = borderWidth
        invalidate()
    }

    private fun prepareShader(w: Int, h: Int) {
        // prepare buffer here
        if (w == 0 || drawable == null) return
        val srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    private fun setup() {
        with(avatarPaint) {
            color = Color.RED
            style = Paint.Style.FILL
        }

        with(borderPaint) {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
        }
    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> context.dpToPx(DEFAULT_SIZE).toInt()
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

    private fun drawAvatar(canvas: Canvas) {
        canvas.drawOval(viewRect.toRectF(), avatarPaint)
    }

    private fun drawInitials(canvas: Canvas) {
        initialsPaint.color = initialsToColor(initials)
        canvas.drawOval(viewRect.toRectF(), initialsPaint)
        with (initialsPaint) {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = height * 0.33f
        }
        val offsetY = (initialsPaint.descent() + initialsPaint.ascent())/2
        canvas.drawText(initials, viewRect.exactCenterX(), viewRect.exactCenterY() - offsetY, initialsPaint)
    }

    private fun initialsToColor(letters:String): Int {
        if (letters.isEmpty()) return bgColors[0]

        val b = letters[0].toByte()
        val len = bgColors.size
        val d = b/len.toDouble()
        val index = ((d - truncate(d))* len).toInt()
        return bgColors[index]
    }

    private fun handleLongClick(): Boolean {
        val va = ValueAnimator.ofInt(width, width*2).apply {
            duration = 600
            interpolator = LinearInterpolator()
            repeatMode = ValueAnimator.REVERSE
            repeatCount = 1
            addUpdateListener {
                size = it.animatedValue as Int
                requestLayout()
            }
        }
        va.doOnRepeat { toggleMode() }
        va.start()

        return true
    }

    private fun toggleMode() {
        isAvatarMode = !isAvatarMode
        invalidate()
    }

    private class SavedState : BaseSavedState {
        var isAvatarMode = true
        var borderWidth = 0f
        var borderColor = 0

        constructor(superState: Parcelable?): super(superState)

        constructor(src: Parcel) : super(src) {
            // restore the state from parcel
            isAvatarMode = src.readInt() == 1
            borderWidth = src.readFloat()
            borderColor = src.readInt()
        }

        override fun writeToParcel(dst: Parcel, flags: Int) {
            //write the state to parcel
            super.writeToParcel(dst, flags)
            dst.writeInt(if (isAvatarMode) 1 else 0)
            dst.writeFloat(borderWidth)
            dst.writeInt(borderColor)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}
