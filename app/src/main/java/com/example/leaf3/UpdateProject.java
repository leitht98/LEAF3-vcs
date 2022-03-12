package com.example.leaf3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigDecimal;

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

        enterHours.setOnClickListener(v -> enterHours.getText().clear());
        enterUVDose.setOnClickListener(v -> enterUVDose.getText().clear());

        Intent intent = getIntent();
        String dataString = intent.getStringExtra("data");
        String username = intent.getStringExtra("username");
        Project project = new Project();
        project.loadProjectData(dataString);

        projectData.setText(dataString);

        if(!isNetworkAvailable()){
            Toast.makeText(UpdateProject.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        backButton.setOnClickListener(v -> this.finish());

        updateButton.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                try {
                    //Changed from floats
                    project.updateProjectData(new BigDecimal(enterHours.getText().toString()), new BigDecimal(enterUVDose.getText().toString()), username);
                    Intent i = new Intent(this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    this.startActivity(i);
                    Toast.makeText(UpdateProject.this, "Project has been updated.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {Toast.makeText(UpdateProject.this, "Values must be numbers", Toast.LENGTH_SHORT).show();}
            } else{Toast.makeText(UpdateProject.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}