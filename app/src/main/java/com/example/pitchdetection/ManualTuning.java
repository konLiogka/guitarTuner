package com.example.pitchdetection;

import static com.example.pitchdetection.MainActivity.notesList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class ManualTuning {
    private final Context context;

    public ManualTuning(Context context) {
        this.context = context;
    }
    public void changeColor(Button button){


         int color;
        View view = LayoutInflater.from(context).inflate(R.layout.activity_main, null);
        color = Color.parseColor("#A2FF86");



        button.setBackgroundTintList(ColorStateList.valueOf(color));

    }

    public void manualTuning(Button btn, double pitchFrequency){

    }
}
