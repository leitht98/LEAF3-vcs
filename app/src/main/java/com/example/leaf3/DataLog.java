package com.example.leaf3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class DataLog extends AppCompatActivity {
    Button backButton;
    String dataString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_log);
        backButton = findViewById(R.id.backButton);

        //Data stuff

        //get the list
        RecyclerView projectDataList = findViewById(R.id.dataList);

        //Find and tidy data
        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");
        String[] dataArray = dataString.split("\\{");
        for (int i=0; i<dataArray.length; i++){
            //gets rid of a trailing bracket (used the other to split it)
            dataArray[i] = dataArray[i].replace("}", "");
            //adds new lines
            dataArray[i] = dataArray[i].replace(", ", "\n");
            //removes dead space
            dataArray[i] = dataArray[i].trim();
        }

        //get a new instance of a very basic custom adapter (taken from the google tutorials, mostly)
        DataAdapter dataAdapter = new DataAdapter(dataArray);
        //add the adapter to the list thing
        projectDataList.setAdapter(dataAdapter);
        //make a layout manager and add it
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        projectDataList.setLayoutManager(layoutManager);
        //make it sexy
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(projectDataList.getContext(), layoutManager.getOrientation());
        projectDataList.addItemDecoration(dividerItemDecoration);

        backButton.setOnClickListener(v -> {
            this.finish();
        });
    }


}