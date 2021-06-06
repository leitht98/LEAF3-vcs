package com.example.leaf3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static java.lang.Math.log;

public class MainActivity extends AppCompatActivity implements LocationListener{
    Location currentLocation = new Location("");
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected String latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("HI");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);*/

        new MyTask().execute();
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("!Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        currentLocation = location;
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        String result;
        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("Doing anything?");//apparently not
            String daysOutput = "Days required: "+daysToReachUVDose();
            System.out.println(daysOutput);
            System.out.println("Latitude:" + currentLocation.getLatitude() + ", Longitude:" + currentLocation.getLongitude());
            return null;
        }

        private int daysToReachUVDose() {
            //Get this to change with dropdown?
            float uvFen = (float) 0.043617209;
            float requiredDegredation = (float) 0.99;

            float uvDoseRequired = (float) -(10000*log(1-requiredDegredation))/(9*uvFen);
                    
            int daysRequired = 0;
            float uvRate = 1;
            if(uvFen == 0.000304508){
                uvRate = (float) 2.707452846;
            }
            else if (uvFen == 0.035078273){
                uvRate = (float) 46.48309932;
            }
            else if (uvFen == 0.00801709){
                uvRate = (float) 26.48524698;
            }
            else if (uvFen == 0.043617209) {
                uvRate = (float) 55.94852496;
            }
            float secondsRequired = uvDoseRequired/uvRate;
            float hoursRequired = secondsRequired/60/60;

            float predictedDaylightHours = (float) 0.0;
            float predictedCumulativeHours = (float) 0.0;
            int days = 0;
            while (predictedCumulativeHours < hoursRequired && days < 300){
                predictedDaylightHours = findDaylightHours(days);
                predictedCumulativeHours += predictedDaylightHours;
                days++;
            }
            daysRequired = days;
            return daysRequired;
        }

        private float findDaylightHours(int days){
            URL url;
            try {
                //start with basic string then add date
                //Change so you can set a date, not just today
                Date requestDate = new Date();
                LocalDate lRequestDate = requestDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                //System.out.println(lRequestDate);
                //Get this to change with a parameter
                lRequestDate = lRequestDate.plusDays(days);
                //System.out.println(lRequestDate);
                requestDate = java.sql.Date.valueOf(lRequestDate.toString());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(requestDate);
                System.out.println("!!!!!"+days);
                System.out.println("!!!"+dateString);
                String latitudeString = "55.953251";
                String longitudeString = "-3.188267";
                url = new URL("https://api.sunrise-sunset.org/json?lat="+latitudeString+"&lng="+longitudeString+"&date="+dateString);
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
                //System.out.println(dayLength);
                //Convert to int
                float dayLengthInHours = dayLengthToHours(dayLength);
                System.out.println(dayLengthInHours);
                return dayLengthInHours;
                //delegate.processFinish(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return (float) 0.0;
        }



        @Override
        protected void onPostExecute(Void aVoid) {

            //System.out.println(result);
            /*
            try {
                JSONObject obj = new JSONObject(result);
                String dayLength = obj.getJSONObject("results").getString("day_length");
                //System.out.println(dayLength);
                //Convert to int
                float dayLengthInHours = dayLengthToHours(dayLength);
                System.out.println(dayLengthInHours);
                //delegate.processFinish(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(aVoid);*/
            System.out.println("Done baby!");
            super.onPostExecute(aVoid);
        }
    }

    //Not at all safe but should work.
    private float dayLengthToHours(String dayLength){
        float totalHours = 0;
        char h1 = dayLength.charAt(0);
        char h2 = dayLength.charAt(1);
        String dayLightHours = String.valueOf(h1) + String.valueOf(h2);
        //System.out.println("H:"+dayLightHours);
        totalHours += Float.parseFloat(dayLightHours);
        //System.out.println(totalHours);
        char m1 = dayLength.charAt(3);
        char m2 = dayLength.charAt(4);
        String dayLightMinutes = String.valueOf(m1) + String.valueOf(m2);
        //System.out.println("M:"+dayLightMinutes);
        //float minutesToHours =
        totalHours += Float.parseFloat(dayLightMinutes)/60;
        //System.out.println(totalHours);
        char s1 = dayLength.charAt(6);
        char s2 = dayLength.charAt(7);
        String dayLightSeconds = String.valueOf(s1) + String.valueOf(s2);
        //System.out.println("S:"+dayLightSeconds);
        totalHours += Float.parseFloat(dayLightSeconds)/60/60;
        //System.out.println(totalHours);

        return totalHours;
    }


}