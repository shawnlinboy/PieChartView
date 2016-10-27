package me.linshen.testchart.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;

import java.text.DecimalFormat;
import java.util.List;

import me.linshen.testchart.R;

/**
 * Created by linshen on 2016/10/6.
 */
public class PieChartView extends View {

    private static final String TAG = "PieChartView";

    private static final float sPieRatio = 0.86f;  //饼图占整个layout面积的比例，因为最外层要用来画文字
    private static final float sDotRadius = 6.67f; //文字小圆点的半径
    private static final int sMaxPiewCount = 4;  //最大允许几段
    private static final int sMinPercent = 5;   //最小百分比,太小了容易看不清
    private static final long sArcAnimTime = 850;
    private static final long sCenterNumAnimTime = 500;
    private static final Interpolator sArcInterpolator = PathInterpolatorCompat.create(0.3f, 0, 0.25f, 1);
    private static final Interpolator sCenterNumInterpolator = PathInterpolatorCompat.create(0.3f, 0, 0.7f, 1);

    private RectF mOutBounds;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int[] mColors = null;
    private RectF[] mRectFs = new RectF[sMaxPiewCount];  //每个扇形的位置
    private float[] mStartAngles = null;  //每个扇形的起始角度
    private float[] mSweepAngles = null;
    private float[] mPercents = null; //每个扇形对应的百分比
    private String[] mNames = null;  //每个扇形的名称
    private String mAmount;
    private int mCenterPaintAlpha;

    private int d = 0;  //直径
    private float mTextSize;
    private float mStartAngle;
    private Pair<String, String> mCenterElement;

