package com.example.pitchdetection;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.List;


public class EditTuningFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<String> data;

    public EditTuningFragment(List<String>  data) {
        this.data=data;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_edit_tuning, container, false);
        recyclerView = view.findViewById(R.id.editRecyclerViewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MainActivity activity = (MainActivity) getActivity();
        EditTuningRecyclerAdapter adapter = new EditTuningRecyclerAdapter(data, activity);
        recyclerView.setAdapter(adapter);
        Button close = view.findViewById(R.id.close);
        close.setOnClickListener(view1 -> {
            activity.onClickEditTView();
        });
        return view;
    }
}