package me.linshen.testchart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        }
    }
}
