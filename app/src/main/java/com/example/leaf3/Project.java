package com.example.leaf3;

import com.google.firebase.firestore.FirebaseFirestore;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    String pesticideType, projectID, coveringType, formattedStartDate, formattedUpdateDate;
    //String resultOutput;
    //int daysNeeded;
    FirebaseFirestore db;

    //Values:
    BigDecimal zeroInKelvin = BigDecimal.valueOf(273.15);
    BigDecimal mmHgToPaScaleFactor = BigDecimal.valueOf(133.322);


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
        currentQuantityMidPoint = BigDecimal.valueOf(0).max((testRemainingAfterUV.add(testRemainingAfterTemp)).divide(BigDecimal.valueOf(2),15, RoundingMode.HALF_UP));
        currentQuantityCombinedBreakdown = BigDecimal.valueOf(0).max(testRemainingAfterUV.subtract(enteredStartQuantity.subtract(testRemainingAfterTemp)));
        currentQuantityUVThenTemp = BigDecimal.valueOf(0).max(remainingPesticideTemp(testRemainingAfterUV, enteredHours));
        currentQuantityTempThenUV = BigDecimal.valueOf(0).max(remainingPesticideUV(testRemainingAfterTemp, enteredUVDose));


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        formattedStartDate = df.format(c);
        formattedUpdateDate = "N/A";

        //daysNeeded = Integer.parseInt(resultOutput.substring(15));

        Map<String, Object> user = sendToDatabase();

        db.collection(username)
                .add(user);
    }

    public void updateProjectData(BigDecimal enteredHours,BigDecimal enteredUVDose, String username){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        formattedUpdateDate = df.format(c);

        growHours = growHours.add(enteredHours);
        uvDose = uvDose.add(enteredUVDose);

        currentQuantityUV = remainingPesticideUV(currentQuantityUV,enteredUVDose).max(BigDecimal.valueOf(0));
        currentQuantityTemp = remainingPesticideTemp(currentQuantityTemp,enteredHours).max(BigDecimal.valueOf(0));

        //Guesses as to how to combine the two breakdown rates:
        currentQuantityBestOne = BigDecimal.valueOf(0).max(remainingPesticideUV(currentQuantityBestOne,enteredUVDose).min(remainingPesticideTemp(currentQuantityBestOne,enteredHours)));
        currentQuantityWorstOne = BigDecimal.valueOf(0).max(remainingPesticideUV(currentQuantityWorstOne,enteredUVDose).min(remainingPesticideTemp(currentQuantityWorstOne,enteredHours)));
        currentQuantityMidPoint = BigDecimal.valueOf(0).max((remainingPesticideUV(currentQuantityMidPoint,enteredUVDose).add(remainingPesticideTemp(currentQuantityMidPoint,enteredHours))).divide(BigDecimal.valueOf(2),15, RoundingMode.HALF_UP));
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
                case "01. covering": coveringType = labelDataPair[1]; break;
                case "02. uv fen": uvFen = new BigDecimal(labelDataPair[1]); break;
                case "03. uv rate": uvRate = new BigDecimal(labelDataPair[1]); break;
                case "04. pesticide": pesticideType = labelDataPair[1]; break;
                case "05. regression parameter 1": regressionParam1 = new BigDecimal(labelDataPair[1]); break;
                case "06. regression parameter 2": regressionParam2 = new BigDecimal(labelDataPair[1]); break;
                //case "longitude": longitude = labelDataPair[1]; break;
                //case "latitude": latitude = labelDataPair[1]; break;
                //case "days needed": daysNeeded = Integer.parseInt(labelDataPair[1]); break;
                case "07. start date": formattedStartDate = labelDataPair[1]; break;
                //"8. last updated"
                case "08. last update": formattedUpdateDate = labelDataPair[1]; break;
                case "09. start quantity": startQuantity = new BigDecimal(labelDataPair[1]); break;
                case "10. degradation required": degradationRequired = new BigDecimal(labelDataPair[1]); break;
                case "11. grow temp": growTemp = new BigDecimal(labelDataPair[1]); break;
                case "12. grow hours": growHours = new BigDecimal(labelDataPair[1]); break;
                case "13. uv dose": uvDose = new BigDecimal(labelDataPair[1]); break;
                case "14. current quantity temp": currentQuantityTemp = new BigDecimal(labelDataPair[1]); break;
                case "15. current quantity uv": currentQuantityUV = new BigDecimal(labelDataPair[1]); break;
                case "16. current quantity best one": currentQuantityBestOne = new BigDecimal(labelDataPair[1]); break;
                case "17. current quantity worst one": currentQuantityWorstOne = new BigDecimal(labelDataPair[1]); break;
                case "18. current quantity mid point": currentQuantityMidPoint = new BigDecimal(labelDataPair[1]); break;
                case "19. current quantity combined breakdown": currentQuantityCombinedBreakdown = new BigDecimal(labelDataPair[1]); break;
                case "20. current quantity temp then uv": currentQuantityTempThenUV = new BigDecimal(labelDataPair[1]); break;
                case "21. current quantity uv then temp": currentQuantityUVThenTemp = new BigDecimal(labelDataPair[1]); break;
                case "id": projectID = labelDataPair[1]; break;
                default: break;
            }
        }
    }

    private Map<String, Object> sendToDatabase(){
        Map<String, Object> user = new HashMap<>();

        //Think I've got to add a .toString to the BigDecimals because I had to with the admin app, worth testing later though
        user.put("01. covering", coveringType);
        user.put("02. uv fen", uvFen.toString());
        user.put("03. uv rate",uvRate.toString());
        user.put("04. pesticide", pesticideType);
        user.put("05. regression parameter 1", regressionParam1.toString());
        user.put("06. regression parameter 2", regressionParam2.toString());
        //user.put("latitude", latitude);
        //user.put("longitude", longitude);
        //Based only on UV breakdown, not temperature yet
        //user.put("days needed", daysNeeded);
        user.put("07. start date", formattedStartDate);
        //"8. last updated" Also, no harm in adding the time, helps to identify the different projects
        user.put("08. last update", formattedUpdateDate);
        //in mg/m2
        user.put("09. start quantity", startQuantity.toString());
        user.put("10. degradation required", degradationRequired.toString());
        user.put("11. grow temp", growTemp.toString());
        //To be updated, running total
        user.put("12. grow hours", growHours.toString());
        user.put("13. uv dose", uvDose.toString());
        //To be updated each time new data is added
        user.put("14. current quantity temp", currentQuantityTemp.toString());
        user.put("15. current quantity uv", currentQuantityUV.toString());
        //Guesses as to how to combine the two breakdown rates:
        user.put("16. current quantity best one", currentQuantityBestOne.toString());
        user.put("17. current quantity worst one", currentQuantityWorstOne.toString());
        user.put("18. current quantity mid point", currentQuantityMidPoint.toString());
        user.put("19. current quantity combined breakdown", currentQuantityCombinedBreakdown.toString());
        user.put("20. current quantity temp then uv", currentQuantityTempThenUV.toString());
        user.put("21. current quantity uv then temp", currentQuantityUVThenTemp.toString());
        //Test of newVolatilisationRate(), it works!
        System.out.println(">>>>>>>>>>"+newVolatilisationRate(BigDecimal.valueOf(0.00357020501337738),BigDecimal.valueOf(20),BigDecimal.valueOf(277.23)).toString());
        return user;
    }

    private BigDecimal remainingPesticideTemp(BigDecimal startConcentration, BigDecimal hours){
        return startConcentration.subtract(hours.multiply(volatilisationRate().divide(BigDecimal.valueOf(1000),15, RoundingMode.HALF_UP)));
    }

    private BigDecimal remainingPesticideUV(BigDecimal startConcentration, BigDecimal givenUVDose){
        //Okay, can't use .pow() from BigDecimal because you can only raise by integers, so might have to use doubles. This could also be fucked
        //There are going to be so many mistakes, god if all the numbers are right first go it'll be a miracle
        BigDecimal fractionPhotodegraded = BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(1).multiply(BigDecimal.valueOf(Math.pow(E,-0.0009*uvFen.doubleValue()*givenUVDose.doubleValue()))));
        return startConcentration.subtract(fractionPhotodegraded.multiply(startConcentration));
    }

    //This will need changing, pass RPs to the class, don't find them here
    private BigDecimal volatilisationRate(){
        BigDecimal tempInKelvin = growTemp.add(zeroInKelvin);
        //I'm not exactly clear why this division broke it, the others seem fine but I'm worried they'll break if I can't figure out this one
        BigDecimal vapourPressureP1 = BigDecimal.valueOf(regressionParam2.doubleValue()/tempInKelvin.doubleValue());
        BigDecimal vapourPressureP2 = regressionParam1.subtract(vapourPressureP1);
        BigDecimal vapourPressure = BigDecimal.valueOf(Math.pow(10,vapourPressureP2.doubleValue()));
        BigDecimal vapourPressure1mmHg = vapourPressure.multiply(mmHgToPaScaleFactor);
        //Bit janky, maybe try to improve?
        BigDecimal lnVP = BigDecimal.valueOf(log(vapourPressure1mmHg.doubleValue()));
        //Also pretty janky
        return BigDecimal.valueOf(Math.pow(E,BigDecimal.valueOf(11.81).add(BigDecimal.valueOf(0.85956).multiply(lnVP)).doubleValue()));
        //Why are all these values hardcoded?? Are they to do with which pesticide or covering you use? Really need to know what these are.
        //Even if they aren't ones that would change, we should at least name them, that can be the boring task for tomorrow
        //I think they're constant? Very confusing
    }

    private BigDecimal newVolatilisationRate(BigDecimal referenceVapourPressure, BigDecimal referenceTemp, BigDecimal molarMass){
        BigDecimal a = BigDecimal.valueOf(-95000).divide(BigDecimal.valueOf(8.314),15,RoundingMode.HALF_UP);
        BigDecimal b1 = BigDecimal.valueOf(1).divide(growTemp.add(zeroInKelvin),15,RoundingMode.HALF_UP);
        BigDecimal b2 = BigDecimal.valueOf(1).divide(referenceTemp.add(zeroInKelvin),15,RoundingMode.HALF_UP);
        BigDecimal b = b1.subtract(b2);
        BigDecimal exponent = a.multiply(b);
        BigDecimal vapourPressureScaler = BigDecimal.valueOf(Math.pow(E,exponent.doubleValue()));
        //This works.
        BigDecimal predictedVapourPressure = referenceVapourPressure.multiply(vapourPressureScaler);
        //1464 seems to be constant across pesticides
        //This works! Spitting out the same rate as the excel, just need to get confirmation that this is the one to use
        //Then just get it storing these values instead of RP1 and RP2 and you're set
        return BigDecimal.valueOf(1464).multiply(predictedVapourPressure).multiply(molarMass);
    }
}
