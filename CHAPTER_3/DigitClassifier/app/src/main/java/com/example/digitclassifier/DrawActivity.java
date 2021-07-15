package com.example.digitclassifier;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.divyanshu.draw.widget.DrawView;

public class DrawActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        DrawView drawView = findViewById(R.id.drawView);
    }
}