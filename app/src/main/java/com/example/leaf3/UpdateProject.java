package com.example.leaf3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.E;
import static java.lang.Math.log;

public class UpdateProject extends AppCompatActivity {
    Button backButton, updateButton;
    TextView projectData;
    EditText enterHours, enterUVDose;
    float currentQuantityBestOne, currentQuantityCombinedBreakdown, currentQuantityMidPoint, currentQuantityTemp, currentQuantityTempThenUV;
    float currentQuantityUV, currentQuantityUVThenTemp, currentQuantityWorstOne, growHours, uvDose, uvFen, growTemp;
    String pesticideType, projectID, coveringType, degradationRequired, longitude, latitude, formattedDate, startQuantity, daysNeeded;

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

        String[] features = dataString.split("\n");
        for(String feature : features) {
            if(feature.contains("current quantity best one")) {
                String[] labelDataPair = feature.split(" = ");
                currentQuantityBestOne = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity combined breakdown")) {
                String[] labelDataPair = feature.split(" = ");
                currentQuantityCombinedBreakdown = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity mid point")) {
                String[] labelDataPair = feature.split(" = ");
                currentQuantityMidPoint = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity temp then uv")) {
                String[] labelDataPair = feature.split(" = ");
                currentQuantityTempThenUV = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity temp")) {
                String[] labelDataPair = feature.split(" = ");
                currentQuantityTemp = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity uv then temp")) {
                String[] labelDataPair = feature.split(" = ");
                currentQuantityUVThenTemp = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity uv")) {
                String[] labelDataPair = feature.split(" = ");
                currentQuantityUV = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity worst one")) {
                String[] labelDataPair = feature.split(" = ");
                currentQuantityWorstOne = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("grow hours")) {
                String[] labelDataPair = feature.split(" = ");
                growHours = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("uv dose")) {
                String[] labelDataPair = feature.split(" = ");
                uvDose = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("grow temp")) {
                String[] labelDataPair = feature.split(" = ");
                growTemp = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("uv fen")) {
                String[] labelDataPair = feature.split(" = ");
                uvFen = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("pesticide")) {
                String[] labelDataPair = feature.split(" = ");
                pesticideType = labelDataPair[1];
            }
            else if(feature.contains("id")){
                String[] labelDataPair = feature.split(" = ");
                projectID = labelDataPair[1];
            }
            else if(feature.contains("covering")){
                String[] labelDataPair = feature.split(" = ");
                coveringType = labelDataPair[1];
            }
            else if(feature.contains("degradation required")){
                String[] labelDataPair = feature.split(" = ");
                degradationRequired = labelDataPair[1];
            }
            else if(feature.contains("longitude")){
                String[] labelDataPair = feature.split(" = ");
                longitude = labelDataPair[1];
            }
            else if(feature.contains("latitude")){
                String[] labelDataPair = feature.split(" = ");
                latitude = labelDataPair[1];
            }
            else if(feature.contains("start date")){
                String[] labelDataPair = feature.split(" = ");
                formattedDate = labelDataPair[1];
            }
            else if(feature.contains("start quantity")){
                String[] labelDataPair = feature.split(" = ");
                startQuantity = labelDataPair[1];
            }
            else if(feature.contains("days needed")){
                String[] labelDataPair = feature.split(" = ");
                daysNeeded = labelDataPair[1];
            }
        }
        projectData.setText(dataString);

        backButton.setOnClickListener(v -> this.finish());

