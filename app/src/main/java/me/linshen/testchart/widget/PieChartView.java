package me.linshen.testchart.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

    private static final int sPieGap = 0;
    private static final float sPieRatio = 0.86f;
    private static final float sDotRadius = 6.67f;

    private RectF mArcRect;
    private RectF mCenterRect;
    private Paint mPaint = new Paint();
    private String[][] mData;
    private int[] mColors = null;

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
            mStartAngle = a.getInt(R.styleable.PieChartView_startAngle, 120);
            checkStartAngle();
        } finally {
            a.recycle();
        }
        mPaint.setAntiAlias(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        d = nw > nh ? nh : nw;
        mCenterRect = new RectF(0, 0, d, d);
        mArcRect = new RectF((1 - sPieRatio) * d, (1 - sPieRatio) * d, sPieRatio * d, sPieRatio * d);
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

    /**
     * Set the default start angle to a given value.
     *
     * @param angle The desired start angle. Value <b>MUST</b> between 0 and 360.
     */
    public void setStartAngle(int angle) {
        mStartAngle = angle;
        checkStartAngle();
        invalidate();
    }

    /***
     * Set pie elements data.
     * <p>
     * <b>Note:</b> Most methods do not check to see that sum of all elements' percentage is 100.
     * No matter how many elements they are.
     *
     * @param elements
     */
    public void setPieElements(List<PieElement> elements) {
        if (elements != null) {
            if (elements.size() > 0 && elements.size() <= 4) {
                mData = new String[elements.size()][2];
                mColors = new int[elements.size()];
                int dataIndex = 0;
                int colorIndex = 0;
                for (PieElement element : elements) {
                    mData[dataIndex++] = new String[]{element.name + "", element.percentage + ""};
                    mColors[colorIndex++] = element.color;
                }
                postInvalidate();
            } else {
                throw new IllegalArgumentException("PieChartView can only support 4 or fewer data elements");
            }
        }
    }

    /**
     * Set center text element.
     *
     * @param element The desired text element to be shown
     */
    public void setCenterElement(Pair<String, String> element) {
        mCenterElement = element;
        if (element != null) {
            invalidate();
        }
    }

    @Override
    public void onDraw(Canvas c) {
        Log.d(TAG, "onDraw() called!!!");
        if (mData == null || mColors == null) {
            return;
        }
        int size = mData.length;
        int startAngle = mStartAngle, endAngle, colorIndex = 0;
        // draw arc
        for (int i = 0; i < size; i++) {
            int percentage = Integer.parseInt(mData[i][1]);
            int degree = p2d(percentage);
            endAngle = startAngle + degree;
            mPaint.setColor(mColors[colorIndex++]);
            if (colorIndex == mColors.length)
                colorIndex = 0;
            c.drawArc(mArcRect, startAngle + sPieGap, degree - sPieGap, true, mPaint);
            startAngle = endAngle;
            updateArcRect();
        }

        //draw circle shade in center
        mPaint.setColor(Color.WHITE);
        c.drawCircle(mCenterRect.right / 2, mCenterRect.bottom / 2, (int) (0.59 * ((d * sPieRatio) / 2)), mPaint);

        //draw text in center
        //TODO what about a looooooong text?
        if (mCenterElement != null) {
            String line1 = mCenterElement.first;
            mPaint.setColor(Color.RED);
            mPaint.setFakeBoldText(true);
            mPaint.setTextSize(getResources().getDimension(R.dimen.pie_chart_center_text_size_line1));
            c.drawText(line1, (d - mPaint.measureText(line1)) / 2, d / 2, mPaint);
            String line2 = mCenterElement.second;
            mPaint.setColor(Color.GRAY);
            mPaint.setFakeBoldText(false);
            mPaint.setTextSize(getResources().getDimension(R.dimen.pie_chart_center_text_size_line2));
            c.drawText(line2, (d - mPaint.measureText(line2)) / 2, d / 2 - mPaint.ascent() + mPaint.descent()
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
            int percentage = Integer.parseInt(mData[i][1]);
            int degree = p2d(percentage);
            endAngle = startAngle + degree;
            realAngle = (startAngle + degree / 2) * Math.PI / 180;
            int x = (int) (mCenterRect.right / 2 + (((mCenterRect.right / 2) * .5) * Math.cos(realAngle)));
            int y = (int) (mCenterRect.right / 2 + (((mCenterRect.right / 2) * .5) * Math.sin(realAngle)));
            String text = mData[i][0];
            if (x > d / 2) {
                mPaint.setColor(mColors[colorIndex++]);
                c.drawCircle(x, y, sDotRadius, mPaint);
                mPaint.setColor(Color.GRAY); //TODO what color?
                mPaint.setFakeBoldText(true);
                c.drawText(text, x - mPaint.measureText(text) / 2, y - mPaint.ascent() + mPaint.descent(), mPaint);
            } else {
                mPaint.setColor(Color.GRAY); //TODO what color?
                mPaint.setFakeBoldText(true);
                c.drawText(text, x - mPaint.measureText(text) / 2, y, mPaint);
                mPaint.setColor(mColors[colorIndex++]);
                c.drawCircle(x, y - mPaint.ascent(), sDotRadius, mPaint);
            }
            Log.d(TAG, "draw [" + text + "] and dot at: x = [" + x + "] y = [" + y + "]");
            startAngle = endAngle;
        }
    }

    private void updateArcRect() {
        if (mArcRect != null) {
            mArcRect.left = mArcRect.left + 8;
            mArcRect.top = mArcRect.top + 8;
            mArcRect.right = mArcRect.right - 8;
            mArcRect.bottom = mArcRect.bottom - 8;
        }
    }

    private int p2d(int percentage) {
        if (percentage < 0) {
            percentage = 0;
        } else if (percentage > 100) {
            percentage = 100;
        }
        return (percentage * 360) / 100;
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
        private int percentage;
        private int color;

        public PieElement(String name, int percentage, int color) {
            this.name = name;
            this.percentage = percentage;
            this.color = color;
        }

        @Override
        public String toString() {
            return "PieElement{" +
                    "name='" + name + '\'' +
                    ", percentage=" + percentage +
                    ", color=" + color +
                    '}';
        }
    }

}