package me.linshen.testchart.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by linshen on 16-10-8.
 */

public class CanvasView extends View {

    private Paint mPaint = new Paint();
    private RectF mRectf;
    private int d = 0;

    public CanvasView(Context context) {
        this(context, null);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mRectf = new RectF(0, 0, 500, 500);
        setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        d = nw > nh ? nh : nw;
        float left = d* 0.2f;
        float right = d* 0.8f;
        mRectf = new RectF(left, left, right, right);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRectf, 0, 90, true, mPaint);
    }
}
