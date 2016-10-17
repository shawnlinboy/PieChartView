package me.linshen.testchart;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import me.linshen.testchart.widget.PieChart;
import me.linshen.testchart.widget.PieChartView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButton;
    private PieChartView mPieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showPieView();
//        showPieViewAndroid();
    }

    @Override
    public void onClick(View v) {
/*
        if (v.getId() == R.id.btn) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        }
*/
    }

    private void showPieView() {
        mPieChartView = (PieChartView) findViewById(R.id.chartView);
        PieChartView.PieElement element1 = new PieChartView.PieElement("Twitter", 86.5f, getResources().getColor(R.color.turquoise));
        PieChartView.PieElement element2 = new PieChartView.PieElement("Facebook", 4.8f, Color.RED);
        PieChartView.PieElement element3 = new PieChartView.PieElement("Whatsapp", 4.8f, Color.BLUE);
        PieChartView.PieElement element4 = new PieChartView.PieElement("Meizu", 3.8f, Color.CYAN);
        List<PieChartView.PieElement> list = new ArrayList<>();
        list.add(element1);
        list.add(element2);
        list.add(element3);
        list.add(element4);
        Pair<String, String> centerElement = new Pair<>("-2000.50", "第二行");
        mPieChartView.setData(list, centerElement);
        mPieChartView.startAnim();
    }

    private void showPieViewAndroid() {
        Resources res = getResources();
        final PieChart pie = (PieChart) this.findViewById(R.id.chartView);
        pie.addItem("Agamemnon", 2, res.getColor(R.color.seafoam));
        pie.addItem("Bocephus", 3.5f, res.getColor(R.color.chartreuse));
        pie.addItem("Calliope", 2.5f, res.getColor(R.color.emerald));
        pie.addItem("Daedalus", 3, res.getColor(R.color.bluegrass));
        pie.addItem("Euripides", 1, res.getColor(R.color.turquoise));
        pie.addItem("Ganymede", 3, res.getColor(R.color.slate));
    }
}
