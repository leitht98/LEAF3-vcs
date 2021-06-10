package com.example.leaf3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class DataLog extends AppCompatActivity {
    Button backButton;
    TextView projectData;
    String dataString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_log);
        backButton = findViewById(R.id.backButton);
        projectData = findViewById(R.id.projectData);

        Intent intent = getIntent();
        dataString = intent.getStringExtra("data_string");
        projectData.setText(dataString);

        backButton.setOnClickListener(v->{
            this.finish();
        });
    }


}