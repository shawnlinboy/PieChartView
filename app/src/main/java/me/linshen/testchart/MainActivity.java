package me.linshen.testchart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import me.linshen.testchart.widget.PieChartView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButton;
    private PieChartView mPieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.btn);
        mButton.setOnClickListener(this);
        mPieChartView = (PieChartView) findViewById(R.id.chartView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {
            PieChartView.PieElement element1 = new PieChartView.PieElement("Twitter", 10, Color.YELLOW);
            PieChartView.PieElement element2 = new PieChartView.PieElement("Facebook", 30, Color.BLACK);
            PieChartView.PieElement element3 = new PieChartView.PieElement("Whatsapp", 20, Color.RED);
            PieChartView.PieElement element4 = new PieChartView.PieElement("Meizu", 20, Color.BLUE);
            PieChartView.PieElement element5 = new PieChartView.PieElement("Xiaomi", 20, Color.GRAY);
            List<PieChartView.PieElement> list = new ArrayList<>();
            list.add(element1);
            list.add(element2);
            list.add(element3);
            list.add(element4);
            list.add(element5);
            mPieChartView.setPieElements(list);
        }
    }
}
