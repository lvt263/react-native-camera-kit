package com.rncamerakit.barcode

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.annotation.ColorInt
import com.rncamerakit.R


class BarcodeFrame(context: Context) : View(context) {
    private var borderPaint: Paint = Paint()
    private var laserPaint: Paint = Paint()
    var frameRect: Rect = Rect()

    private var frameWidth = 0
    private var frameHeight = 0
    private var borderMargin = 0
    private var previousFrameTime = System.currentTimeMillis()
    private var laserY = 0

    private fun init(context: Context) {
        borderPaint = Paint()
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = STROKE_WIDTH.toFloat()
        laserPaint.style = Paint.Style.STROKE
        laserPaint.strokeWidth = STROKE_WIDTH.toFloat()
        borderMargin = context.resources.getDimensionPixelSize(R.dimen.border_length)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        frameWidth = measuredWidth
        frameHeight = measuredHeight
        val marginWidth = width / WIDTH_SCALE
        val marginHeight = (height / HEIGHT_SCALE).toInt()
        frameRect.left = marginWidth
        frameRect.right = width - marginWidth
        frameRect.top = marginHeight
        frameRect.bottom = height - marginHeight
    }

    override fun onDraw(canvas: Canvas) {
        val timeElapsed = System.currentTimeMillis() - previousFrameTime
        super.onDraw(canvas)
        drawBorder(canvas)
        drawLaser(canvas, timeElapsed)
        previousFrameTime = System.currentTimeMillis()
        this.invalidate(frameRect)
    }

    private fun drawBorder(canvas: Canvas) {
        // set up some constants
        // set up some constants
        val w = canvas.width
        val h = canvas.height
        // same constants as above except innerRectFillColor is not used. Instead:
        // same constants as above except innerRectFillColor is not used. Instead:
        val outerFillColor = 0x77000000

        // first create an off-screen bitmap and its canvas

        // first create an off-screen bitmap and its canvas
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val auxCanvas = Canvas(bitmap)

        // then fill the bitmap with the desired outside color

        // then fill the bitmap with the desired outside color
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = outerFillColor
        paint.style = Paint.Style.FILL
        auxCanvas.drawPaint(paint)

        // then punch a transparent hole in the shape of the rect
        val rect = RectF(frameRect)
        // then punch a transparent hole in the shape of the rect
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        auxCanvas.drawRoundRect(
            rect, 0f,
            0f, paint
        )

        // then draw the white rect border (being sure to get rid of the xfer mode!)

        // then draw the white rect border (being sure to get rid of the xfer mode!)
        paint.xfermode = null
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        auxCanvas.drawRoundRect(
            rect, 0f,
            0f, paint
        )

        // finally, draw the whole thing to the original canvas

        // finally, draw the whole thing to the original canvas
        canvas.drawBitmap(bitmap, 0f, 0f , paint)
        canvas.drawLine(frameRect.left.toFloat(), frameRect.top.toFloat(), frameRect.left.toFloat(), (frameRect.top + borderMargin).toFloat(), borderPaint)
        canvas.drawLine(frameRect.left.toFloat(), frameRect.top.toFloat(), (frameRect.left + borderMargin).toFloat(), frameRect.top.toFloat(), borderPaint)
        canvas.drawLine(frameRect.left.toFloat(), frameRect.bottom.toFloat(), frameRect.left.toFloat(), (frameRect.bottom - borderMargin).toFloat(), borderPaint)
        canvas.drawLine(frameRect.left.toFloat(), frameRect.bottom.toFloat(), (frameRect.left + borderMargin).toFloat(), frameRect.bottom.toFloat(), borderPaint)
        canvas.drawLine(frameRect.right.toFloat(), frameRect.top.toFloat(), (frameRect.right - borderMargin).toFloat(), frameRect.top.toFloat(), borderPaint)
        canvas.drawLine(frameRect.right.toFloat(), frameRect.top.toFloat(), frameRect.right.toFloat(), (frameRect.top + borderMargin).toFloat(), borderPaint)
        canvas.drawLine(frameRect.right.toFloat(), frameRect.bottom.toFloat(), frameRect.right.toFloat(), (frameRect.bottom - borderMargin).toFloat(), borderPaint)
        canvas.drawLine(frameRect.right.toFloat(), frameRect.bottom.toFloat(), (frameRect.right - borderMargin).toFloat(), frameRect.bottom.toFloat(), borderPaint)
    }

    private fun drawLaser(canvas: Canvas, timeElapsed: Long) {
        if (laserY > frameRect.bottom || laserY < frameRect.top) laserY = frameRect.top
        canvas.drawLine((frameRect.left + STROKE_WIDTH).toFloat(), laserY.toFloat(), (frameRect.right - STROKE_WIDTH).toFloat(), laserY.toFloat(), laserPaint)
        laserY += (timeElapsed / ANIMATION_SPEED).toInt()
    }

    fun setFrameColor(@ColorInt borderColor: Int) {
        borderPaint.color = borderColor
    }

    fun setLaserColor(@ColorInt laserColor: Int) {
        laserPaint.color = laserColor
    }

    companion object {
        private const val STROKE_WIDTH = 5
        private const val ANIMATION_SPEED = 8
        private const val WIDTH_SCALE = 7
        private const val HEIGHT_SCALE = 2.75
    }

    init {
        init(context)
    }
}
