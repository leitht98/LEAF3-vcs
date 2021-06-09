package com.example.leaf3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

import static java.lang.Math.floorDiv;
import static java.lang.Math.log;

public class MainActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {
    Location currentLocation = new Location("");
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected String latitude,longitude;
    protected float uvFen;
    TextView resultOutput;
    FusedLocationProviderClient mFusedLocationClient;

    TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;
    EditText enterDate;
    Button goButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //System.out.println("HI");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SPINNER NONSENSE
        Spinner coveringSpinner = (Spinner) findViewById(R.id.covering_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.covering_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        coveringSpinner.setAdapter(adapter);
        coveringSpinner.setOnItemSelectedListener(this);

        latitudeTextView = findViewById(R.id.latTextView);
        longitTextView = findViewById(R.id.lonTextView);

        enterDate = findViewById(R.id.enterDate);
        goButton = findViewById(R.id.goButton);

        ProjectDAO dao = new ProjectDAO();

        goButton.setOnClickListener(v->{
            System.out.println("GO???");
            Project prj = new Project(enterDate.getText().toString());
            dao.add(prj).addOnSuccessListener(suc -> {
                Toast.makeText(this,"Date recorded",Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(er -> {
                Toast.makeText(this,""+er.getMessage(),Toast.LENGTH_SHORT).show();
            });
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        resultOutput = (TextView) findViewById(R.id.result_output);

        getLastLocation();

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
        //new MyTask().execute();
    }

    //https://www.geeksforgeeks.org/how-to-get-user-location-in-android/
    //Very much copied
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitudeTextView.setText(location.getLatitude() + "");
                            longitTextView.setText(location.getLongitude() + "");
                            //System.out.println("~~~~~~~~~~~~Lat:"+location.getLatitude() + "");
                            //System.out.println("~~~~~~~~~~~~Long:"+location.getLongitude() + "");
                            latitude = location.getLatitude()+"";
                            longitude = location.getLongitude()+"";
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
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

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
            //System.out.println("~~~~~~~Lat:"+mLastLocation.getLatitude() + "");
            //System.out.println("~~~~~~~Long:"+mLastLocation.getLongitude() + "");
            //latitude = mLastLocation.getLatitude()+"";
            //longitude = mLastLocation.getLongitude()+"";
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
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
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        parent.getItemAtPosition(pos);
        //System.out.println("---------------------------------------------------------------------------" + id);
        switch ((int)id) {
            case 0: //t
                uvFen = (float) 0.035078273;
                break;
            case 1: //o
                uvFen = (float) 0.0003045083;
                break;
            case 2: //s
                uvFen = (float) 0.00801709;
                break;
            case 3: //no film
                uvFen = (float) 0.043617209;
                break;

        }
        //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!FEN" +uvFen);
        new MyTask().execute();
    }
    public void onNothingSelected(AdapterView<?> parent) {

    }



    @Override
    public void onLocationChanged(Location location) {
        //System.out.println("!Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        currentLocation = location;
    }

    private class MyTask extends AsyncTask<Void, Void, Void> implements AdapterView.OnItemSelectedListener {

        //float uvFen;
        String result;
        @Override
        protected Void doInBackground(Void... voids) {

            //System.out.println("Doing anything?");
            String daysOutput = "";
            if (daysToReachUVDose()>300){
                daysOutput = "Days required: OVER 300";
            } else {
                daysOutput = "Days required: " + daysToReachUVDose();
            }
            resultOutput.setText(daysOutput);
            System.out.println(daysOutput);
            //System.out.println("Latitude:" + currentLocation.getLatitude() + ", Longitude:" + currentLocation.getLongitude());
            //System.out.println("!Latitude:" + latitude + ", !Longitude:" + longitude);
            return null;
        }

        private int daysToReachUVDose() {
            //Get this to change with dropdown?
//            float uvFen = (float) 0.035078273;
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
            while (predictedCumulativeHours < hoursRequired && days < 301){
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
                //System.out.println(dateString);
                //System.out.println("!!!!!!!!!!------"+uvFen);
                String latString = "0";
                String longString = "0";
                //System.out.println("########################"+latitude+longitude);
                //System.out.println("### "+"https://api.sunrise-sunset.org/json?lat="+latString+"&lng="+longString+"&date="+dateString);
                //System.out.println("# "+"https://api.sunrise-sunset.org/json?lat="+latitude+"&lng="+longitude+"&date="+dateString);
                url = new URL("https://api.sunrise-sunset.org/json?lat="+latitude+"&lng="+longitude+"&date="+dateString);
                System.out.println(url);
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


        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
            parent.getItemAtPosition(pos);
            switch (pos) {
                case 1:
                    uvFen = (float) 0.035078273;
                    break;
                case 2:
                    uvFen = (float) 0.000304508;
                    break;
                case 3:
                    uvFen = (float) 0.00801709;
                    break;
                case 4:
                    uvFen = (float) 0.043617209;
                    break;

            }
        }
        public void onNothingSelected(AdapterView<?> parent) {

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