    private ValueAnimator mAngleAnimator;
    private ValueAnimator mCenterNumAnimator;

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context ctx) {
        this(ctx, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (context == null || attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PieChartView, 0, 0);
        try {
            mTextSize = a.getDimension(R.styleable.PieChartView_android_textSize, 20f);
            mStartAngle = a.getFloat(R.styleable.PieChartView_pieStartAngle, 120f);
            checkStartAngle();
        } finally {
            a.recycle();
        }
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        d = nw > nh ? nh : nw;  //取宽高的小值作为直径

        mOutBounds = new RectF(0, 0, nw, nh);
        for (int i = 0; i < sMaxPiewCount; i++) {
            mRectFs[i] = initRectfs(i, nw, nh);
        }
    }

    public void setPieElementTextSize(float size) {
        mTextSize = size;
        invalidate();
    }

    /**
     * Set the default text size to a given unit and value.  See {@link
     * TypedValue} for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */

    public void setPieElementTextSize(int unit, float size) {
        Context c = getContext();
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        setPieElementTextSize(TypedValue.applyDimension(
                unit, size, r.getDisplayMetrics()));
    }

    public void setData(List<PieElement> pieElement, @Nullable Pair<String, String> centerElement) {
        if ((mAngleAnimator != null && mAngleAnimator.isRunning())
                || (mCenterNumAnimator != null && mCenterNumAnimator.isRunning())) {
            Log.e(TAG, "animation running, return");
            return;
        }
        mAngleAnimator = ValueAnimator.ofFloat(0, 1f);
        ValueAnimator alphaAnimator = null;
        if (centerElement != null) {
            mCenterElement = centerElement;
            float amount = Float.parseFloat(mCenterElement.first);
            mCenterNumAnimator = ValueAnimator.ofFloat(0, amount).setDuration(sCenterNumAnimTime);
            mCenterNumAnimator.setInterpolator(sCenterNumInterpolator);
            mCenterNumAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //因为是金额，所以只要保留两位小数
                    DecimalFormat df = new DecimalFormat("#.00");
                    mAmount = df.format(animation.getAnimatedValue());
                }
            });
            alphaAnimator = ValueAnimator.ofInt(0, 255).setDuration(sCenterNumAnimTime);
            alphaAnimator.setInterpolator(sCenterNumInterpolator);
            alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCenterPaintAlpha = (int) animation.getAnimatedValue();
                }
            });
        }
        if (pieElement == null || pieElement.size() == 0) {
            //TODO 数据为空
        } else {
            final int size = pieElement.size();  //little trick here
            if (size <= sMaxPiewCount) {
                //TODO 干正事
                mColors = new int[size];
                mPercents = new float[size];
                mNames = new String[size];
                mStartAngles = new float[size];
                mSweepAngles = new float[size];
                float sum = 0;
                for (int i = 0; i < size; i++) {
                    PieElement element = pieElement.get(i);
                    sum += element.amount;
                    mColors[i] = element.color;
                    mNames[i] = element.name;
                }
                float surplus = 0;  //用来补足的百分比总计
                for (int i = 0; i < size; i++) {
                    PieElement element = pieElement.get(i);
                    float percent = ((element.amount * 100.00f) / sum);
                    //如果百分比不足5%，补全
                    if (percent < sMinPercent) {
                        surplus += sMinPercent - percent;  //计算总共补全了多少百分比，稍后从最大的占比中减去
                        percent = sMinPercent;
                    }
                    mPercents[i] = percent;
                }
                if (surplus != 0) {  //如果发现上面有补全百分比的行为，否则这边不会进去
                    //先找出所有百分比里面最大的是哪个
                    float max = 0;
                    for (int i = 0; i < size; i++) {
                        float percentage = mPercents[i];
                        if (max < percentage) {
                            max = percentage;
                        }
                    }
                    //再从最大的里面减去这个差值
                    for (int i = 0; i < size; i++) {
                        float percentage = mPercents[i];
                        if (percentage == max) {
                            mPercents[i] = percentage - surplus;
                        }
                    }
                }
                float start = mStartAngle;
                final float[] destSweepAngles = new float[size];
                for (int i = 0; i < size; i++) {
                    float percentage = mPercents[i];
                    float sweep = p2d(percentage);
                    mStartAngles[i] = start;
                    destSweepAngles[i] = sweep;
                    start += sweep;
                }
                mAngleAnimator.setDuration(sArcAnimTime).setInterpolator(sArcInterpolator);
                mAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float f = (float) animation.getAnimatedValue();
                        if (f != 0) {
                            for (int i = 0; i < size; i++) {
                                mSweepAngles[i] = destSweepAngles[i] * f;
                            }
                            postInvalidate();
                        }
                    }
                });
                mAngleAnimator.start();
                if (mCenterNumAnimator != null) {
                    mCenterNumAnimator.start();
                    alphaAnimator.start();
                }
            } else {
                Log.e(TAG, "PieChartView can only contain no more than " + sMaxPiewCount + " elements");
            }
        }
    }

    @Override
    public void onDraw(Canvas c) {
        if (mPercents == null) {
            Log.e(TAG, "mPercents is null, return");
            return;
        }
        int size = mPercents.length;
        // draw arc
        for (int i = 0; i < size; i++) {
            mPaint.setColor(mColors[i]);
            c.drawArc(mRectFs[i], mStartAngles[i], mSweepAngles[i], true, mPaint);
        }

        //draw circle shade in center
        mPaint.setColor(Color.WHITE);
        c.drawCircle(mOutBounds.right / 2, mOutBounds.bottom / 2, (int) (0.59 * ((d * sPieRatio) / 2)), mPaint);

        //draw text in center
        //TODO what about a looooooong text?
        if (mCenterElement != null) {
            String line1 = String.valueOf(mAmount);
            mCenterPaint.setColor(Color.RED);
            mCenterPaint.setAlpha(mCenterPaintAlpha);
            mCenterPaint.setFakeBoldText(true);
            mCenterPaint.setTextSize(getResources().getDimension(R.dimen.pie_chart_center_text_size_line1));
            c.drawText(line1, (mOutBounds.right - mCenterPaint.measureText(line1)) / 2,
                    mOutBounds.bottom / 2, mCenterPaint);
            String line2 = mCenterElement.second;
            mCenterPaint.setColor(Color.GRAY);
            mCenterPaint.setAlpha(mCenterPaintAlpha);
            mCenterPaint.setFakeBoldText(false);
            mCenterPaint.setTextSize(getResources().getDimension(R.dimen.pie_chart_center_text_size_line2));
            c.drawText(line2, (mOutBounds.right - mCenterPaint.measureText(line2)) / 2, mOutBounds.bottom / 2 - mPaint.ascent() + mPaint.descent()
                    + getResources().getDimensionPixelOffset(R.dimen.pie_chart_center_text_margin), mCenterPaint);
        }

        //draw text and dot
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(mTextSize);
        double realAngle = 0; // Radian angle
        for (int i = 0; i < size; i++) {
            realAngle = (mStartAngles[i] + mSweepAngles[i] / 2) * Math.PI / 180;
            int x = (int) (mOutBounds.right / 2 + (((mOutBounds.right / 2) * 0.8f) * Math.cos(realAngle)));
            int y = (int) (mOutBounds.bottom / 2 + (((mOutBounds.bottom / 2) * 0.8f) * Math.sin(realAngle)));
            String text = mNames[i];
            if (x < (d * sPieRatio) / 2) { //在左半边的，需要先画点再画文字
                mPaint.setColor(mColors[i]);
                c.drawCircle(x, y, sDotRadius, mPaint);
                mPaint.setColor(Color.GRAY);
                mPaint.setFakeBoldText(true);
                c.drawText(text, x - mPaint.measureText(text) / 2,
                        y - mPaint.ascent() + mPaint.descent() + sDotRadius, mPaint);
            } else {  //在右边的，先画文字再画点
                mPaint.setColor(Color.GRAY);
                mPaint.setFakeBoldText(true);
                c.drawText(text, x - mPaint.measureText(text) / 2, y + mPaint.ascent(), mPaint);
                mPaint.setColor(mColors[i]);
                c.drawCircle(x, y, sDotRadius, mPaint);
            }
        }
    }

    public void startAnim() {

    }

    /**
     * 每一段的半径需要比上一段小，按照视觉要求，每次减小的值不一样
     *
     * @param i
     */
    private RectF initRectfs(int i, int nw, int nh) {
        RectF rec;
        if (i == 0) {
            int offsetX = 0;
            int offsetY = 0;
            if (nw > nh) {
                offsetX = (nw - nh) / 2;
            } else {
                offsetY = (nh - nw) / 2;
            }
            rec = new RectF((1 - sPieRatio) * d + offsetX, (1 - sPieRatio) * d + offsetY,
                    sPieRatio * d + offsetX, sPieRatio * d + offsetY);
        } else {
            int minus = 8;  //默认是8
            if (i == 1) {
                minus = 12;
            } else if (i == 2) {
                minus = 10;
            } else if (i == 3) {
                minus = 8;
            }
            rec = new RectF(mRectFs[i - 1]);
            rec.left += minus;
            rec.top += minus;
            rec.right -= minus;
            rec.bottom -= minus;
        }
        return rec;
    }

    /**
     * 将百分比转化成360°的占比
     *
     * @param percentage
     * @return
     */
    private float p2d(float percentage) {
        if (percentage < 0) {
            percentage = 0.00f;
        } else if (percentage > 100) {
            percentage = 100.00f;
        }
        return (percentage * 360) / 100.00f;
    }

    private void checkStartAngle() {
        if (mStartAngle < 0) {
            mStartAngle = 0;
        } else if (mStartAngle > 360) {
            mStartAngle = 360;
        }
    }

    public static class PieElement {
        private String name;
        private float amount;
        private int color;

        public PieElement(String name, float amount, int color) {
            this.name = name;
            this.amount = amount;
            this.color = color;
        }

        @Override
        public String toString() {
            return "PieElement{" +
                    "name='" + name + '\'' +
                    ", amount=" + amount +
                    ", color=" + color +
                    '}';
        }
    }

}