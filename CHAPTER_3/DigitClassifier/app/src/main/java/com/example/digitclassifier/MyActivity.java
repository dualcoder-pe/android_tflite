package com.example.digitclassifier;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Button finishBtn = findViewById(R.id.finish_Btn);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Log.d("MyActivity", "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MyActivity", "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MyActivity", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MyActivity", "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MyActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MyActivity", "onPause");
    }

}

