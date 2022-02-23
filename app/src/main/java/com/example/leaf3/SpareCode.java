package com.example.leaf3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SpareCode {
    //Attempts at getting covering data
    /*ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) {
            db.collection("coverings")
                    .get()
                    .addOnCompleteListener(task -> {
                        StringBuilder dataString = new StringBuilder();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                dataString.append(document.getData());
                            }
                            //openNewActivity(dataString.toString());
                            //Toast.makeText(MainActivity.this, dataString.toString(), Toast.LENGTH_SHORT).show();
                            System.out.println(dataString);
                            //{UV Rate=2.707452846, UV Fen=0.0003045083, name=Opaque}, id=0ELYwBTDJezJp1iT4JWJ{UV Rate=55.94852496, UV Fen=0.043617209, name=No film}, id=gU7LbC3LfFV0uKTHmdfq{UV Rate=26.48524698, UV Fen=0.00801709, name=Standard}, id=i6zYAC4L8O04l6Uad1Z6{UV Rate=46.48309932, UV Fen=0.035078273, name=Transparent}, id=jPQc0r6jo5isloaM4LE1
                            String[] coveringNamesRaw = dataString.toString().split("name=");
                            for(String i : coveringNamesRaw) {
                                System.out.println(i);
                            }
                            System.out.println("!!!!!!!!!!!!!!!!!!!");
                            String[] coveringNamesTrimmed = Arrays.copyOfRange(coveringNamesRaw, 1, coveringNamesRaw.length);

                            for(String i : coveringNamesTrimmed) {
                                System.out.println(i);
                                //Toast.makeText(MainActivity.this, i, Toast.LENGTH_SHORT).show();
                                String[] tempArray = i.split("\\}");
                                //Toast.makeText(MainActivity.this, tempArray[0], Toast.LENGTH_SHORT).show();
                                tempCoveringSpinnerArray.add(tempArray[0]);

                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Please connect ot the internet.", Toast.LENGTH_SHORT).show();
                        }
                        *//*Toast.makeText(MainActivity.this, ""+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
                        for(String j : tempCoveringSpinnerArray){
                            Toast.makeText(MainActivity.this, j, Toast.LENGTH_SHORT).show();
                        }*//*
                    });
        } else{
            Toast.makeText(MainActivity.this, "Please connect ot the internet.", Toast.LENGTH_SHORT).show();
        }*/
        /*Toast.makeText(MainActivity.this, ""+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
        for(String j : tempCoveringSpinnerArray){
            Toast.makeText(MainActivity.this, j, Toast.LENGTH_SHORT).show();
        }*/
    //coveringSpinnerArray.remove(0);
        /*
        Toast.makeText(MainActivity.this, "%"+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
        for(String i : tempCoveringSpinnerArray){
            Toast.makeText(MainActivity.this, i, Toast.LENGTH_SHORT).show();
        }*/
    /*Toast.makeText(MainActivity.this, "!"+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
        for(String j : tempCoveringSpinnerArray){
            Toast.makeText(MainActivity.this, "!"+j, Toast.LENGTH_SHORT).show();
        }*/
    //This works, clearly pulling the data correctly because it's showing up
        /*Toast.makeText(MainActivity.this, "!!!>"+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
        for(String j : tempCoveringSpinnerArray){
            Toast.makeText(MainActivity.this, "!!!>"+j, Toast.LENGTH_SHORT).show();
        }*/
    /*Toast.makeText(MainActivity.this, "!!!"+ finalTempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
                for(String j : finalTempCoveringSpinnerArray){
                    Toast.makeText(MainActivity.this, "!!!"+j, Toast.LENGTH_SHORT).show();
                }*/
    /*Toast.makeText(MainActivity.this, ""+ finalTempCoveringSpinnerArray1.size(), Toast.LENGTH_SHORT).show();
            for(String i : finalTempCoveringSpinnerArray1){
                Toast.makeText(MainActivity.this, i, Toast.LENGTH_SHORT).show();
            }*/
    /*Toast.makeText(MainActivity.this, "~~"+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
        for(String i : tempCoveringSpinnerArray){
            Toast.makeText(MainActivity.this, "~~"+i, Toast.LENGTH_SHORT).show();
        }*/

    //Failed attempt to make another AsyncTask
    /*
    @SuppressLint("StaticFieldLeak")
    private class LoadSpinnerDataAsync extends AsyncTask<Void, Void, Void> implements AdapterView.OnItemSelectedListener {

        @SuppressLint("SetTextI18n")
        @Override
        protected Void doInBackground(Void... voids) {
            //This is where you'll do shit
            goButton.setText("Weeeeeeeeee");
            //Toast.makeText(MainActivity.this, "Async Baby!!!!!", Toast.LENGTH_SHORT).show();
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            goButton.setText("GO GO GO");
        }

        protected void onPreExecute() {

            super.onPreExecute();

            goButton.setText("I'm so confused");
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){spinnerSwitch(parent,pos,id);}
        public void onNothingSelected(AdapterView<?> parent) {}
    }*/

    //Currently used and working AsyncTask, being replaced w/ ExecutorService - in theory
    //Maybe don't delete until you know the new thing works
    /*/*@SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<Void, Void, Void> implements AdapterView.OnItemSelectedListener {
        //String result;

        @SuppressLint("SetTextI18n")
        @Override
        protected Void doInBackground(Void... voids) {
            //.makeText(MainActivity.this, "What the actual fuck is wrong?", Toast.LENGTH_SHORT).show();
            if(myTaskBehaviour==0){
                //Toast.makeText(MainActivity.this, "God Let This Be IT.", Toast.LENGTH_SHORT).show();
                myTaskBehaviour=1;
            }
            else {
                goButton.setText("Calculating...");
            }
            //Okay, I think the background stuff needs to happen here.
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Toast.makeText(MainActivity.this, "Or even here?", Toast.LENGTH_SHORT).show();
            if(myTaskBehaviour==0){
                //Toast.makeText(MainActivity.this, "This Is Going To Be IT.", Toast.LENGTH_SHORT).show();
            }

            else{
                //Toast.makeText(MainActivity.this, "Do we get here?", Toast.LENGTH_SHORT).show();
                goButton.setText("GO!");

                try {
                    if(Float.parseFloat(enterDegradation.getText().toString())<100&&Float.parseFloat(enterDegradation.getText().toString())>=0) {
                        //Okay, this works but remember it's using UVFen, not just a UV dose.
                        //So, I need to check if UVFen is constant in growing environments or find a way to get it from the dose
                        //Seems like the later would be easy if I got a breakdown of dose by wavelength, but don't know if that's possible
                        Project project = new Project();

                        //Need to add error message if it fails to write to the database.
                        //project.saveToDatabase(Float.parseFloat(enterStartQuantity.getText().toString()), Float.parseFloat(enterUVDose.getText().toString()), Float.parseFloat(enterHours.getText().toString()), Float.parseFloat(enterGrowTemp.getText().toString()), Float.parseFloat(enterDegradation.getText().toString()), resultOutput.getText().toString(), coveringType, latitude, longitude, pesticideType, uvFen);
                        project.saveToDatabase(Float.parseFloat(enterStartQuantity.getText().toString()), Float.parseFloat(enterUVDose.getText().toString()), Float.parseFloat(enterHours.getText().toString()), Float.parseFloat(enterGrowTemp.getText().toString()), Float.parseFloat(enterDegradation.getText().toString()), coveringType, pesticideType, uvFen, username);
                        //Toast.makeText(MainActivity.this, "Calculation Finished.\n" + resultOutput.getText().toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Calculation Finished.\nProject is being saved...", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(MainActivity.this, "Degradation must be between 0 and 100%", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Values must be numbers", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            if(myTaskBehaviour==0){
                //Toast.makeText(MainActivity.this, "This Has To Be IT.", Toast.LENGTH_SHORT).show();
                tempCoveringSpinnerArray.add("PlEASE");
                //Toast.makeText(MainActivity.this, "For the love of god please"+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
                //getCoveirngs();
                //I need to get to DoInBAckground, so this needs to go, but the other bits can't crash
                cancel(true);
                //Okay, we're just going to add dummy numbers and see what that does
                /*
                enterStartQuantity.setText("5");
                enterUVDose.setText("5");
                enterHours.setText("5");
                enterGrowTemp.setText("5");
                enterDegradation.setText("5");
                coveringType = "Opaque";
                pesticideType = "Fenitrothion";
                uvFen = (float) 0.4563;
            /*}
            else {
                try {
                    Float.parseFloat(enterDegradation.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Values must be numbers", Toast.LENGTH_SHORT).show();
                    cancel(true);
                }
            }
        }*/
    /*

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){spinnerSwitch(parent,pos,id);}
        public void onNothingSelected(AdapterView<?> parent) {}
    }*/

    //getCoverings(), might still be useful, don't delete just yet
    /*public ArrayList<String> getCoveirngs(){
        //Toast.makeText(MainActivity.this, "MaybE?????"+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<String> tempCoveringSpinnerArray = new ArrayList<>();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) {
            db.collection("coverings")
                    .get()
                    .addOnCompleteListener(task -> {
                        StringBuilder dataString = new StringBuilder();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                dataString.append(document.getData());
                            }
                            //openNewActivity(dataString.toString());
                            //Toast.makeText(MainActivity.this, dataString.toString(), Toast.LENGTH_SHORT).show();
                            System.out.println(dataString);
                            //{UV Rate=2.707452846, UV Fen=0.0003045083, name=Opaque}, id=0ELYwBTDJezJp1iT4JWJ{UV Rate=55.94852496, UV Fen=0.043617209, name=No film}, id=gU7LbC3LfFV0uKTHmdfq{UV Rate=26.48524698, UV Fen=0.00801709, name=Standard}, id=i6zYAC4L8O04l6Uad1Z6{UV Rate=46.48309932, UV Fen=0.035078273, name=Transparent}, id=jPQc0r6jo5isloaM4LE1
                            String[] coveringNamesRaw = dataString.toString().split("name=");
                            for(String i : coveringNamesRaw) {
                                System.out.println(i);
                            }
                            System.out.println("!!!!!!!!!!!!!!!!!!!");
                            String[] coveringNamesTrimmed = Arrays.copyOfRange(coveringNamesRaw, 1, coveringNamesRaw.length);

                            for(String i : coveringNamesTrimmed) {
                                System.out.println(i);
                                //Toast.makeText(MainActivity.this, i, Toast.LENGTH_SHORT).show();
                                String[] tempArray = i.split("\\}");
                                //Toast.makeText(MainActivity.this, tempArray[0], Toast.LENGTH_SHORT).show();
                                tempCoveringSpinnerArray.add(tempArray[0]);

                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Please connect ot the internet.", Toast.LENGTH_SHORT).show();
                        }
                        /*Toast.makeText(MainActivity.this, ""+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
                        for(String j : tempCoveringSpinnerArray){
                            Toast.makeText(MainActivity.this, j, Toast.LENGTH_SHORT).show();
                        }*/
                    /*});
        } else{
            Toast.makeText(MainActivity.this, "Please connect ot the internet.", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(MainActivity.this, "Okay, what's going on"+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
        return tempCoveringSpinnerArray;
    }*/


    //Location stuff, this will probably actually need to be used again but it's clogging things up at the minute
    //getLastLocation();
    /*
    //Copied from:
    //https://www.geeksforgeeks.org/how-to-get-user-location-in-android/
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        latitude = location.getLatitude()+"";
                        longitude = location.getLongitude()+"";
                        //latitudeTextView.setText("Latitude: "+latitude);
                        //longitudeTextView.setText("Longitude: "+longitude);
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {};

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
    //End of copied section

     */

    //Days required output from old version, also important
    //Stick in doInBackground of MyTask, or equivalent
    /*resultOutput.setText("");
            String daysOutput;
            if (daysToReachUVDose() > 300) {
                daysOutput = "Days required: OVER 300";
            } else {
                daysOutput = "Days required: " + daysToReachUVDose();
            }
            resultOutput.setText(daysOutput);*/

    //Functions to go in MyTask, so they can run as AsyncTask, or equivalent
    /*
        private int daysToReachUVDose() {

            float requiredDegradation = (float) Float.parseFloat(enterDegradation.getText().toString()) / 100;

            float uvDoseRequired = (float) -(10000 * log(1 - requiredDegradation)) / (9 * uvFen);

            int daysRequired;
            float secondsRequired = uvDoseRequired / uvRate;
            float hoursRequired = secondsRequired / 60 / 60;
            float predictedCumulativeHours = (float) 0.0;
            int days = 0;
            while (predictedCumulativeHours < hoursRequired && days < 301) {
                predictedCumulativeHours += findDaylightHours(days);
                days++;
            }
            daysRequired = days;
            return daysRequired;
        }

        private float findDaylightHours(int days){
            URL url;
            try {
                Date requestDate = new Date();
                LocalDate lRequestDate = requestDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                lRequestDate = lRequestDate.plusDays(days);
                requestDate = java.sql.Date.valueOf(lRequestDate.toString());
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(requestDate);
                url = new URL("https://api.sunrise-sunset.org/json?lat="+latitude+"&lng="+longitude+"&date="+dateString);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String stringOutput = "";
                while ((stringBuffer = bufferedReader.readLine()) != null){
                    stringOutput = String.format("%s%s", stringOutput, stringBuffer);
                }
                bufferedReader.close();
                result = stringOutput;
            } catch (IOException e){
                e.printStackTrace();
                result = e.toString();
            }
            try {
                JSONObject obj = new JSONObject(result);
                String dayLength = obj.getJSONObject("results").getString("day_length");
                return dayLengthToHours(dayLength);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return (float) 0.0;
        }

         */
    //Doesn't need to be Async
    /*
    private float dayLengthToHours(String dayLength){
        float totalHours = 0;
        String dayLightHours = dayLength.charAt(0) + String.valueOf(dayLength.charAt(1));
        totalHours += Float.parseFloat(dayLightHours);
        String dayLightMinutes = dayLength.charAt(3) + String.valueOf(dayLength.charAt(4));
        totalHours += Float.parseFloat(dayLightMinutes)/60;
        String dayLightSeconds = dayLength.charAt(6) + String.valueOf(dayLength.charAt(7));
        totalHours += Float.parseFloat(dayLightSeconds)/60/60;
        return totalHours;
    }

     */

}
