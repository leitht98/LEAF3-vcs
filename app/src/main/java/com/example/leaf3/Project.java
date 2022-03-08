package com.example.leaf3;

import com.google.firebase.firestore.FirebaseFirestore;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static java.lang.Math.E;
import static java.lang.Math.log;

public class Project {
    BigDecimal currentQuantityBestOne, currentQuantityCombinedBreakdown, currentQuantityMidPoint, currentQuantityTemp, currentQuantityTempThenUV;
    BigDecimal currentQuantityUV, currentQuantityUVThenTemp, currentQuantityWorstOne, degradationRequired, growHours, uvDose, startQuantity, uvFen, uvRate, growTemp;
    BigDecimal regressionParam1, regressionParam2;
    String pesticideType, projectID, coveringType, formattedDate;
    //String resultOutput;
    //int daysNeeded;
    FirebaseFirestore db;

    public Project() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveToDatabase(BigDecimal enteredStartQuantity, BigDecimal enteredUVDose, BigDecimal enteredHours, BigDecimal enteredGrowTemp, BigDecimal enteredDegradation, String enteredCovering, String enteredPesticide, BigDecimal enteredUVFen, BigDecimal enteredUVRate, BigDecimal pesticideRP1, BigDecimal pesticideRP2, String username){
        growTemp = enteredGrowTemp;
        coveringType = enteredCovering;
        //latitude = enteredLatitude;
        //longitude = enteredLongitude;
        pesticideType = enteredPesticide;
        uvFen = enteredUVFen;
        uvRate = enteredUVRate;
        //resultOutput = enteredResultOutput;
        startQuantity = enteredStartQuantity;
        degradationRequired = enteredDegradation;
        growHours = enteredHours;
        uvDose = enteredUVDose;
        regressionParam1 = pesticideRP1;
        regressionParam2 = pesticideRP2;

        BigDecimal testRemainingAfterUV = remainingPesticideUV(enteredStartQuantity, enteredUVDose);
        BigDecimal testRemainingAfterTemp = remainingPesticideTemp(enteredStartQuantity, enteredHours);

        currentQuantityUV = testRemainingAfterUV.max(BigDecimal.valueOf(0));
        currentQuantityTemp = testRemainingAfterTemp.max(BigDecimal.valueOf(0));

        //Guesses as to how to combine the two breakdown rates:
        currentQuantityBestOne = BigDecimal.valueOf(0).max(testRemainingAfterUV.min(testRemainingAfterTemp));
        currentQuantityWorstOne = BigDecimal.valueOf(0).max(testRemainingAfterUV.max(testRemainingAfterTemp));
        currentQuantityMidPoint = BigDecimal.valueOf(0).max((testRemainingAfterUV.add(testRemainingAfterTemp)).divide(BigDecimal.valueOf(2)));
        currentQuantityCombinedBreakdown = BigDecimal.valueOf(0).max(testRemainingAfterUV.subtract(enteredStartQuantity.subtract(testRemainingAfterTemp)));
        currentQuantityUVThenTemp = BigDecimal.valueOf(0).max(remainingPesticideTemp(testRemainingAfterUV, enteredHours));
        currentQuantityTempThenUV = BigDecimal.valueOf(0).max(remainingPesticideUV(testRemainingAfterTemp, enteredUVDose));


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        formattedDate = df.format(c);

        //daysNeeded = Integer.parseInt(resultOutput.substring(15));

        Map<String, Object> user = sendToDatabase();

        db.collection(username)
                .add(user);
    }

