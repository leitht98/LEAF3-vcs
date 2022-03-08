package com.example.leaf3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.util.Arrays;

public class DataLog extends AppCompatActivity {
    Button backButton;
    String dataString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_log);
        backButton = findViewById(R.id.backButton);

        RecyclerView projectDataList = findViewById(R.id.dataList);

        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");
        String username = intent.getStringExtra("username");
        String[] dataArray = dataString.split("\\{");

        for (int i=0; i<dataArray.length; i++){
            String[] tempArray = dataArray[i].split(", ");
            Arrays.sort(tempArray);
            StringBuilder tempString = new StringBuilder();
            for(int j=0; j<tempArray.length; j++){
                if(j<tempArray.length-1) {tempString.append(tempArray[j]).append(", ");
                } else{tempString.append(tempArray[j]);}
            }
            dataArray[i] = tempString.toString();
            dataArray[i] = dataArray[i].replace("}", "");
            dataArray[i] = dataArray[i].replace(", ", "\n");
            dataArray[i] = dataArray[i].replace("=", " = ");
            dataArray[i] = dataArray[i].replace("_", " ");
            dataArray[i] = dataArray[i].trim();
        }
        String[] trimArray = new String[dataArray.length-1];
        int iterator = 0;
        for(String s:dataArray){
            if(!s.equals("")){
                trimArray[iterator] = s;
                iterator++;
            }
        }
        //get a new instance of a very basic custom adapter (taken from the google tutorials, mostly)
        DataAdapter dataAdapter = new DataAdapter(trimArray,username);
        //add the adapter to the list thing
        projectDataList.setAdapter(dataAdapter);
        //make a layout manager and add it
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        projectDataList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(projectDataList.getContext(), layoutManager.getOrientation());
        projectDataList.addItemDecoration(dividerItemDecoration);

        backButton.setOnClickListener(v -> this.finish());
    }
}