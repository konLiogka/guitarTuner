package com.example.pitchdetection;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

public class TuningFragment extends Fragment implements RecyclerAdapter.OnItemClickListener{
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;


    public TuningFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {


        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tuning, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<String> data = new ArrayList<>();
        data.add("Automatic Tuning");
        data.add("Standard E (E2 A2 D3 G3 B3 e4)");
        data.add("1st Down (D#2 G#2 C#3 F#3 A#3 d#4)");
        data.add("2st Down (D2 G2 C3 F3 A3 d4)");
        data.add("3st Down (C#2 F#2 B2 E3 G#3 c#4)");
        data.add("4st Down (C2  F2  A2 D#3 G3  c3)");






        adapter = new RecyclerAdapter(data);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        return view;


    }

    @Override
    public void onItemClick(String[] notes, String clickedItem) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {

            activity.setTuning(notes,clickedItem);


        }
    }
}