        updateButton.setOnClickListener(v -> {
            try {
                updateProjectData();
                Intent i=new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.startActivity(i);
            } catch (Exception e) {
                Toast.makeText(UpdateProject.this, "Values must be numbers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateProjectData(){
        Map<String, Object> user = new HashMap<>();

        user.put("covering", coveringType);
        user.put("degradation_required", degradationRequired);
        user.put("latitude", latitude);
        user.put("longitude", longitude);
        user.put("pesticide", pesticideType);
        //Date c = Calendar.getInstance().getTime();
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        //String formattedDate = df.format(c);
        user.put("start_date", formattedDate);
        //Add a "last updated" date thing. Also, no harm int adding the time, helps to identify the different projects
        //Maybe need a unique ID? Might make editing files in the database easier
        //in mg/m2
        user.put("start_quantity", startQuantity);
        user.put("grow_temp", growTemp);
        //To be updated, running total
        user.put("grow_hours", (growHours + Float.parseFloat(enterHours.getText().toString())));
        user.put("uv_dose", (uvDose + Float.parseFloat(enterUVDose.getText().toString())));
        //To be updated each time new data is added
        user.put("current_quantity_uv", Math.max(0,remainingPesticideUV(currentQuantityUV,Float.parseFloat(enterUVDose.getText().toString()))));
        user.put("current_quantity_temp", Math.max(0,remainingPesticideTemp(currentQuantityTemp,Float.parseFloat(enterHours.getText().toString()), growTemp)));
        //Guesses as to how to combine the two breakdown rates:
        user.put("current_quantity_best_one", Math.max(0,Math.min(remainingPesticideUV(currentQuantityBestOne,Float.parseFloat(enterUVDose.getText().toString())),remainingPesticideTemp(currentQuantityBestOne,Float.parseFloat(enterHours.getText().toString()), growTemp))));
        user.put("current_quantity_worst_one", Math.max(0,Math.max(remainingPesticideUV(currentQuantityWorstOne,Float.parseFloat(enterUVDose.getText().toString())),remainingPesticideTemp(currentQuantityWorstOne,Float.parseFloat(enterHours.getText().toString()), growTemp))));
        user.put("current_quantity_mid_point", Math.max(0,(remainingPesticideUV(currentQuantityMidPoint,Float.parseFloat(enterUVDose.getText().toString()))+remainingPesticideTemp(currentQuantityWorstOne,Float.parseFloat(enterHours.getText().toString()), growTemp))/2));
        user.put("current_quantity_combined_breakdown", Math.max(0,remainingPesticideUV(currentQuantityCombinedBreakdown,Float.parseFloat(enterUVDose.getText().toString()))-(currentQuantityCombinedBreakdown-remainingPesticideTemp(currentQuantityCombinedBreakdown,Float.parseFloat(enterHours.getText().toString()), growTemp))));
        user.put("current_quantity_uv_then_temp", Math.max(0,remainingPesticideTemp(remainingPesticideUV(currentQuantityUVThenTemp,Float.parseFloat(enterUVDose.getText().toString())),Float.parseFloat(enterHours.getText().toString()), growTemp)));
        user.put("current_quantity_temp_then_uv", Math.max(0,remainingPesticideUV(remainingPesticideTemp(currentQuantityTempThenUV,Float.parseFloat(enterHours.getText().toString()), growTemp),Float.parseFloat(enterUVDose.getText().toString()))));

        user.put("uv_fen", uvFen);
        //Based only on UV breakdown, not temperature yet
        user.put("days_needed", daysNeeded);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("projects").
                document(projectID).
                set(user).
                addOnSuccessListener(aVoid -> Toast.makeText(UpdateProject.this, "Project has been updated.\nReturn to database to view.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(UpdateProject.this, "Fail to update the project", Toast.LENGTH_SHORT).show());
    }

    //This shouldn't need to be duplicated, you should put these functions in another class and call them from there.
    private float remainingPesticideTemp(float startConcentration, float hours, float growingTemp){
        return startConcentration - (hours * volatilisationRate(growingTemp))/1000;
    }

    private float remainingPesticideUV(float startConcentration, float givenUVDose){
        float fractionPhotodegraded = 1 - (float) (1 * Math.pow(E,-0.0009 * uvFen * givenUVDose));
        float endConcentration = startConcentration - (fractionPhotodegraded*startConcentration);
        return endConcentration;
    }

    private float volatilisationRate(float growingTemp){
        float tempInKelvin = growingTemp + (float) 273.15;
        float regressionParam1 = 0;
        float regressionParam2 = 0;
        if(pesticideType.equals("Fenitrothion")){
            regressionParam1 = (float) 6.3362;
            regressionParam2 = (float) 3197.8;
        }
        float vapourPressure = (float) Math.pow(10,(regressionParam1 - (regressionParam2/tempInKelvin)));
        float vapourPressure1mmHg = (float) vapourPressure * (float) 133.322;
        float lnVP = (float) log(vapourPressure1mmHg);
        float volatilisationRatePerHour = (float) Math.pow(E,(11.81+(0.85956*lnVP)));
        return volatilisationRatePerHour;
    }
}