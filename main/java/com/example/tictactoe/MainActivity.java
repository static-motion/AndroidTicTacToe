package com.example.tictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn4;
    private Button mBtn5;
    private Button mBtn6;
    private Button mBtn7;
    private Button mBtn3;
    private Button mBtn8;
    private Button mBtn9;
    private int circle = R.drawable.circle;
    private int cross = R.drawable.cross;
    private int mTurnsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn1 = findViewById(R.id.btn_1);
        mBtn2 = findViewById(R.id.btn_2);
        mBtn3 = findViewById(R.id.btn_3);
        mBtn4 = findViewById(R.id.btn_4);
        mBtn5 = findViewById(R.id.btn_5);
        mBtn6 = findViewById(R.id.btn_6);
        mBtn7 = findViewById(R.id.btn_7);
        mBtn8 = findViewById(R.id.btn_8);
        mBtn9 = findViewById(R.id.btn_9);

        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
        mBtn3.setOnClickListener(this);
        mBtn4.setOnClickListener(this);
        mBtn5.setOnClickListener(this);
        mBtn6.setOnClickListener(this);
        mBtn7.setOnClickListener(this);
        mBtn8.setOnClickListener(this);
        mBtn9.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_1:
                updateButton(mBtn1);
                break;
            case R.id.btn_2:
                updateButton(mBtn2);
                break;
            case R.id.btn_3:
                updateButton(mBtn3);
                break;
            case R.id.btn_4:
                updateButton(mBtn4);
                break;
            case R.id.btn_5:
                updateButton(mBtn5);
                break;
            case R.id.btn_6:
                updateButton(mBtn6);
                break;
            case R.id.btn_7:
                updateButton(mBtn7);
                break;
            case R.id.btn_8:
                updateButton(mBtn8);
                break;
            case R.id.btn_9:
                updateButton(mBtn9);
                break;
        }
        mTurnsCount++;
    }

    private void updateButton(Button button) {
        if(mTurnsCount % 2 == 0){
            button.setBackgroundResource(cross);
        }
        else {
            button.setBackgroundResource(circle);
        }
    }
}
