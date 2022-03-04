package com.example.leaf3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteProject extends AppCompatActivity {
    Button noButton, yesButton;
    TextView projectData;
    String documentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_project);
        projectData = findViewById(R.id.project_data);
        Intent intent = getIntent();
        String dataString = intent.getStringExtra("data");
        String username = intent.getStringExtra("username");
        projectData.setText(dataString);

        if(!isNetworkAvailable()){
            Toast.makeText(DeleteProject.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        noButton = findViewById(R.id.noButton);
        noButton.setOnClickListener(v -> this.finish());

        String[] features = dataString.split("\n");
        for (String feature : features) {
            if (feature.contains("id") && !feature.contains("pesticide")) {
                String[] labelDataPair = feature.split(" = ");
                documentID = labelDataPair[1];
            }
        }

        yesButton = findViewById(R.id.yesButton);
        yesButton.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(username).
                        document(documentID)
                        .delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(DeleteProject.this, "Project has been deleted" + documentID, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(DeleteProject.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    DeleteProject.this.startActivity(i);
                }).addOnFailureListener(e -> Toast.makeText(DeleteProject.this, "Fail to delete the project", Toast.LENGTH_SHORT).show());
            } else{
                Toast.makeText(DeleteProject.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}