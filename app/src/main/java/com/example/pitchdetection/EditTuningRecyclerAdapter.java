package com.example.pitchdetection;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

//Editing a tuning.
public class EditTuningRecyclerAdapter extends RecyclerView.Adapter<EditTuningRecyclerAdapter.ViewHolder> {
    static private List<String> items;
    private static MainActivity activity;

    public EditTuningRecyclerAdapter(List<String> items, MainActivity activity) {
        EditTuningRecyclerAdapter.items = items.subList(11, items.size());
        EditTuningRecyclerAdapter.activity = activity;
    }

    @Override
    public EditTuningRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edittuning_textview, parent, false);

        return new EditTuningRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EditTuningRecyclerAdapter.ViewHolder holder, int position) {
        String item = items.get(position);
        holder.editTuningText.setText(item);

    }


    @Override
    public int getItemCount() {
        return items.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView editTuningText;
        private String clickedItem ;
        public ViewHolder(View itemView) {
            super(itemView);
            editTuningText = itemView.findViewById(R.id.editTuningText);

            ImageButton editButton = itemView.findViewById(R.id.editButton);

            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                    clickedItem = items.get(position);
                    activity.onClickCustomTView(clickedItem,position);


            });

            ImageButton deleteButton = itemView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v ->{
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to remove this item?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    int position = getAdapterPosition();
                    activity.removeTuning(activity ,position);

                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            });



        }





    }


}
