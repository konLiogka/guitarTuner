package com.example.pitchdetection;

import android.content.Context;
import android.content.SharedPreferences;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TuningsList {

    private static final String LIST_KEY = "tuningList_key";


    public static void saveList(Context context, List<String> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray(list);
        editor.putString(LIST_KEY, jsonArray.toString());
        editor.apply();
    }

    public static List<String> getList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String jsonArrayString = sharedPreferences.getString(LIST_KEY, null);
        List<String> list = new ArrayList<>();

        if (jsonArrayString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    public static void addToList( String newString,Context context) {
        List<String> data = getList(context);
        data.add(newString);
        saveList(context, data);
    }

    public static List<String> fillList(Context context){
        List<String> data = getList(context);
        data.add("Automatic Tuning");
        data.add("E Standard(E2 A2 D3 G3 B3 e4)");
        data.add("D# Standard(D#2 G#2 C#3 F#3 A#3 d#4)");
        data.add("D Standard(D2 G2 C3 F3 A3 d4)");
        data.add("C# Standard(C#2 F#2 B2 E3 G#3 c#4)");
        data.add("C Standard(C2 F2 A#2 D#3 G3 c3)");
        data.add("B Standard(B1 E2 A2 D3 F#3 b3)");
        data.add("Drop D(D2 A2 D3 G3 B3 e4)");
        data.add("Drop C#(C#2 G#2 C#3 F#3 A#3 d#4)");
        data.add("Drop C(C2 G2 C3 F3 A3 d4)");
        data.add("Drop B(B1 F#2 B2 E3 G#3 c#4)");
        saveList(context, data);
        return data;
    }
}
