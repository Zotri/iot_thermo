package com.example.thermalapp;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.text.MessageFormat;

public class StartTempCheckActivity extends AppCompatActivity {

    TextView seekBarText;
    int minimumVal = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_temp_check);

        SeekBar seekBar = findViewById(R.id.seek_bar);
        seekBar.setProgress(20);
        seekBar.incrementProgressBy(1);
        seekBar.setMax(28);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        seekBarText = findViewById(R.id.text_seek_bar);

        seekBarText.setText(MessageFormat.format("Slide to set temperature degree: {0}°", progress));
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress >= minimumVal) {
                seekBar.setProgress(progress);
            seekBarText.setText(MessageFormat.format("Temperature Value: {0}°", progress));
            } else {
                seekBar.setProgress(minimumVal);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
            Toast.makeText(StartTempCheckActivity.this,
                    "Set temperature value on", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Toast.makeText(StartTempCheckActivity.this,
                    "Temperature is set", Toast.LENGTH_SHORT).show();
        }
    };

}
