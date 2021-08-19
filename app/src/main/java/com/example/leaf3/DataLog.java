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

        //Data stuff

        //get the list
        RecyclerView projectDataList = findViewById(R.id.dataList);

        //Find and tidy data
        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");
        String[] dataArray = dataString.split("\\{");

        for (int i=0; i<dataArray.length; i++){
            //System.out.println(i+">>> "+dataArray[i]);
            String[] tempArray = dataArray[i].split(", ");
            Arrays.sort(tempArray);
            String tempString = "";
            for(int j=0; j<tempArray.length; j++){
                //System.out.println(">"+tempArray[j]);
                if(j<tempArray.length-1) {
                    tempString += tempArray[j] + ", ";
                } else{
                    //System.out.println("~~~~~~~~~~");
                    tempString += tempArray[j];
                }
            }
            //System.out.println("--->>> "+ tempString);
            dataArray[i] = tempString;
            //String projectID = dataArray[i].substring(dataArray[i].length()-20);
            //gets rid of a trailing bracket (used the other to split it)
            dataArray[i] = dataArray[i].replace("}", "");
            //adds new lines
            dataArray[i] = dataArray[i].replace(", ", "\n");
            //removes dead space
            dataArray[i] = dataArray[i].replace("=", " = ");
            dataArray[i] = dataArray[i].replace("_", " ");
            dataArray[i] = dataArray[i].trim();
            //System.out.println(dataArray[i]);
            //System.out.println(projectID);
        }
        String[] trimArray = new String[dataArray.length-1];
        int iter = 0;
        for(String s:dataArray){
            //System.out.println(">>>"+s);
            if(!s.equals("")){
                //System.out.println("--->>>"+s);
                trimArray[iter] = s;
                iter++;
            }
        }
        //get a new instance of a very basic custom adapter (taken from the google tutorials, mostly)
        DataAdapter dataAdapter = new DataAdapter(trimArray);
        //add the adapter to the list thing
        projectDataList.setAdapter(dataAdapter);
        //make a layout manager and add it
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        projectDataList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(projectDataList.getContext(), layoutManager.getOrientation());
        projectDataList.addItemDecoration(dividerItemDecoration);

        backButton.setOnClickListener(v -> {
            this.finish();
        });
    }


}