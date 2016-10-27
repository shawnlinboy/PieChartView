package me.linshen.testchart.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

import me.linshen.testchart.R;

/**
 * Created by linshen on 2016/10/6.
 */
public class PieChartView extends View {

    private static final String TAG = "PieChartView";

    private static final float sPieRatio = 0.86f;  //饼图占整个layout面积的比例，因为最外层要用来画文字
    private static final float sDotRadius = 6.67f; //文字远点的半径
    private static final int sMaxPiewCount = 4;  //最大允许几段
    private static final int sMinPercent = 5;   //最小百分比

    private RectF mArcBounds;
    private RectF mOutBounds;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String[][] mData;
    private int[] mColors = null;
    private RectF[] mRectFs;

    private int d = 0;
    private float mTextSize;
    private int mStartAngle;
    private Pair<String, String> mCenterElement;

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
            mStartAngle = a.getInt(R.styleable.PieChartView_pieStartAngle, 90);
            checkStartAngle();
        } finally {
            a.recycle();
        }
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        d = nw > nh ? nh : nw;

        int offsetX = 0;
        int offsetY = 0;
        if (nw > nh) {
            offsetX = (nw - nh) / 2;
        } else {
            offsetY = (nh - nw) / 2;
        }
        mOutBounds = new RectF(0, 0, nw, nh);
        mArcBounds = new RectF((1 - sPieRatio) * d + offsetX, (1 - sPieRatio) * d + offsetY,
                sPieRatio * d + offsetX, sPieRatio * d + offsetY);
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
        mCenterElement = centerElement;
        if (pieElement == null || pieElement.size() == 0) {
            //TODO 数据为空
        } else if (pieElement.size() <= sMaxPiewCount) {
            //TODO 干正事
            int size = pieElement.size();  //把 size 拿出来避免每次都去取
            mColors = new int[size];
            mRectFs = new RectF[size];
            for (int i = 0; i < size; i++) {
                if (i == 0) {
                    mRectFs[i] = new RectF(mArcBounds);
                } else {
                    mRectFs[i] = updateArcRect(mRectFs[i - 1], i);
                }
            }
            int ci = 0;
            float sum = 0;
            for (PieElement element : pieElement) {
                sum += element.amount;
                mColors[ci++] = element.color;
            }
            mData = new String[size][2];
            int di = 0;
            float completeSum = 0;
            for (int i = 0; i < size; i++) {
                PieElement element = pieElement.get(i);
                float percent = ((element.amount * 100.00f) / sum);
                //如果百分比不足5%，补全
                if (percent < sMinPercent) {
                    completeSum += sMinPercent - percent;
                    percent = sMinPercent;
                }
                mData[di++] = new String[]{element.name + "", percent + ""};
            }
            //如果发现上面有补全百分比的行为，把差值在最大的那个上面减去
            if (completeSum != 0) {
                //先找出所有百分比里面最大的是哪个
                float max = 0;
                for (int i = 0; i < mData.length; i++) {
                    float percentage = Float.parseFloat(mData[i][1]);
                    if (max < percentage) {
                        max = percentage;
                    }
                }
                //再把最大的百分比减去刚才的差值
                for (int i = 0; i < mData.length; i++) {
                    float percentage = Float.parseFloat(mData[i][1]);
                    if (percentage == max) {
                        mData[i][1] = String.valueOf(percentage - completeSum);
                        return;
                    }
                }
            }
            postInvalidate();
        } else {
            Log.e(TAG, "PieChartView can only contain no more than " + sMaxPiewCount + " elements");
        }
    }

    @Override
    public void onDraw(Canvas c) {
        if (mData == null || mColors == null) {
            return;
        }
        int size = mData.length;
        float startAngle = mStartAngle, endAngle;
        int colorIndex = 0;
        // draw arc
//        RectF rectF = new RectF(mArcBounds);
        for (int i = 0; i < size; i++) {
            float percentage = Float.parseFloat(mData[i][1]);
            float sweep = p2d(percentage);
            endAngle = startAngle + sweep;
            int color = mColors[colorIndex++];
            mPaint.setColor(color);
//            mPaint.setShadowLayer(6.67f, 0, 4.67f, color);
            if (colorIndex == mColors.length)
                colorIndex = 0;
            c.drawArc(mRectFs[i], startAngle, sweep, true, mPaint);
            Log.e(TAG, "start: " + startAngle + " end: " + endAngle);
            startAngle = endAngle;
//            updateArcRect(mRectFs[i], i);
        }
        mPaint.clearShadowLayer();

        //draw circle shade in center
        mPaint.setColor(Color.WHITE);
        c.drawCircle(mOutBounds.right / 2, mOutBounds.bottom / 2, (int) (0.59 * ((d * sPieRatio) / 2)), mPaint);

        //draw text in center
        //TODO what about a looooooong text?
        if (mCenterElement != null) {
            String line1 = mCenterElement.first;
            mPaint.setColor(Color.RED);
            mPaint.setFakeBoldText(true);
            mPaint.setTextSize(getResources().getDimension(R.dimen.pie_chart_center_text_size_line1));
            c.drawText(line1, (mOutBounds.right - mPaint.measureText(line1)) / 2, mOutBounds.bottom / 2, mPaint);
            String line2 = mCenterElement.second;
            mPaint.setColor(Color.GRAY);
            mPaint.setFakeBoldText(false);
            mPaint.setTextSize(getResources().getDimension(R.dimen.pie_chart_center_text_size_line2));
            c.drawText(line2, (mOutBounds.right - mPaint.measureText(line2)) / 2, mOutBounds.bottom / 2 - mPaint.ascent() + mPaint.descent()
                    + getResources().getDimensionPixelOffset(R.dimen.pie_chart_center_text_margin), mPaint);
        }

        //draw text and dot
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(mTextSize);
        startAngle = mStartAngle;
        endAngle = 0;
        colorIndex = 0;
        double realAngle = 0; // Radian angle
        for (int i = 0; i < size; i++) {
            if (colorIndex == mColors.length)
                colorIndex = 0;
            float percentage = Float.parseFloat(mData[i][1]);
            float degree = p2d(percentage);
            endAngle = startAngle + degree;
            realAngle = (startAngle + degree / 2) * Math.PI / 180;
            int x = (int) (mOutBounds.right / 2 + (((mOutBounds.right / 2) * 0.8f) * Math.cos(realAngle)));
            int y = (int) (mOutBounds.bottom / 2 + (((mOutBounds.bottom / 2) * 0.8f) * Math.sin(realAngle)));
            String text = mData[i][0];
            if (x < (d * sPieRatio) / 2) { //在左半边的，需要先画点再画文字
                mPaint.setColor(mColors[colorIndex++]);
                c.drawCircle(x, y, sDotRadius, mPaint);
                mPaint.setColor(Color.GRAY);
                mPaint.setFakeBoldText(true);
                c.drawText(text, x - mPaint.measureText(text) / 2,
                        y - mPaint.ascent() + mPaint.descent() + sDotRadius, mPaint);
            } else {  //在右边的，先画文字再画点
                mPaint.setColor(Color.GRAY);
                mPaint.setFakeBoldText(true);
                c.drawText(text, x - mPaint.measureText(text) / 2, y + mPaint.ascent(), mPaint);
                mPaint.setColor(mColors[colorIndex++]);
                c.drawCircle(x, y, sDotRadius, mPaint);
            }
            startAngle = endAngle;
        }
    }

    public void startAnim() {

    }

    /**
     * 每一段的半径需要比上一段小，按照视觉要求，每次减小的因子不一样
     *
     * @param rectF
     * @param i
     */
    private RectF updateArcRect(RectF rectF, int i) {
        int minus = 8;  //基础减小因子是8
        if (i == 1) {
            minus = 12;
        } else if (i == 2) {
            minus = 10;
        } else if (i == 3) {
            minus = 8;
        }
        RectF r = new RectF(rectF);
        r.left += minus;
        r.top += minus;
        r.right -= minus;
        r.bottom -= minus;
        return r;
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