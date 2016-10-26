package me.linshen.testchart.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import me.linshen.testchart.R;

/**
 * Created by linshen on 16-10-8.
 */

public class CanvasView extends View {

    private static final String TAG = "CanvasView";

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF mRectf = new RectF(0, 0, 0, 0);
    private int d = 0;

    public CanvasView(Context context) {
        this(context, null);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setColor(Color.RED);
//        mPaint.setStrokeWidth(context.getResources().getDimensionPixelOffset(R.dimen.stroke_width));
//        mPaint.setStrokeCap(Paint.Cap.BUTT);
//        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShadowLayer(4.46f, 2f, 16.64f, Color.YELLOW);

        mPaint2.setColor(Color.YELLOW);
        mPaint2.setStrokeWidth(context.getResources().getDimensionPixelOffset(R.dimen.stroke_width) - 15);
        mPaint2.setStrokeCap(Paint.Cap.BUTT);
        mPaint2.setStyle(Paint.Style.STROKE);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x = MeasureSpec.getSize(widthMeasureSpec);
        int y = MeasureSpec.getSize(heightMeasureSpec);
        int progressSize = 517;
        int paddingX = (x - progressSize) / 2;
        int paddingY = (y - progressSize) / 2;
        mRectf.set(paddingX, paddingY, x - paddingX, y - paddingY);
//        mRoundCenterY = mRoundOval.centerY();
//        mRoundCenterX = mRoundOval.centerX();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout() called with: changed = [" + changed + "], left = [" + left + "], top = [" + top + "], right = [" + right + "], bottom = [" + bottom + "]");
    }

    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        Log.d(TAG, "onSizeChanged() called with: nw = [" + nw + "], nh = [" + nh + "], ow = [" + ow + "], oh = [" + oh + "]");
        d = nw > nh ? nh : nw;
//        mRectf = new RectF(0, 0, d, d);
//        mPaint.setShadowLayer(6.67f, 4.67f, 4.67f, Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw() called with: canvas = [" + canvas + "]");
        canvas.drawArc(mRectf, 0, 90, true, mPaint);
//        canvas.drawArc(mRectf, 90, 120, false, mPaint2);
    }
}
