package com.kyawhtut.firebasephoneauthlib.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import com.kyawhtut.firebasephoneauthlib.R

class SuccessView : View {

    private var density = -1f
    private lateinit var paintOne: Paint
    private lateinit var paintTwo: Paint

    private var minWidth = 0f
    private var minHeight = 0f
    private var angle = -90f
    private var startAngle = -90f

    private val constRadius = dip2px(1.2f)
    private val constRectWeight = dip2px(3f)
    private val constLeftRectW = dip2px(15f)
    private val constRightRectW = dip2px(25f)

    private var leftRectWidth = 0f
    private var rightRectWidth = 0f

    private var isStart = false
    private var start: () -> Unit = {}
    private var end: () -> Unit = {}

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs!!)
    }

    private fun init(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SuccessView)
        val pColor: Int
        val strokeWidth: Float
        try {
            pColor = typedArray.getColor(R.styleable.SuccessView_svStrokeColor, 0xA5DC86)
            strokeWidth = typedArray.getFloat(R.styleable.SuccessView_svStrokeWidth, 2.5f)
        } finally {
            typedArray.recycle()
        }

        minWidth = dip2px(50f)
        minHeight = dip2px(50f)
        paintOne = Paint().apply {
            color = pColor
            style = Paint.Style.FILL_AND_STROKE
            this.strokeWidth = 0.8f
            isAntiAlias = true
        }

        paintTwo = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            this.strokeWidth = dip2px(strokeWidth)
            color = pColor
        }
    }

    override fun onDraw(canvas: Canvas) {
        val bounds = canvas.clipBounds
        val left: Float
        val right: Float
        val top: Float
        val bottom: Float
        when {
            bounds.width() > bounds.height() -> {
                val distance = (bounds.width() / 2 - bounds.height() / 2).toFloat()
                left = bounds.left + distance
                right = bounds.right - distance
                top = bounds.top.toFloat()
                bottom = bounds.bottom.toFloat()
            }
            bounds.width() < bounds.height() -> {
                val distance = (bounds.height() / 2 - bounds.width() / 2).toFloat()
                top = bounds.top + distance
                bottom = bounds.bottom - distance
                left = bounds.left.toFloat()
                right = bounds.right.toFloat()
            }
            else -> {
                left = bounds.left.toFloat()
                right = bounds.right.toFloat()
                top = bounds.top.toFloat()
                bottom = bounds.bottom.toFloat()
            }
        }
        val oval = RectF(left + dip2px(2f), top + dip2px(2f), right - dip2px(2f), bottom - dip2px(2f))
        if (isStart)
            canvas.drawArc(oval, startAngle, angle, false, paintTwo)
        var totalW = width.toFloat()
        var totalH = height.toFloat()

        canvas.rotate(45f, totalW / 2, totalH / 2)

        totalW /= 1.2f
        totalH /= 1.2f
        val leftRect = RectF()
        if (leftRectWidth > 0) {
            leftRect.left = (totalW - constLeftRectW) / 2 + constRectWeight
            leftRect.right = leftRect.left + dip2px(leftRectWidth)
            leftRect.top = (totalH + constRightRectW) / 2
            leftRect.bottom = leftRect.top + constRectWeight
            canvas.drawRoundRect(leftRect, constRadius, constRadius, paintOne)
        }
        if (rightRectWidth > 0) {
            val rightRect = RectF()
            rightRect.bottom = (totalH + constRightRectW) / 2 + constRectWeight
            rightRect.left = (totalH + constLeftRectW) / 2
            rightRect.right = rightRect.left + constRectWeight
            rightRect.top = rightRect.bottom - dip2px(rightRectWidth)
            canvas.drawRoundRect(rightRect, constRadius, constRadius, paintOne)
        }
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            (paddingLeft + minWidth + paddingRight).toInt()
        }
        val height = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            (paddingTop + minHeight + paddingBottom).toInt()
        }
        setMeasuredDimension(width, height)
    }

    fun listener(start: () -> Unit = {}, end: () -> Unit = {}) {
        this.start = start
        this.end = end
    }

    fun startAnim(startDelay: Long, duration: Long = 1000) {
        clearAnimation()
        val animator = ValueAnimator.ofFloat(0f, 60f, 120f, 180f, 240f, 300f, 360f, 375f, 400f)
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            angle = -value
            if (value > 360 && value <= 375) {
                leftRectWidth = value - 360
            } else if (value > 375) {
                rightRectWidth = value - 375
            }
            isStart = true
            invalidate()
        }
        animator.duration = duration
        animator.interpolator = LinearInterpolator()
        animator.startDelay = startDelay
        animator.start()
        animator.addListener(
                onStart = {
                    start()
                },
                onEnd = {
                    end()
                }
        )
    }

    private fun dip2px(dpValue: Float): Float {
        if (density == -1f) {
            density = resources.displayMetrics.density
        }
        return dpValue * density + 0.5f
    }

}