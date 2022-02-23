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
    String localUsername;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        //Add buttons to each project in the database to 'Update' and 'Delete'
        public ViewHolder(View view, String username) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textView);
            //I'm not sure why I added this
            if(textView.getText().equals("")) {
                //Add listener to 'Update' button and call function to launch new activity
                Button projectButton = (Button) view.findViewById(R.id.projectButton);
                projectButton.setOnClickListener(v -> openNewActivity((String) textView.getText(), username, view));

                //Add listener to 'Delete' button and call function to launch new activity
                Button deleteButton = (Button) view.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(v -> openDeleteActivity((String) textView.getText(), username, view));
            }
        }

        public TextView getTextView() {return textView;}

        //Launch UpdateProject activity
        public void openNewActivity(String dataString, String username, View view){
            Context context = view.getContext();
            Intent intent = new Intent(context, UpdateProject.class);
            intent.putExtra("data",dataString);
            intent.putExtra("username",username);
            context.startActivity(intent);
        }

        //Launch DeleteProject activity
        public void openDeleteActivity(String dataString, String username, View view){
            Context context = view.getContext();
            Intent intent = new Intent(context, DeleteProject.class);
            intent.putExtra("data",dataString);
            intent.putExtra("username",username);
            context.startActivity(intent);
        }
    }



    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     * @param username
     */
    //Constructor
    public DataAdapter(String[] dataSet, String username) {
        localDataSet = dataSet;
        localUsername = username;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);
        return new ViewHolder(view, localUsername);
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



