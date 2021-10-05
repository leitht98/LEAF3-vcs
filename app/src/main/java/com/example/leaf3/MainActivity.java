package com.example.leaf3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {

    String username = "Tom";
    Location currentLocation = new Location("");
    //protected String latitude,longitude;
    protected float uvFen, regressionParam1, regressionParam2;
    //private TextView resultOutput;
    private FusedLocationProviderClient mFusedLocationClient;

    //private TextView latitudeTextView, longitudeTextView;
    //int PERMISSION_ID = 44;
    private EditText enterDegradation, enterStartQuantity, enterGrowTemp, enterHours, enterUVDose;
    private Button goButton;
    float uvRate = (float) 1;

    String coveringType, pesticideType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Spinner coveringSpinner = (Spinner) findViewById(R.id.covering_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.covering_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        coveringSpinner.setAdapter(adapter);
        coveringSpinner.setOnItemSelectedListener(this);

        Spinner pesticideSpinner = (Spinner) findViewById(R.id.pesticide_spinner);
        ArrayAdapter<CharSequence> pAdapter = ArrayAdapter.createFromResource(this, R.array.pesticides_array, android.R.layout.simple_spinner_item);
        pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        pesticideSpinner.setAdapter(pAdapter);
        pesticideSpinner.setOnItemSelectedListener(this);

        //latitudeTextView = findViewById(R.id.latTextView);
        //longitudeTextView = findViewById(R.id.lonTextView);

        enterDegradation = findViewById(R.id.enterDegradation);
        enterStartQuantity = findViewById(R.id.enterStartQuantity);
        enterGrowTemp = findViewById(R.id.enterGrowTemp);
        enterHours = findViewById(R.id.enterHours);
        enterUVDose = findViewById(R.id.enterUVDose);
        goButton = findViewById(R.id.goButton);
        Button databaseButton = findViewById(R.id.databaseButton);

        goButton.setOnClickListener(v->{
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) {
                new MyTask().execute();
            }
            else {
                Toast.makeText(MainActivity.this, "Please connect ot the internet.", Toast.LENGTH_SHORT).show();
            }
        });

        databaseButton.setOnClickListener(v->{
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) {
                db.collection(username)
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId());
                                }
                                openNewActivity(dataString.toString());
                            } else {
                                Toast.makeText(MainActivity.this, "Please connect ot the internet.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else{
                Toast.makeText(MainActivity.this, "Please connect ot the internet.", Toast.LENGTH_SHORT).show();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //resultOutput = (TextView) findViewById(R.id.result_output);

        //getLastLocation();
    }

    public void openNewActivity(String dataString){
        Intent intent = new Intent(this, DataLog.class);
        intent.putExtra("data_string", dataString);
        intent.putExtra("username", username);
        startActivity(intent);
    }

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


    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){spinnerSwitch(parent,pos,id);}
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onLocationChanged(Location location) {currentLocation = location;}

    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<Void, Void, Void> implements AdapterView.OnItemSelectedListener {
        //String result;

        @SuppressLint("SetTextI18n")
        @Override
        protected Void doInBackground(Void... voids) {
            goButton.setText("Calculating...");
            /*resultOutput.setText("");
            String daysOutput;
            if (daysToReachUVDose() > 300) {
                daysOutput = "Days required: OVER 300";
            } else {
                daysOutput = "Days required: " + daysToReachUVDose();
            }
            resultOutput.setText(daysOutput);*/
            return null;
        }

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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            try {
                Float.parseFloat(enterDegradation.getText().toString());
            } catch (Exception e){
                Toast.makeText(MainActivity.this, "Values must be numbers", Toast.LENGTH_SHORT).show();
                cancel(true);
            }
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){spinnerSwitch(parent,pos,id);}
        public void onNothingSelected(AdapterView<?> parent) {}
    }

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

    @SuppressLint("NonConstantResourceId")
    private void spinnerSwitch(AdapterView<?> parent, int pos, long id){
        switch (parent.getId()) {
            case R.id.covering_spinner:
                parent.getItemAtPosition(pos);
                switch ((int) id) {
                    case 0: //Transparent
                        uvFen = (float) 0.035078273;
                        uvRate = (float) 46.48309932;
                        coveringType = "Transparent";
                        break;
                    case 1: //Opaque
                        uvFen = (float) 0.0003045083;
                        uvRate = (float) 2.707452846;
                        coveringType = "Opaque";
                        break;
                    case 2: //Standard
                        uvFen = (float) 0.00801709;
                        uvRate = (float) 26.48524698;
                        coveringType = "Standard";
                        break;
                    case 3: //No film
                        uvFen = (float) 0.043617209;
                        uvRate = (float) 55.94852496;
                        coveringType = "No film";
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Error fetching covering type.", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case R.id.pesticide_spinner:
                parent.getItemAtPosition(pos);
                switch ((int) id) {
                    case 0: //Fenitrothion
                        pesticideType = "Fenitrothion";
                        regressionParam1 = (float) 6.3362;
                        regressionParam2 = (float) 3197.8;
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Error fetching pesticide.", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }
}