    public void updateProjectData(BigDecimal enteredHours,BigDecimal enteredUVDose, String username){
        growHours = growHours.add(enteredHours);
        uvDose = uvDose.add(enteredUVDose);

        currentQuantityUV = remainingPesticideUV(currentQuantityUV,enteredUVDose).max(BigDecimal.valueOf(0));
        currentQuantityTemp = remainingPesticideTemp(currentQuantityTemp,enteredHours).max(BigDecimal.valueOf(0));

        //Guesses as to how to combine the two breakdown rates:
        currentQuantityBestOne = BigDecimal.valueOf(0).max(remainingPesticideUV(currentQuantityBestOne,enteredUVDose).min(remainingPesticideTemp(currentQuantityBestOne,enteredHours)));
        currentQuantityWorstOne = BigDecimal.valueOf(0).max(remainingPesticideUV(currentQuantityWorstOne,enteredUVDose).min(remainingPesticideTemp(currentQuantityWorstOne,enteredHours)));
        currentQuantityMidPoint = BigDecimal.valueOf(0).max((remainingPesticideUV(currentQuantityMidPoint,enteredUVDose).add(remainingPesticideTemp(currentQuantityMidPoint,enteredHours))).divide(BigDecimal.valueOf(2)));
        currentQuantityCombinedBreakdown = BigDecimal.valueOf(0).max(remainingPesticideUV(currentQuantityCombinedBreakdown,enteredUVDose).subtract(currentQuantityCombinedBreakdown.subtract(remainingPesticideTemp(currentQuantityCombinedBreakdown,enteredHours))));
        currentQuantityUVThenTemp = BigDecimal.valueOf(0).max(remainingPesticideTemp(remainingPesticideUV(currentQuantityUVThenTemp,enteredUVDose),enteredHours));
        currentQuantityTempThenUV = BigDecimal.valueOf(0).max(remainingPesticideUV(remainingPesticideTemp(currentQuantityTempThenUV,enteredHours),enteredUVDose));


        Map<String, Object> user = sendToDatabase();
        db.collection(username).
                document(projectID).
                set(user);
    }

    public void loadProjectData(String dataString){
        String[] features = dataString.split("\n");
        for(String feature : features) {
            String[] labelDataPair = feature.split(" = ");
            switch (labelDataPair[0]) {
                case "current quantity best one": currentQuantityBestOne = new BigDecimal(labelDataPair[1]); break;
                case "current quantity combined breakdown": currentQuantityCombinedBreakdown = new BigDecimal(labelDataPair[1]); break;
                case "current quantity mid point": currentQuantityMidPoint = new BigDecimal(labelDataPair[1]); break;
                case "current quantity temp then uv": currentQuantityTempThenUV = new BigDecimal(labelDataPair[1]); break;
                case "current quantity temp": currentQuantityTemp = new BigDecimal(labelDataPair[1]); break;
                case "current quantity uv then temp": currentQuantityUVThenTemp = new BigDecimal(labelDataPair[1]); break;
                case "current quantity uv": currentQuantityUV = new BigDecimal(labelDataPair[1]); break;
                case "current quantity worst one": currentQuantityWorstOne = new BigDecimal(labelDataPair[1]); break;
                case "grow hours": growHours = new BigDecimal(labelDataPair[1]); break;
                case "uv dose": uvDose = new BigDecimal(labelDataPair[1]); break;
                case "grow temp": growTemp = new BigDecimal(labelDataPair[1]); break;
                case "uv fen": uvFen = new BigDecimal(labelDataPair[1]); break;
                case "uv rate": uvRate = new BigDecimal(labelDataPair[1]); break;
                case "pesticide": pesticideType = labelDataPair[1]; break;
                case "id": projectID = labelDataPair[1]; break;
                case "covering": coveringType = labelDataPair[1]; break;
                case "degradation required": degradationRequired = new BigDecimal(labelDataPair[1]); break;
                //case "longitude": longitude = labelDataPair[1]; break;
                //case "latitude": latitude = labelDataPair[1]; break;
                case "start date": formattedDate = labelDataPair[1]; break;
                case "start quantity": startQuantity = new BigDecimal(labelDataPair[1]); break;
                //case "days needed": daysNeeded = Integer.parseInt(labelDataPair[1]); break;
                case "regression parameter 1": regressionParam1 = new BigDecimal(labelDataPair[1]); break;
                case "regression parameter 2": regressionParam2 = new BigDecimal(labelDataPair[1]); break;
                default: break;
            }
        }
    }

