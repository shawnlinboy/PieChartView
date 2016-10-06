package me.linshen.testchart.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import me.linshen.testchart.R;

/**
 * @author vivek
 */
//========================================
public class PieChartView extends View {

    private final static String[] RColors = {"#448c9b", "#789a98", "#a4a795", "#ac6226", "#ef3c5e", "#b17170", "#9a9a82", "#bda763"};

    private RectF rec;
    private Paint p = new Paint();
    private String[][] data = {
            {"Facebook", "20"},
            {"Twitter", "20"},
            {"Whats app", "20"},
            {"Pinterest", "20"},
            {"Baby App", "20"}
    };
    private int d = 0;
    private float mTextSize = 20f;

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
            mTextSize = a.getDimension(R.styleable.PieChartView_android_text, 20f);
        } finally {
            a.recycle();
        }
    }

    //========================
    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        d = nw > nh ? nh : nw;
        rec = new RectF(0, 0, d, d);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public void setTextSize(float size) {
        mTextSize = size;
    }

    /**
     * Set data to be shown in pie chart view
     *
     * @param dList ArrayList of Object[], 1st element will contain the name, 2nd the percentage (int)
     */
    public void setData(ArrayList<Object[]> dList) {
        data = new String[dList.size()][2];
        int i = 0;
        for (Object[] op : dList) {
            data[i++] = new String[]{op[0] + "", op[1] + ""};
        }
    }

    //========================
    @Override
    public void onDraw(Canvas c) {

        int size = data.length;
        int startAngle = 0, endAngle = 0, ci = 0;
        //== draw arc
        for (int i = 0; i < size; i++) {
            int perc = Integer.parseInt(data[i][1]);
            int pdeg = (perc * 360) / 100;
            endAngle = startAngle + pdeg;
            p.setColor(Color.parseColor(RColors[ci++]));
            if (ci == RColors.length)
                ci = 0;
            c.drawArc(rec, startAngle + 2, pdeg - 2, true, p);
            startAngle = endAngle;
        }

        //== draw circle in center
        p.setColor(Color.WHITE);
        c.drawCircle(rec.right / 2, rec.bottom / 2, (int) (0.8 * (d / 2)), p);

        //== write text
        p.setColor(Color.BLACK);
        p.setFakeBoldText(true);
        p.setTextSize(mTextSize);
        startAngle = 0;
        endAngle = 0;
        ci = 0;
        double ra = 0; // Radian angle
        for (int i = 0; i < size; i++) {
            p.setColor(Color.parseColor(RColors[ci++]));
            if (ci == RColors.length)
                ci = 0;
            int perc = Integer.parseInt(data[i][1]);
            int pdeg = (perc * 360) / 100;
            endAngle = startAngle + pdeg; //== in degrees
            ra = (startAngle + pdeg / 2) * Math.PI / 180;
            int x = (int) (rec.right / 2 + (((rec.right / 2) * .5) * Math.cos(ra)));
            int y = (int) (rec.right / 2 + (((rec.right / 2) * .5) * Math.sin(ra)));
            String text = data[i][0];
            c.drawText(text, x - p.measureText(text) / 2, y, p);
            text = data[i][1] + "%";
            c.drawText(text, x - p.measureText(text) / 2, y - p.ascent() + p.descent(), p);
            startAngle = endAngle;
        }
    }
}