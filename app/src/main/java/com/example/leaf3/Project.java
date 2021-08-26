package com.example.leaf3;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.Math.E;
import static java.lang.Math.log;

public class Project {
    float currentQuantityBestOne, currentQuantityCombinedBreakdown, currentQuantityMidPoint, currentQuantityTemp, currentQuantityTempThenUV;
    float currentQuantityUV, currentQuantityUVThenTemp, currentQuantityWorstOne, degradationRequired, growHours, additionalHours, uvDose, additionalUVDose, startQuantity, uvFen, growTemp;
    String pesticideType, projectID, coveringType, longitude, latitude, formattedDate, resultOutput;
    int daysNeeded;
    FirebaseFirestore db;

    public Project() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveToDatabase(float enteredStartQuantity, float enteredUVDose, float enteredHours, float enteredGrowTemp, float enteredDegradation, String enteredResultOutput, String enteredCovering, String enteredLatitude, String enteredLongitude, String enteredPesticide, float enteredUVFen){
        growTemp = enteredGrowTemp;
        coveringType = enteredCovering;
        latitude = enteredLatitude;
        longitude = enteredLongitude;
        pesticideType = enteredPesticide;
        uvFen = enteredUVFen;
        resultOutput = enteredResultOutput;
        startQuantity = enteredStartQuantity;
        degradationRequired = enteredDegradation;
        growHours = enteredHours;
        //additionalHours = enteredHours;
        uvDose = enteredUVDose;
        //additionalUVDose = enteredUVDose;

        float testRemainingAfterUV = remainingPesticideUV(enteredStartQuantity, enteredUVDose);
        float testRemainingAfterTemp = remainingPesticideTemp(enteredStartQuantity, enteredHours);

        currentQuantityUV = Math.max(0, testRemainingAfterUV);
        currentQuantityTemp = Math.max(0, testRemainingAfterTemp);

        //Guesses as to how to combine the two breakdown rates:
        currentQuantityBestOne = Math.max(0, Math.min(testRemainingAfterUV, testRemainingAfterTemp));
        currentQuantityWorstOne = Math.max(0, Math.max(testRemainingAfterUV, testRemainingAfterTemp));
        currentQuantityMidPoint = Math.max(0, (testRemainingAfterUV + testRemainingAfterTemp) / 2);
        currentQuantityCombinedBreakdown = Math.max(0, testRemainingAfterUV - (enteredStartQuantity - testRemainingAfterTemp));
        currentQuantityUVThenTemp = Math.max(0, remainingPesticideTemp(testRemainingAfterUV, enteredHours));
        currentQuantityTempThenUV = Math.max(0, remainingPesticideUV(testRemainingAfterTemp, enteredUVDose));

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        formattedDate = df.format(c);

        daysNeeded = Integer.parseInt(resultOutput.substring(15));

        Map<String, Object> user = sendToDatabase();

        db.collection("projects")
                .add(user);
    }

    public void updateProjectData(float enteredHours,float enteredUVDose){
        growHours = growHours + enteredHours;
        uvDose = uvDose + enteredUVDose;
        //additionalHours = enteredHours;

        currentQuantityUV = Math.max(0,remainingPesticideUV(currentQuantityUV,enteredUVDose));
        currentQuantityTemp = Math.max(0,remainingPesticideTemp(currentQuantityTemp,enteredHours));

        //Guesses as to how to combine the two breakdown rates:
        currentQuantityBestOne = Math.max(0,Math.min(remainingPesticideUV(currentQuantityBestOne,enteredUVDose),remainingPesticideTemp(currentQuantityBestOne,enteredHours)));
        currentQuantityWorstOne = Math.max(0,Math.max(remainingPesticideUV(currentQuantityWorstOne,enteredUVDose),remainingPesticideTemp(currentQuantityWorstOne,enteredHours)));
        //!!! Doesn't match what it used to say but I think this is actually right.
        currentQuantityMidPoint = Math.max(0,(remainingPesticideUV(currentQuantityMidPoint,enteredUVDose)+remainingPesticideTemp(currentQuantityMidPoint,enteredHours))/2);
        currentQuantityCombinedBreakdown = Math.max(0,remainingPesticideUV(currentQuantityCombinedBreakdown,enteredUVDose)-(currentQuantityCombinedBreakdown-remainingPesticideTemp(currentQuantityCombinedBreakdown,enteredHours)));
        currentQuantityUVThenTemp = Math.max(0,remainingPesticideTemp(remainingPesticideUV(currentQuantityUVThenTemp,enteredUVDose),enteredHours));
        currentQuantityTempThenUV = Math.max(0,remainingPesticideUV(remainingPesticideTemp(currentQuantityTempThenUV,enteredHours),enteredUVDose));

        Map<String, Object> user = sendToDatabase();
        db.collection("projects").
                document(projectID).
                set(user);
    }