    private Map<String, Object> sendToDatabase(){
        Map<String, Object> user = new HashMap<>();

        //Think I've got to add a .toString to the BigDecimals because I had to with the admin app, worth testing later though
        user.put("covering", coveringType);
        user.put("degradation_required", degradationRequired.toString());
        //user.put("latitude", latitude);
        //user.put("longitude", longitude);
        user.put("pesticide", pesticideType);
        user.put("start_date", formattedDate);

        //Add a "last updated" date thing. Also, no harm int adding the time, helps to identify the different projects

        //in mg/m2
        user.put("start_quantity", startQuantity.toString());

        user.put("grow_temp", growTemp.toString());

        //To be updated, running total
        user.put("grow_hours", growHours.toString());
        user.put("uv_dose", uvDose.toString());

        //To be updated each time new data is added
        user.put("current_quantity_uv", currentQuantityUV.toString());
        user.put("current_quantity_temp", currentQuantityTemp.toString());

        //Guesses as to how to combine the two breakdown rates:
        user.put("current_quantity_best_one", currentQuantityBestOne.toString());
        user.put("current_quantity_worst_one", currentQuantityWorstOne.toString());
        user.put("current_quantity_mid_point", currentQuantityMidPoint.toString());
        user.put("current_quantity_combined_breakdown", currentQuantityCombinedBreakdown.toString());
        user.put("current_quantity_uv_then_temp", currentQuantityUVThenTemp.toString());
        user.put("current_quantity_temp_then_uv", currentQuantityTempThenUV.toString());

        user.put("uv_fen", uvFen.toString());
        user.put("uv_rate",uvRate.toString());

        user.put("regression parameter 1", regressionParam1.toString());
        user.put("regression parameter 2", regressionParam2.toString());

        //Based only on UV breakdown, not temperature yet
        //user.put("days_needed", daysNeeded);
        return user;
    }

    private BigDecimal remainingPesticideTemp(BigDecimal startConcentration, BigDecimal hours){
        return startConcentration.subtract(hours.multiply(volatilisationRate().divide(BigDecimal.valueOf(1000))));
    }

    private BigDecimal remainingPesticideUV(BigDecimal startConcentration, BigDecimal givenUVDose){
        //Okay, can't use .pow() from BigDecimal because you can only raise by integers, so might have to use doubles. This could also be fucked
        //There are going to be so many mistakes, god if all the numbers are right first go it'll be a miracle
        BigDecimal fractionPhotodegraded = BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(1).multiply(BigDecimal.valueOf(Math.pow(E,-0.0009*uvFen.doubleValue()*givenUVDose.doubleValue()))));
        return startConcentration.subtract(fractionPhotodegraded.multiply(startConcentration));
    }

    //This will need changing, pass RPs to the class, don't find them here
    private BigDecimal volatilisationRate(){
        BigDecimal tempInKelvin = growTemp.add(BigDecimal.valueOf(273.15));
        //I'm not exactly clear why this division broke it, the others seem fine but I'm worried they'll break if I can't figure out this one
        BigDecimal vapourPressureP1 = BigDecimal.valueOf(regressionParam2.doubleValue()/tempInKelvin.doubleValue());
        BigDecimal vapourPressureP2 = regressionParam1.subtract(vapourPressureP1);
        BigDecimal vapourPressure = BigDecimal.valueOf(Math.pow(10,vapourPressureP2.doubleValue()));
        BigDecimal vapourPressure1mmHg = vapourPressure.multiply(BigDecimal.valueOf(133.322));
        //Bit janky, maybe try to improve?
        BigDecimal lnVP = BigDecimal.valueOf(log(vapourPressure1mmHg.doubleValue()));
        //Also pretty janky
        return BigDecimal.valueOf(Math.pow(E,BigDecimal.valueOf(11.81).add(BigDecimal.valueOf(0.85956).multiply(lnVP)).doubleValue()));
        //Why are all these values hardcoded?? Are they to do with which pesticide or covering you use? Really need to know what these are.
        //Even if they aren't ones that would change, we should at least name them, that can be the boring task for tomorrow
    }
}
