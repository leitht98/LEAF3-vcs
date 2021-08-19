package com.example.leaf3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.Math.E;
import static java.lang.Math.log;
import static java.lang.Math.pow;

public class UpdateProject extends AppCompatActivity {
    Button backButton, updateButton;
    TextView projectData;
    EditText enterHours, enterUVDose;
    float currentQuantityBestOne, currentQuantityCombinedBreakdown, currentQuantityMidPoint, currentQuantityTemp, currentQuantityTempThenUV = 0;
    float currentQuantityUV, currentQuantityUVThenTemp, currentQuantityWorstOne, growHours, uvDose, uvFen, growTemp = 0;
    String pesticideType = "Fenitrothion";
    String projectID, coveringType, degradationRequired, longitude, latitude, formattedDate, startQuantity, daysNeeded = "";

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

        //System.out.println("YO");
        //System.out.println("Hiya " + dataString);
        String[] features = dataString.split("\n");
        //String outputText = "";
        for(String feature : features) {
            if(feature.contains("current quantity best one")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                currentQuantityBestOne = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity combined breakdown")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                currentQuantityCombinedBreakdown = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity mid point")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                currentQuantityMidPoint = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity temp then uv")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                currentQuantityTempThenUV = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity temp")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                currentQuantityTemp = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity uv then temp")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                currentQuantityUVThenTemp = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("current quantity uv")) {
                //System.out.println("?????????????"+feature);
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                currentQuantityUV = Float.parseFloat(labelDataPair[1]);
                //System.out.println("?????????????xxx"+currentQuantityUV);
            }
            else if(feature.contains("current quantity worst one")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                currentQuantityWorstOne = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("grow hours")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                growHours = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("uv dose")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                uvDose = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("grow temp")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                growTemp = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("uv fen")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                uvFen = Float.parseFloat(labelDataPair[1]);
            }
            else if(feature.contains("pesticide")) {
                String[] labelDataPair = feature.split(" = ");
                //outputText += labelDataPair[1]+'\n';
                //System.out.println(labelDataPair[1]);
                pesticideType = labelDataPair[1];
            }
            else if(feature.contains("id")){
                String[] labelDataPair = feature.split(" = ");
                //System.out.println(">>>>>"+labelDataPair[1]);
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
        //outputText += currentQuantityBestOne+"-"+currentQuantityCombinedBreakdown+"-"+currentQuantityMidPoint+"-"+currentQuantityTemp+"-"+currentQuantityTempThenUV+"-"+currentQuantityUV+"-"+currentQuantityUVThenTemp+"-"+currentQuantityWorstOne+"-"+growHours+"-"+uvDose;
        projectData.setText(dataString);

        backButton.setOnClickListener(v -> {
            this.finish();
        });

        updateButton.setOnClickListener(v -> {
            //System.out.println("Hey, I'm updating the database.");
            try {
                updateProjectData();
                //this.finish();
                Intent i=new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.startActivity(i);
            } catch (Exception e) {
                Toast.makeText(UpdateProject.this, "Values must be numbers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateProjectData(){

        //float testRemainingAfterUV = remainingPesticideUV(currentQuantityUV,Float.parseFloat(enterUVDose.getText().toString()));
        //float testRemainingAfterTemp = remainingPesticideTemp(currentQuantityTemp,Float.parseFloat(enterHours.getText().toString()), growTemp);

        //Add a "last updated" date thing. Also, no harm int adding the time, helps to identify the different projects
        //Maybe need a unique ID? Might make editing files in the database easier

        //To be updated, running total
        System.out.println("grow_hours"+ (growHours + Float.parseFloat(enterHours.getText().toString())));
        System.out.println("uv_dose"+ (uvDose + Float.parseFloat(enterUVDose.getText().toString())));
        //To be updated each time new data is added
        //System.out.println("!!!!!!!!"+currentQuantityUV);
        System.out.println("current_quantity_uv"+ Math.max(0,remainingPesticideUV(currentQuantityUV,Float.parseFloat(enterUVDose.getText().toString()))));
        //System.out.println("current_quantity_temp"+ Math.max(0,remainingPesticideTemp(currentQuantityTemp,Float.parseFloat(enterHours.getText().toString()), growTemp)));
        System.out.println("\ncurrent_quantity_temp"+ remainingPesticideTemp(currentQuantityTemp,Float.parseFloat(enterHours.getText().toString()), growTemp)+'\n');
        //System.out.println("current quantity temp: "+currentQuantityTemp);
        //System.out.println("entered hours: "+enterHours.getText().toString());
        //System.out.println("grow temp: "+growTemp+'\n');
        //Guesses as to how to combine the two breakdown rates:


        //Something seems funny here, why aren't best/ worst ones matching with the uv/ temp ones? more testing required but think we're close enough for now.
        System.out.println("current_quantity_best_one"+ Math.max(0,Math.min(remainingPesticideUV(currentQuantityBestOne,Float.parseFloat(enterUVDose.getText().toString())),remainingPesticideTemp(currentQuantityBestOne,Float.parseFloat(enterHours.getText().toString()), growTemp))));
        System.out.println("current_quantity_worst_one"+Math.max(0,Math.max(remainingPesticideUV(currentQuantityWorstOne,Float.parseFloat(enterUVDose.getText().toString())),remainingPesticideTemp(currentQuantityWorstOne,Float.parseFloat(enterHours.getText().toString()), growTemp))));
        System.out.println("current_quantity_mid_point"+Math.max(0,(remainingPesticideUV(currentQuantityMidPoint,Float.parseFloat(enterUVDose.getText().toString()))+remainingPesticideTemp(currentQuantityWorstOne,Float.parseFloat(enterHours.getText().toString()), growTemp))/2));
        System.out.println("current_quantity_combined_breakdown"+Math.max(0,remainingPesticideUV(currentQuantityCombinedBreakdown,Float.parseFloat(enterUVDose.getText().toString()))-(currentQuantityCombinedBreakdown-remainingPesticideTemp(currentQuantityCombinedBreakdown,Float.parseFloat(enterHours.getText().toString()), growTemp))));
        System.out.println("current_quantity_uv_then_temp"+Math.max(0,remainingPesticideTemp(remainingPesticideUV(currentQuantityUVThenTemp,Float.parseFloat(enterUVDose.getText().toString())),Float.parseFloat(enterHours.getText().toString()), growTemp)));
        System.out.println("current_quantity_temp_then_uv"+Math.max(0,remainingPesticideUV(remainingPesticideTemp(currentQuantityTempThenUV,Float.parseFloat(enterHours.getText().toString()), growTemp),Float.parseFloat(enterUVDose.getText().toString()))));

        //String testDocumentID = "9ouCIT5TYcN3KmizEjLu";
        Map<String, Object> user = new HashMap<>();

        //user.put("covering", "Look! It changed!");

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
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // on successful completion of this process
                        // we are displaying the toast message.
                        Toast.makeText(UpdateProject.this, "Project has been updated.\nReturn to database to view.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            // inside on failure method we are
            // displaying a failure message.
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProject.this, "Fail to update the project", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //This shouldn't need to be duplicated, you should put these functions in another class and call them from there.
    private float remainingPesticideTemp(float startConcentration, float hours, float growingTemp){
        //System.out.println("\n>"+startConcentration);
        //System.out.println(">"+hours);
        //System.out.println(">"+growingTemp);
        //System.out.println(">>"+volatilisationRate(growingTemp));
        //System.out.println(">>>"+(startConcentration - (hours * volatilisationRate(growingTemp))/1000)+'\n');
        return startConcentration - (hours * volatilisationRate(growingTemp))/1000;
    }

    private float remainingPesticideUV(float startConcentration, float givenUVDose){
        //System.out.println("\n>"+startConcentration);
        //System.out.println(">"+givenUVDose);

        float fractionPhotodegraded = 1 - (float) (1 * Math.pow(E,-0.0009 * uvFen * givenUVDose));
        //System.out.println("Fraction: "+fractionPhotodegraded);
        float endConcentration = startConcentration - (fractionPhotodegraded*startConcentration);
        //System.out.println("Concentration left: "+endConcentration);
        //System.out.println(">>"+fractionPhotodegraded);
        //System.out.println(">>>"+endConcentration);
        return endConcentration;
    }

    private float volatilisationRate(float growingTemp){
        //Assumption, maybe allow user to input? Are greenhouses temperature controlled?
        //int growingTemp = 20;
        float tempInKelvin = growingTemp + (float) 273.15;
        float regressionParam1 = 0;
        float regressionParam2 = 0;
        //System.out.println("???"+pesticideType);
        if(pesticideType.equals("Fenitrothion")){
            regressionParam1 = (float) 6.3362;
            regressionParam2 = (float) 3197.8;
        }
        //System.out.println("!!!!!"+regressionParam1);
        float vapourPressure = (float) Math.pow(10,(regressionParam1 - (regressionParam2/tempInKelvin)));
        float vapourPressure1mmHg = (float) vapourPressure * (float) 133.322;
        float lnVP = (float) log(vapourPressure1mmHg);
        float volatilisationRatePerHour = (float) Math.pow(E,(11.81+(0.85956*lnVP)));
        return volatilisationRatePerHour;
    }
}