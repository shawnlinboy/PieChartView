package me.linshen.testchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.linshen.testchart.widget.CanvasView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        CanvasView canvasView = (CanvasView) findViewById(R.id.fuck);
        canvasView.startAnim();
    }

}
