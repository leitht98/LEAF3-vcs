package com.example.leaf3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateProject extends AppCompatActivity {
    Button backButton, updateButton;
    TextView projectData;
    EditText enterHours, enterUVDose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_project);
        backButton = findViewById(R.id.backButton);
        updateButton = findViewById(R.id.updateButton);
        projectData = findViewById(R.id.project_data);
        enterHours = findViewById(R.id.enterHours);
        enterUVDose = findViewById(R.id.enterUVDose);

        Intent intent = getIntent();
        String dataString = intent.getStringExtra("data");
        String username = intent.getStringExtra("username");
        Project project = new Project();
        project.loadProjectData(dataString);

        projectData.setText(dataString);

        backButton.setOnClickListener(v -> this.finish());

        updateButton.setOnClickListener(v -> {
            try {
                project.updateProjectData(Float.parseFloat(enterHours.getText().toString()),Float.parseFloat(enterUVDose.getText().toString()),username);
                Intent i=new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.startActivity(i);
                Toast.makeText(UpdateProject.this, "Project has been updated.\nReturn to database to view.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(UpdateProject.this, "Values must be numbers", Toast.LENGTH_SHORT).show();
            }
        });
    }
}