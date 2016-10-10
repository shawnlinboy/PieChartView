package me.linshen.testchart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
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
        showPieView();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        }
    }

    private void showPieView() {
        PieChartView.PieElement element1 = new PieChartView.PieElement("Twitter", 40, Color.YELLOW);
        PieChartView.PieElement element2 = new PieChartView.PieElement("Facebook", 30, Color.RED);
        PieChartView.PieElement element3 = new PieChartView.PieElement("Whatsapp", 30, Color.BLUE);
        List<PieChartView.PieElement> list = new ArrayList<>();
        list.add(element1);
        list.add(element2);
        list.add(element3);
//        list.add(element4);
        mPieChartView.setPieElements(list);
        Pair<String, String> centerElement = new Pair<>("-2000.50", "第二行");
        mPieChartView.setCenterElement(centerElement);
    }
}