    public void loadProjectData(String dataString){
        String[] features = dataString.split("\n");
        for(String feature : features) {
            String[] labelDataPair = feature.split(" = ");
            switch ((String) labelDataPair[0]) {
                case "current quantity best one": currentQuantityBestOne = Float.parseFloat(labelDataPair[1]); break;
                case "current quantity combined breakdown": currentQuantityCombinedBreakdown = Float.parseFloat(labelDataPair[1]); break;
                case "current quantity mid point": currentQuantityMidPoint = Float.parseFloat(labelDataPair[1]); break;
                case "current quantity temp then uv": currentQuantityTempThenUV = Float.parseFloat(labelDataPair[1]); break;
                case "current quantity temp": currentQuantityTemp = Float.parseFloat(labelDataPair[1]); break;
                case "current quantity uv then temp": currentQuantityUVThenTemp = Float.parseFloat(labelDataPair[1]); break;
                case "current quantity uv": currentQuantityUV = Float.parseFloat(labelDataPair[1]); break;
                case "current quantity worst one": currentQuantityWorstOne = Float.parseFloat(labelDataPair[1]); break;
                case "grow hours": growHours = Float.parseFloat(labelDataPair[1]); break;
                case "uv dose": uvDose = Float.parseFloat(labelDataPair[1]); break;
                case "grow temp": growTemp = Float.parseFloat(labelDataPair[1]); break;
                case "uv fen": uvFen = Float.parseFloat(labelDataPair[1]); break;
                case "pesticide": pesticideType = labelDataPair[1]; break;
                case "id": projectID = labelDataPair[1]; break;
                case "covering": coveringType = labelDataPair[1]; break;
                case "degradation required": degradationRequired = Float.parseFloat(labelDataPair[1]); break;
                case "longitude": longitude = labelDataPair[1]; break;
                case "latitude": latitude = labelDataPair[1]; break;
                case "start date": formattedDate = labelDataPair[1]; break;
                case "start quantity": startQuantity = Float.parseFloat(labelDataPair[1]); break;
                case "days needed": daysNeeded = Integer.parseInt(labelDataPair[1]); break;
                default: break;
            }
        }
    }

    private Map<String, Object> sendToDatabase(){
        Map<String, Object> user = new HashMap<>();

        user.put("covering", coveringType);
        user.put("degradation_required", degradationRequired);
        user.put("latitude", latitude);
        user.put("longitude", longitude);
        user.put("pesticide", pesticideType);
        user.put("start_date", formattedDate);

        //Add a "last updated" date thing. Also, no harm int adding the time, helps to identify the different projects

        //in mg/m2
        user.put("start_quantity", startQuantity);

        user.put("grow_temp", growTemp);

        //To be updated, running total
        user.put("grow_hours", growHours);
        user.put("uv_dose", uvDose);

        //To be updated each time new data is added
        user.put("current_quantity_uv", currentQuantityUV);
        user.put("current_quantity_temp", currentQuantityTemp);

        //Guesses as to how to combine the two breakdown rates:
        user.put("current_quantity_best_one", currentQuantityBestOne);
        user.put("current_quantity_worst_one", currentQuantityWorstOne);
        user.put("current_quantity_mid_point", currentQuantityMidPoint);
        user.put("current_quantity_combined_breakdown", currentQuantityCombinedBreakdown);
        user.put("current_quantity_uv_then_temp", currentQuantityUVThenTemp);
        user.put("current_quantity_temp_then_uv", currentQuantityTempThenUV);

        user.put("uv_fen", uvFen);

        //Based only on UV breakdown, not temperature yet
        user.put("days_needed", daysNeeded);


        return user;
    }

    private float remainingPesticideTemp(float startConcentration, float hours){
        return startConcentration - (hours * volatilisationRate())/1000;
    }

    private float remainingPesticideUV(float startConcentration, float givenUVDose){
        float fractionPhotodegraded = 1 - (float) (1 * Math.pow(E,-0.0009 * uvFen * givenUVDose));
        return startConcentration - (fractionPhotodegraded*startConcentration);
    }

    private float volatilisationRate(){
        float tempInKelvin = growTemp + (float) 273.15;
        float regressionParam1 = 0;
        float regressionParam2 = 0;
        if(pesticideType.equals("Fenitrothion")){
            regressionParam1 = (float) 6.3362;
            regressionParam2 = (float) 3197.8;
        }
        float vapourPressure = (float) Math.pow(10,(regressionParam1 - (regressionParam2/tempInKelvin)));
        float vapourPressure1mmHg = (float) vapourPressure * (float) 133.322;
        float lnVP = (float) log(vapourPressure1mmHg);
        return (float) Math.pow(E,(11.81+(0.85956*lnVP)));
    }
}
