package com.example.leaf3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    final private String[] localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textView);
            if(textView.getText().equals("")) {
                Button projectButton = (Button) view.findViewById(R.id.projectButton);
                projectButton.setOnClickListener(v -> openNewActivity((String) textView.getText(),view));

                Button deleteButton = (Button) view.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(v -> openDeleteActivity((String) textView.getText(),view));
            }
        }

        public TextView getTextView() {return textView;}

        public void openNewActivity(String dataString, View view){
            Context context = view.getContext();
            Intent intent = new Intent(context, UpdateProject.class);
            intent.putExtra("data",dataString);
            context.startActivity(intent);
        }

        public void openDeleteActivity(String dataString, View view){
            Context context = view.getContext();
            Intent intent = new Intent(context, DeleteProject.class);
            intent.putExtra("data",dataString);
            context.startActivity(intent);
        }
    }



    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public DataAdapter(String[] dataSet) {localDataSet = dataSet;}

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        // Get element from dataset at this position and replace the
        // contents of the view with that element
        if(!localDataSet[position].equals("")) {
            viewHolder.getTextView().setText(localDataSet[position]);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {return localDataSet.length;}
}



