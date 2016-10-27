package me.linshen.testchart.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by linshen on 16-10-8.
 */

public class CanvasView extends View {

    private static final String TAG = "CanvasView";

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF mRectf = null;
    private RectF mRectf2 = null;
    private int d = 0;
    private float mSweap;
    private float mSweap2;
    private float mRotate;

    public CanvasView(Context context) {
        this(context, null);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setColor(Color.RED);
        mTextPaint.setTextSize(40);
        mTextPaint.setColor(Color.RED);
        mPaint2.setColor(Color.YELLOW);
//        mPaint.setStrokeWidth(context.getResources().getDimensionPixelOffset(R.dimen.stroke_width));
//        mPaint.setStrokeCap(Paint.Cap.BUTT);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setShadowLayer(4.46f, 2f, 16.64f, Color.YELLOW);

//        mPaint2.setColor(Color.YELLOW);
//        mPaint2.setStrokeWidth(context.getResources().getDimensionPixelOffset(R.dimen.stroke_width) - 15);
//        mPaint2.setStrokeCap(Paint.Cap.BUTT);
//        mPaint2.setStyle(Paint.Style.STROKE);


    }

    public void startAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 90f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweap = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });


        /*ValueAnimator rotateAnimator = ValueAnimator.ofFloat(0, 90f);
        rotateAnimator.setDuration(2000);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRotate = (float) animation.getAnimatedValue();
            }
        });
        rotateAnimator.start();*/

        animate().rotation(90).setDuration(2000).start();


        animator.start();
    }

    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        Log.d(TAG, "onSizeChanged() called with: nw = [" + nw + "], nh = [" + nh + "], ow = [" + ow + "], oh = [" + oh + "]");
        d = nw > nh ? nh : nw;
        mRectf = new RectF(0, 0, d, d);
        mRectf2 = new RectF(0 + 20, 0 + 20, d - 20, d - 20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRectf, 0, mSweap, true, mPaint);
        canvas.drawArc(mRectf2, 90, mSweap, true, mPaint2);
//        canvas.rotate(-mRotate);
        canvas.save();
        canvas.rotate(-mSweap, d / 2, d / 2);
        Log.d(TAG, "onDraw() called with: mSweap = [" + mSweap + "]");
        canvas.drawText(TAG, d / 2 - mTextPaint.measureText(TAG) / 2,
                d / 2, mTextPaint);
        canvas.restore();
//        canvas.restore();
//        canvas.drawArc(mRectf, 90, 120, false, mPaint2);
    }
}
