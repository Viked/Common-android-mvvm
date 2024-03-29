package com.viked.commonandroidmvvm.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import java.util.*
import kotlin.math.min


class AvatarDrawable(private val text: String) : Drawable() {

    private val defaultPlaceholderColor = "#3F51B5"
    private val defaultTextSizePercentage = 33f

    private val initials = text.split(' ')
        .filter { it.isNotEmpty() }
        .take(2)
        .map { it[0].toString() }.let {
            if (it.isEmpty())
                ""
            else
                it.reduce { acc, s -> acc + s }.uppercase(Locale.getDefault())
        }


    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        typeface = Typeface.SANS_SERIF
    }
    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = convertStringToColor(text)
    }

    private var textStartXPoint: Float = 0.toFloat()
    private var textStartYPoint: Float = 0.toFloat()

    private var radius: Float = 0.toFloat()
    private var circleX: Float = 0.toFloat()
    private var circleY: Float = 0.toFloat()

    override fun draw(canvas: Canvas) {
        if (radius == 0f) {
            val bounds = canvas.clipBounds
            setAvatarCircleValues(bounds.width(), bounds.height())
            setAvatarTextValues()
        }

        canvas.drawCircle(circleX, circleY, radius, backgroundPaint)
        canvas.drawText(initials, textStartXPoint, textStartYPoint, textPaint)
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
        backgroundPaint.alpha = alpha
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // Ignore color filter for avatar
    }

    private fun setAvatarTextValues() {
        textPaint.textSize = calculateTextSize()
        textStartXPoint = calculateTextStartXPoint()
        textStartYPoint = calculateTextStartYPoint()
    }

    private fun setAvatarCircleValues(width: Int, height: Int) {
        radius = min(width, height).toFloat() / 2f
        circleX = (width / 2).toFloat()
        circleY = (height / 2).toFloat()
    }

    private fun calculateTextSize(): Float {
        return bounds.height() * defaultTextSizePercentage / 100
    }

    private fun calculateTextStartXPoint(): Float {
        val stringWidth = textPaint.measureText(initials)
        return bounds.width() / 2f - stringWidth / 2f
    }

    private fun calculateTextStartYPoint(): Float {
        return bounds.height() / 2f - (textPaint.ascent() + textPaint.descent()) / 2f
    }

    private fun convertStringToColor(text: String): Int {
        if (text.isEmpty()) return Color.parseColor(defaultPlaceholderColor)
        val hash = 0xFFFF and text.hashCode()
        val hue = 360.0f * hash.toFloat() / (1 shl 15) % 360.0f
        return Color.HSVToColor(floatArrayOf(hue, 0.4f, 0.90f))
    }
}