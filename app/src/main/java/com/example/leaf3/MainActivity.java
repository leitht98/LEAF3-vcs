package com.example.leaf3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {

    //Hardcoded username to store projects in individual databases, eventually this will be entered by the user
    //It is now the user's gmail
    String username = "Backup";
    //To store the project location
    Location currentLocation = new Location("");
    protected String latitude,longitude;

    //private TextView resultOutput;
    private FusedLocationProviderClient mFusedLocationClient;

    //private TextView latitudeTextView, longitudeTextView;
    int PERMISSION_ID = 44;
    private EditText enterDegradation, enterStartQuantity, enterGrowTemp, enterHours, enterUVDose;
    private Button goButton;
    private Button getDataButton;
    Button signOutButton;

    //BigDecimal uvFen, uvRate, rParam1, rParam2;

    ArrayList<String> tempCoveringSpinnerArray = new ArrayList<>();
    ArrayList<String> coveringSpinnerArray = new ArrayList<>();
    ArrayList<String> tempPesticideSpinnerArray = new ArrayList<>();
    ArrayList<String> pesticideSpinnerArray = new ArrayList<>();

    Spinner coveringSpinner;
    Spinner pesticideSpinner;

    //String coveringType, pesticideType;

    ArrayList<Covering> coveringsObjectArray = new ArrayList<>();
    ArrayList<Pesticide> pesticidesObjectArray = new ArrayList<>();

    SignInButton btnSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    ActivityResultLauncher<Intent> activityResultLauncher;

    Covering selectedCovering;
    Pesticide selectedPesticide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        coveringSpinner = findViewById(R.id.covering_spinner);
        pesticideSpinner = findViewById(R.id.pesticide_spinner);

        mAuth = FirebaseAuth.getInstance();
        requestGoogleSignIn();

        btnSignIn = findViewById(R.id.btnSignIn);

        coveringSpinnerArray = new ArrayList<>();
        ArrayAdapter<String> coveringStringAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, coveringSpinnerArray);
        coveringStringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        coveringSpinner.setAdapter(coveringStringAdapter);
        coveringSpinner.setOnItemSelectedListener(MainActivity.this);

        pesticideSpinnerArray = new ArrayList<>();
        ArrayAdapter<String> pesticideStringAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pesticideSpinnerArray);
        pesticideStringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        pesticideSpinner.setAdapter(pesticideStringAdapter);
        pesticideSpinner.setOnItemSelectedListener(this);

        //Adding all the other items
        enterDegradation = findViewById(R.id.enterDegradation);
        enterStartQuantity = findViewById(R.id.enterStartQuantity);
        enterGrowTemp = findViewById(R.id.enterGrowTemp);
        enterHours = findViewById(R.id.enterHours);
        enterUVDose = findViewById(R.id.enterUVDose);

        enterDegradation.setOnClickListener(v -> enterDegradation.getText().clear());
        enterStartQuantity.setOnClickListener(v -> enterStartQuantity.getText().clear());
        enterGrowTemp.setOnClickListener(v -> enterGrowTemp.getText().clear());
        enterHours.setOnClickListener(v -> enterHours.getText().clear());
        enterUVDose.setOnClickListener(v -> enterUVDose.getText().clear());

        getDataButton = findViewById(R.id.getDataButton);
        goButton = findViewById(R.id.goButton);
        Button databaseButton = findViewById(R.id.databaseButton);

        signOutButton = findViewById(R.id.signOutButton);

        //Ideally, I'll be able to get rid of this.
        getDataButton.setOnClickListener(v-> {
            if(username.equals("Backup")){
                Toast.makeText(MainActivity.this, "Please sign in.", Toast.LENGTH_SHORT).show();
            }
            else if(isNetworkAvailable()){
                try {
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    service.execute(() -> {
                        //onPre
                        //I don't actually think I need this?
                        runOnUiThread(() -> {
                            //
                        });
                        //background
                        //

                        if (isNetworkAvailable()) {
                            db.collection(getString(R.string.coverings_database_collection))
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        StringBuilder dataString = new StringBuilder();
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {dataString.append(document.getData());}
                                            System.out.println("Raw data string" + dataString);

                                            tempCoveringSpinnerArray.clear();
                                            coveringSpinnerArray.clear();
                                            coveringsObjectArray.clear();

                                            String dataStringTrim = dataString.substring(1, dataString.length() - 1);
                                            //System.out.println("Raw and trimmed: " + dataStringTrim);
                                            String[] coveringsData = dataStringTrim.split(getString(R.string.itemSplit));
                                            for (String i : coveringsData) {
                                                System.out.println("%%% covering individual data: " + i);
                                                String[] coveringValues = i.split(", ");
                                                //System.out.println("UV Rate: " + coveringValues[0].split("=")[1] + "\nUV Fen: " + coveringValues[1].split("=")[1] + "\nName: " + coveringValues[2].split("=")[1]);
                                                //BigDecimal rate = new BigDecimal(coveringValues[0].split("=")[1]);
                                                //BigDecimal fen = new BigDecimal(coveringValues[1].split("=")[1]);
                                                //String name = coveringValues[2].split("=")[1];
                                                String name = "error";
                                                BigDecimal rate = BigDecimal.valueOf(0), fen = BigDecimal.valueOf(0);
                                                //UV Rate=30, UV Fen=0.03, name=Test covering
                                                for(String j : coveringValues){
                                                    String [] labelDataPair = j.split("=");
                                                    switch (labelDataPair[0]){
                                                        case "name": name = labelDataPair[1]; break;
                                                        case "UV Fen": fen = new BigDecimal(labelDataPair[1]); break;
                                                        case "UV Rate": rate = new BigDecimal(labelDataPair[1]); break;
                                                        default: break;
                                                    }
                                                }
                                                //////////////////////////////////////////////////////////////////////////////////////////////
                                                boolean newCovering = true;
                                                for (String j : tempCoveringSpinnerArray) {
                                                    if (j.equals(name)) {
                                                        newCovering = false;
                                                        break;
                                                    }
                                                }
                                                if (newCovering) {
                                                    tempCoveringSpinnerArray.add(name);
                                                    coveringsObjectArray.add(new Covering(name, fen, rate));
                                                }
                                            }
                                            //This never seems to run?? Or having this here stops the whole section running, WHAT???
                                            //Having both also makes it too fast? So it doesn't run this section unless it knows it'll close??
                                            Collections.sort(tempCoveringSpinnerArray);
                                            service.shutdown();
                                        } else {System.out.println("Fail at check 1");}
                                    });

                            db.collection(getString(R.string.pesticides_database_collection))
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        StringBuilder dataString = new StringBuilder();
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                dataString.append(document.getData());
                                            }
                                            System.out.println("Raw data string" + dataString);

                                            ////////
                                            tempPesticideSpinnerArray.clear();
                                            pesticideSpinnerArray.clear();
                                            pesticidesObjectArray.clear();
                                            ////////

                                            String dataStringTrim = dataString.substring(1, dataString.length() - 1);
                                            System.out.println("Raw and trimmed: " + dataStringTrim);
                                            String[] pesticidesData = dataStringTrim.split(getString(R.string.itemSplit));
                                            for (String i : pesticidesData) {
                                                System.out.println("%%% pesticide individual data: " + i);
                                                String[] pesticideValues = i.split(", ");
                                                //System.out.println("Name: " + coveringValues[0].split("=")[1] + "\nRP1: " + coveringValues[1].split("=")[1] + "\nRP2: " + coveringValues[2].split("=")[1]);
                                                //String name = coveringValues[0].split("=")[1];
                                                //BigDecimal rp1 = new BigDecimal(coveringValues[1].split("=")[1]);
                                                //BigDecimal rp2 = new BigDecimal(coveringValues[2].split("=")[1]);

                                                String name = "error";
                                                BigDecimal referenceVapourPressure = BigDecimal.valueOf(0), referenceTemperature = BigDecimal.valueOf(0), molarMass = BigDecimal.valueOf(0), recommendedApplicationRate = BigDecimal.valueOf(0);
                                                for(String j : pesticideValues){
                                                    String[] labelDataPair = j.split("=");
                                                    switch (labelDataPair[0]){
                                                        case "name": name = labelDataPair[1]; break;
                                                        case "reference vapour pressure": referenceVapourPressure = new BigDecimal(labelDataPair[1]); break;
                                                        case "reference temperature": referenceTemperature = new BigDecimal(labelDataPair[1]); break;
                                                        case "molar mass": molarMass = new BigDecimal(labelDataPair[1]); break;
                                                        case "recommended application rate": recommendedApplicationRate = new BigDecimal(labelDataPair[1]); break;
                                                        default: break;
                                                    }
                                                }

                                                //reference vapour pressure=0.003570205013377383, molar mass=277.235, reference temperature=205, name=Test pesticideg
                                                ///////////////////////////////////////////////////////////////////////////////////////////////
                                                boolean newPesticide = true;
                                                for (String j : tempPesticideSpinnerArray) {
                                                    if (j.equals(name)) {
                                                        newPesticide = false;
                                                        break;
                                                    }
                                                }
                                                if (newPesticide) {
                                                    tempPesticideSpinnerArray.add(name);
                                                    System.out.println("^^^name:"+name+" rvp:"+referenceVapourPressure+" rt:"+referenceTemperature+" mm:"+molarMass);
                                                    //Okay, this needs fixing. I think the way you split the values (above) with indexing needs to change/////////////////////////////////////////////////
                                                    pesticidesObjectArray.add(new Pesticide(name, referenceVapourPressure,referenceTemperature,molarMass,recommendedApplicationRate));
                                                }
                                            }
                                            //This never seems to run?? Or having this here stops the whole section running, WHAT???
                                            //Having both also makes it too fast? So it doesn't run this section unless it knows it'll close??
                                            //AHAHAHAHAHAHAHAHAHA got you ya prick!
                                            Collections.sort(tempPesticideSpinnerArray);
                                            service.shutdown();
                                        } else {System.out.println("Fail at check 1");}
                                    });


                            //This shuts it down too fast
                            service.shutdown();
                        } else {System.out.println("Fail at check 2");}

                        //onPost
                        runOnUiThread(() -> {
                            //
                            try {
                                StringBuilder coveringNamesString = new StringBuilder();
                                //If you press load a second time it works! I just need to get it to bloody wait and I'm there
                                for (String i : tempCoveringSpinnerArray) {
                                    coveringNamesString.append(i);}
                                if (coveringNamesString.toString().equals("")) {
                                    getDataButton.setText(R.string.press_again);
                                } else {
                                    getDataButton.setText(R.string.get_data);

                                    coveringSpinnerArray = tempCoveringSpinnerArray;
                                    ArrayAdapter<String> coveringStringAdapterTemp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, coveringSpinnerArray);
                                    coveringStringAdapterTemp.setDropDownViewResource(android.R.layout.simple_spinner_item);
                                    coveringSpinner.setAdapter(coveringStringAdapterTemp);

                                    pesticideSpinnerArray = tempPesticideSpinnerArray;
                                    ArrayAdapter<String> pesticideStringAdapterTemp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pesticideSpinnerArray);
                                    pesticideStringAdapterTemp.setDropDownViewResource(android.R.layout.simple_spinner_item);
                                    pesticideSpinner.setAdapter(pesticideStringAdapterTemp);
                                }
                            } catch (Exception e) {System.out.println("Fail at check 3");}
                        });
                    });
                } catch (Exception e) {Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
            } else{Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        goButton.setOnClickListener(v->{
            if(username.equals("Backup")){
                Toast.makeText(MainActivity.this, "Please sign in.", Toast.LENGTH_SHORT).show();
            }
            else if(isNetworkAvailable()) {
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(() -> {
                    //onPre
                    runOnUiThread(() -> {
                        //
                        try { new BigDecimal(enterDegradation.getText().toString());
                        } catch (Exception e) {Toast.makeText(MainActivity.this, "Values must be numbers", Toast.LENGTH_SHORT).show();}
                    });
                    //background
                    goButton.setText(R.string.calculating);

                    //onPost
                    runOnUiThread(() -> {
                        goButton.setText(R.string.go);
                        try {
                            if(!(selectedCovering==null)) {
                                if (Float.parseFloat(enterDegradation.getText().toString()) < 100 && Float.parseFloat(enterDegradation.getText().toString()) >= 0) {
                                    //Okay, this works but remember it's using UVFen, not just a UV dose.
                                    //So, I need to check if UVFen is constant in growing environments or find a way to get it from the dose
                                    //Seems like the later would be easy if I got a breakdown of dose by wavelength, but don't know if that's possible
                                    Project project = new Project();
                                    //project.saveToDatabase(new BigDecimal(enterStartQuantity.getText().toString()), new BigDecimal(enterUVDose.getText().toString()), new BigDecimal(enterHours.getText().toString()), new BigDecimal(enterGrowTemp.getText().toString()), new BigDecimal(enterDegradation.getText().toString()), coveringType, pesticideType, uvFen, uvRate, rParam1, rParam2, username);
                                    project.saveToDatabase(new BigDecimal(enterStartQuantity.getText().toString()), new BigDecimal(enterUVDose.getText().toString()), new BigDecimal(enterHours.getText().toString()), new BigDecimal(enterGrowTemp.getText().toString()), new BigDecimal(enterDegradation.getText().toString()), selectedCovering, selectedPesticide, username);
                                    Toast.makeText(MainActivity.this, "Calculation Finished\nProject saved", Toast.LENGTH_SHORT).show();
                                } else {Toast.makeText(MainActivity.this, "Degradation must be between 0 and 100%", Toast.LENGTH_SHORT).show();}
                            } else{Toast.makeText(MainActivity.this, "Please load Coverings and Pesticides", Toast.LENGTH_SHORT).show();}
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Values must be numbers", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
            else {Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        databaseButton.setOnClickListener(v->{
            if(username.equals("Backup")){
                Toast.makeText(MainActivity.this, "Please sign in.", Toast.LENGTH_SHORT).show();
            }
            else if(isNetworkAvailable()) {
                db.collection(username)
                        .get()
                        .addOnCompleteListener(task -> {
                            StringBuilder dataString = new StringBuilder();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    dataString.append(document.getData());
                                    dataString.append(", id=").append(document.getId());
                                }
                                //Start new activity, saving the project with DataLog, which is the database view
                                //Copy this section to open the loading page
                                openNewActivity(dataString.toString());
                            } else {Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
                        });
            } else{Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        btnSignIn.setOnClickListener(v -> {
            if(isNetworkAvailable()) {
                signIn();
                signOutButton.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.GONE);
            } else {Toast.makeText(MainActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();}
        });

        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            username = "Backup";
            mGoogleSignInClient.revokeAccess();
            Toast.makeText(MainActivity.this, "Signed out.", Toast.LENGTH_SHORT).show();
            signOutButton.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account.getIdToken());
                            Toast.makeText(MainActivity.this,"Sign in successful.", Toast.LENGTH_SHORT).show();
                        } catch (ApiException e) {Toast.makeText(MainActivity.this,"Sign in failed.", Toast.LENGTH_SHORT).show();}
                    }
                });
        //resultOutput = (TextView) findViewById(R.id.result_output);
        getLastLocation();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Toast.makeText(MainActivity.this, "Welcome back "+currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            username = currentUser.getEmail();
            signOutButton.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
        } else {
            signOutButton.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }

    public void openNewActivity(String dataString){
        Intent intent = new Intent(this, DataLog.class);
        intent.putExtra("data_string", dataString);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    //Oh they're here too that explains it.
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){spinnerSwitch(parent);}
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onLocationChanged(Location location) {currentLocation = location;}

    //I think this will need changing?
    //This is the problem
    //Yeah, it's the order they're in that's wrong
    @SuppressLint("NonConstantResourceId")
    private void spinnerSwitch(AdapterView<?> parent){
        switch (parent.getId()) {
            case R.id.covering_spinner:
                for(Covering i:coveringsObjectArray){
                    if(i.getCoveringName().equals(coveringSpinner.getSelectedItem().toString())){
                        //uvFen = i.getUVFen();
                        //uvRate = i.getUVRate();
                        //coveringType = i.getCoveringName();
                        selectedCovering = i;
                        break;
                    }
                }
                break;
            case R.id.pesticide_spinner:
                for (Pesticide i:pesticidesObjectArray){
                    if (i.getName().equals(pesticideSpinner.getSelectedItem().toString())){
                        //pesticideType = i.getName();
                        //rParam1 = i.getRParam1();
                        //rParam2 = i.getRParam2();
                        selectedPesticide = i;
                        enterStartQuantity.setText(i.getRecommendedApplicationRate().toString());
                        break;
                    }
                }
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }




    private void requestGoogleSignIn(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {username = user.getEmail();}
                    }
                });
    }

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
                        System.out.println("qqqq"+latitude);
                        System.out.println("qqqq"+longitude);
                        //Of course, this stuff probably won't go here...
                        Calendar now = Calendar.getInstance();
                        TimeZone timeZone = now.getTimeZone();
                        //I think this is right? be good to test it in budapest...
                        System.out.println("Current TimeZone is : " + timeZone.getRawOffset());
                        //Again, I think this is right?
                        BigDecimal localTimeMeridian = new BigDecimal(timeZone.getRawOffset()).multiply(BigDecimal.valueOf(15));
                        //Seems about right? off by one day, not really sure why but it should do for now
                        //Don't need it as a function probably, it's much simpler than I expected.
                        System.out.println("Date as int:" + dateAsInt());
                        System.out.println(java.time.LocalTime.now());
                        //Sick, this seems 100% right. Hell yeah baby
                        System.out.println(timeAsBigDecimal(java.time.LocalTime.now()));
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

    private BigDecimal timeAsBigDecimal(LocalTime time){
        System.out.println(time);
        String timeAsString = time.toString();
        String[] elements = timeAsString.split(":");
        BigDecimal timeAsNumber = new BigDecimal(elements[0]).add(new BigDecimal(elements[1]).divide(BigDecimal.valueOf(60),15, RoundingMode.HALF_UP));
        return timeAsNumber;
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

    public int dateAsInt(){
        Calendar c = Calendar.getInstance();
        //int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_YEAR);
        return day;
    }
}