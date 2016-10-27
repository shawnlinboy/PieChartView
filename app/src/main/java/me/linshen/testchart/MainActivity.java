package me.linshen.testchart;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.linshen.testchart.widget.PieChart;
import me.linshen.testchart.widget.PieChartView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButton;
    private PieChartView mPieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        showPieView();
        findViewById(R.id.btn).setOnClickListener(this);
//        showPieViewAndroid();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {
            showPieView();
            Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPieView() {
        mPieChartView = (PieChartView) findViewById(R.id.chartView);
        int max = 100;
        int min = 0;
        Random random = new Random();
        int s1 = random.nextInt(max) % (max - min + 1) + min;
        int s2 = random.nextInt(max) % (max - min + 1) + min;
        int s3 = random.nextInt(max) % (max - min + 1) + min;
        int s4 = random.nextInt(max) % (max - min + 1) + min;
        PieChartView.PieElement element1 = new PieChartView.PieElement("Twitter", s1, getResources().getColor(R.color.turquoise));
        PieChartView.PieElement element2 = new PieChartView.PieElement("Facebook", s2, Color.RED);
        PieChartView.PieElement element3 = new PieChartView.PieElement("Whatsapp", s3, Color.BLUE);
        PieChartView.PieElement element4 = new PieChartView.PieElement("Meizu", s4, Color.CYAN);
        List<PieChartView.PieElement> list = new ArrayList<>();
        list.add(element1);
        list.add(element2);
        list.add(element3);
        list.add(element4);
        Pair<String, String> centerElement = new Pair<>("2000.53", "第二行");
//        mPieChartView.setData(list, null);
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
