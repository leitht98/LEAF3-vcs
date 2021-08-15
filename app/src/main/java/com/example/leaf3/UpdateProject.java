package com.example.leaf3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class UpdateProject extends AppCompatActivity {
    Button backButton;
    TextView projectData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_project);
        backButton = findViewById(R.id.backButton);
        projectData = findViewById(R.id.project_data);

        Intent intent = getIntent();
        String dataString = intent.getStringExtra("data");

        //System.out.println("YO");
        //System.out.println("Hiya " + dataString);
        projectData.setText(dataString);

        backButton.setOnClickListener(v -> {
            this.finish();
        });
    }
}