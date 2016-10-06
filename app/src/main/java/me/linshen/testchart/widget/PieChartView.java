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
import android.util.TypedValue;
import android.view.View;

import java.util.List;

import me.linshen.testchart.R;

/**
 * Created by linshen on 2016/10/6.
 */
public class PieChartView extends View {

    private static final String TAG = "PieChartView";

    private RectF rec;
    private Paint p = new Paint();
    private String[][] mData;
    private int[] mColors = null;

    private int d = 0;
    private float mTextSize = 20f;
    private int mStartAngle = 90;
    private int mPieGap = 2;

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
            mStartAngle = a.getInt(R.styleable.PieChartView_startAngle, 90);
            checkStartAngle();
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        d = nw > nh ? nh : nw;
        rec = new RectF(0, 0, d, d);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public void setTextSize(float size) {
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

    public void setTextSize(int unit, float size) {
        Context c = getContext();
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        setTextSize(TypedValue.applyDimension(
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

    public void setPieElements(List<PieElement> elements) {
        if (elements != null && elements.size() > 0) {
            mData = new String[elements.size()][2];
            mColors = new int[elements.size()];
            int dataIndex = 0;
            int colorIndex = 0;
            for (PieElement element : elements) {
                mData[dataIndex++] = new String[]{element.name + "", element.percentage + ""};
                mColors[colorIndex++] = element.color;
            }
            postInvalidate();
        }
    }

    @Override
    public void onDraw(Canvas c) {
        if (mData == null || mColors == null) {
            return;
        }
        int size = mData.length;
        int startAngle = mStartAngle, endAngle, colorIndex = 0;
        //== draw arc
        for (int i = 0; i < size; i++) {
            int percentage = Integer.parseInt(mData[i][1]);
            int degree = p2d(percentage);
            endAngle = startAngle + degree;
            Log.d(TAG, "draw arc, startAngle: " + startAngle + ", end angle: " + endAngle);
            p.setColor(mColors[colorIndex++]);
            if (colorIndex == mColors.length)
                colorIndex = 0;
            c.drawArc(rec, startAngle + mPieGap, degree - mPieGap, true, p);
            startAngle = endAngle;
        }

        //== draw circle in center
        p.setColor(Color.WHITE);
        c.drawCircle(rec.right / 2, rec.bottom / 2, (int) (0.8 * (d / 2)), p);

        //== write text
        p.setColor(Color.BLACK);
        p.setFakeBoldText(true);
        p.setTextSize(mTextSize);
        startAngle = mStartAngle;
        colorIndex = 0;
        double ra = 0; // Radian angle
        for (int i = 0; i < size; i++) {
            p.setColor(mColors[colorIndex++]);
            if (colorIndex == mColors.length)
                colorIndex = 0;
            int percentage = Integer.parseInt(mData[i][1]);
            int degree = p2d(percentage);
            endAngle = startAngle + degree; //== in degrees
            ra = (startAngle + percentage / 2) * Math.PI / 180;
            int x = (int) (rec.right / 2 + (((rec.right / 2) * .5) * Math.cos(ra)));
            int y = (int) (rec.right / 2 + (((rec.right / 2) * .5) * Math.sin(ra)));
            String text = mData[i][0];
            c.drawText(text, x - p.measureText(text) / 2, y, p);
            text = mData[i][1] + "%";
            c.drawText(text, x - p.measureText(text) / 2, y - p.ascent() + p.descent(), p);
            startAngle = endAngle;
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