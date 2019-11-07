package com.example.aopdemo2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";
    private Button btnClick1;
    private Button btnClick2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnClick1 = (Button) findViewById(R.id.btn_click1);
        btnClick2 = (Button) findViewById(R.id.btn_click2);

        btnClick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });
        btnClick2.setOnClickListener(new MyClickListener());
    }


    private void show() {
        Log.d(TAG, "show: ");
    }
}
