package com.example.classpilot.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * CUSTOM VIEW (Week 9): a circular ring that shows a percentage.
 * Green at 75% or more, red below 75%.
 */
public class RingView extends View {

    private static final int GREEN = Color.parseColor("#2E7D32");
    private static final int RED = Color.parseColor("#C62828");

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();
    private final float density;

    private float shownPercent = 0f;   // value being drawn (animated)

    public RingView(Context context) {
        this(context, null);
    }

    public RingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        float stroke = 12 * density;

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(stroke);
        trackPaint.setColor(Color.parseColor("#E0E0E0"));

        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(stroke);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setColor(GREEN);

        percentPaint.setTextAlign(Paint.Align.CENTER);
        percentPaint.setTextSize(30 * density);
        percentPaint.setFakeBoldText(true);
        percentPaint.setColor(Color.parseColor("#212121"));

        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTextSize(12 * density);
        labelPaint.setColor(Color.parseColor("#757575"));
    }

    /** Set the percentage (0 to 100). The ring animates to it. */
    public void setPercent(float percent) {
        float target = Math.max(0f, Math.min(100f, percent));
        arcPaint.setColor(target >= 75f ? GREEN : RED);

        ValueAnimator animator = ValueAnimator.ofFloat(shownPercent, target);
        animator.setDuration(700);
        animator.addUpdateListener(a -> {
            shownPercent = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) (150 * density);
        int w = resolveSize(size, widthMeasureSpec);
        int h = resolveSize(size, heightMeasureSpec);
        int s = Math.min(w, h);
        setMeasuredDimension(s, s);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float pad = arcPaint.getStrokeWidth() / 2f + 2 * density;
        oval.set(pad, pad, getWidth() - pad, getHeight() - pad);

        canvas.drawArc(oval, 0, 360, false, trackPaint);
        canvas.drawArc(oval, -90, 360f * shownPercent / 100f, false, arcPaint);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        canvas.drawText(Math.round(shownPercent) + "%", cx, cy + 8 * density, percentPaint);
        canvas.drawText("present today", cx, cy + 28 * density, labelPaint);
    }
}