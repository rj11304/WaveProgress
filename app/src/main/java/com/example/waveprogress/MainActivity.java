package com.example.waveprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.widget.waveprogress.WaveProgress;

public class MainActivity extends AppCompatActivity {

    private WaveProgress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (WaveProgress) findViewById(R.id.wave);
        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.animatorStart();
            }
        });
    }
}
