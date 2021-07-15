package com.example.digitclassifier;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.divyanshu.draw.activity.DrawingActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = (EditText)findViewById(R.id.input_Et);
        Button button = (Button)findViewById(R.id.input_Btn);
        TextView textView = (TextView)findViewById(R.id.output_Tv);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                textView.setText(text);

//                try {
//                    Thread.sleep(10 * 1000);
//                    Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10 * 1000);
                            Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_SHORT).show();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

//        button.setOnClickListener(view -> {
//        });

        Button actBtn = findViewById(R.id.activity_launch_Btn);
        actBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyActivity.class);
                startActivity(i);
            }
        });

        Button drawSampleBtn = findViewById(R.id.draw_sample_Btn);
        drawSampleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DrawingActivity.class);
                startActivity(i);
            }
        });

        Button drawBtn = findViewById(R.id.draw_Btn);
        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DrawActivity.class);
                startActivity(i);
            }
        });

    }
}