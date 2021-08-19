package com.example.leaf3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteProject extends AppCompatActivity {
    Button noButton, yesButton;
    TextView projectData;
    String documentID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_project);
        projectData = findViewById(R.id.project_data);
        Intent intent = getIntent();
        String dataString = intent.getStringExtra("data");
        projectData.setText(dataString);
        noButton = findViewById(R.id.noButton);
        noButton.setOnClickListener(v -> {
            this.finish();
        });

        //get document id
        String[] features = dataString.split("\n");
        for (String feature : features) {
            if (feature.contains("id") && !feature.contains("pesticide")) {
                String[] labelDataPair = feature.split(" = ");
                documentID = labelDataPair[1];
            }
        }

        yesButton = findViewById(R.id.yesButton);
        yesButton.setOnClickListener(v -> {
            //System.out.println("So now I'd delete the project.");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("projects").
                    document(documentID)
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // on successful completion of this process
                            // we are displaying the toast message.
                            Toast.makeText(DeleteProject.this, "Project has been deleted", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(DeleteProject.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            DeleteProject.this.startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        // inside on failure method we are
                        // displaying a failure message.
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DeleteProject.this, "Fail to delete the project", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}