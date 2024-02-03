package com.example.pitchdetection;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
public class TuningFragment extends Fragment implements TuningRecyclerAdapter.OnItemClickListener{
    private RecyclerView recyclerView;
    private List<String> data;
    public TuningFragment( List<String>  data) {
     this.data=data;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tuning, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TuningRecyclerAdapter adapter = new TuningRecyclerAdapter(data);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onItemClick(String[] notes, String clickedItem , RecyclerView recyclerView) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setTuning(notes, clickedItem);
        }
    }
}