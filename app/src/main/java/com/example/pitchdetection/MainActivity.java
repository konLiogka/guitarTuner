package com.example.pitchdetection;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;


import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements PitchDetector.PitchDetectionListener {
    public TextView pitchTextView;
    public TextView noteTextView;
    private PitchDetector pitchDetector;



    public  String[][] notesList = {

           {"A1", "55.00"},
           {"A#1", "58.27"},
           {"B1", "61.74"},
           {"C2", "65.41"},
           {"C#2", "69.30"},
           {"D2", "73.42"},
           {"D#2", "77.78"},
           {"E2", "82.41"},
           {"F2", "87.31"},
           {"F#2", "92.50"},
           {"G2", "98.00"},
           {"G#2", "103.83"},
           {"A2", "110.00"},
           {"A#2", "116.54"},
           {"B2", "123.47"},
           {"C3", "130.81"},
           {"C#3", "138.59"},
           {"D3", "146.83"},
           {"D#3", "155.56"},
           {"E3", "164.81"},
           {"F3", "174.61"},
           {"F#3", "185.00"},
           {"G3", "196.00"},
           {"G#3", "207.65"},
           {"A3", "220.00"},
           {"A#3", "233.08"},
           {"B3", "246.94"},
           {"C4", "261.63"},
           {"C#4", "277.18"},
           {"D4", "293.66"},
           {"D#4", "311.13"},
           {"E4", "329.63"},
           {"F4", "349.23"},
           {"F#4", "369.99"},
           {"G4", "392.00"},
           {"G#4", "415.30"},
           {"A4", "440.00"},
           {"A#4", "466.16"},
           {"B4", "493.88"},
           {"C5", "523.25"},
           {"C#5", "554.37"},
           {"D5", "587.33"},
           {"D#5", "622.25"},
           {"E5", "659.25"},
           {"F5", "698.46"},
           {"F#5", "739.99"},
           {"G5", "783.99"},
           {"G#5", "830.61"},
           {"A5", "880.00"},
           {"A#5", "932.33"},
           {"B5", "987.77"},
           {"C6", "1046.50"},
           {"C#6", "1108.73"},
           {"D6", "1174.66"},
           {"D#6", "1244.51"},
           {"E6", "1318.51"},
           {"F6", "1396.91"},
           {"F#6", "1479.98"},
           {"G6", "1567.98"},
           {"G#6", "1661.22"},
           {"A6", "1760.00"},
           {"A#6", "1864.66"},
           {"B6", "1975.53"},
           {"C7", "2093.00"},
           {"C#7", "2217.46"},
           {"D7", "2349.32"},
           {"D#7", "2489.02"},
           {"E7", "2637.02"},
           {"F7", "2793.83"},
           {"F#7", "2959.96"},
           {"G7", "3135.96"},
           {"G#7", "3322.44"},
           {"A7", "3520.00"},
           {"A#7", "3729.31"},
           {"B7", "3951.1"}
   };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 123);
        } else {
            pitchDetector = new PitchDetector();
            pitchDetector.start(this);
        }

        pitchTextView = findViewById(R.id.freq);
        noteTextView = findViewById(R.id.note);
        pitchDetector = new PitchDetector();
        pitchDetector.setPitchDetectionListener(this);


        CardView cardView = findViewById(R.id.tuningCardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pitchDetector.stop();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                TuningFragment fragment = new TuningFragment();
                fragmentTransaction.replace(R.id.fragmenttuning, fragment);
                fragmentTransaction.commit();



            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pitchDetector.start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pitchDetector.stop();
    }

    @Override
    public void onPitchDetected( final double pitchFrequency) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(pitchFrequency>50 &&  pitchFrequency<4000 ) {



                        pitchTextView.setText(String.format("%.2f", pitchFrequency)  + " Hz");
                        for (int i = 0; i < notesList.length; i++) {
                            double targetFrequency = Double.parseDouble(notesList[i][1]);
                            double difference = Math.abs(pitchFrequency - targetFrequency);
                            if (difference <= 3.0) {

                                noteTextView.setText(notesList[i][0]);
                                return;
                            }else{
                                noteTextView.setText(" ");
                            }
                        }
                    }





         }





        });
    }
}