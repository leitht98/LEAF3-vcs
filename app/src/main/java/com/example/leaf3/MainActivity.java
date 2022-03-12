package com.example.leaf3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {

    //Hardcoded username to store projects in individual databases, eventually this will be entered by the user
    //It is now the user's gmail
    String username = "Backup";
    //To store the project location
    Location currentLocation = new Location("");
    //protected String latitude,longitude;

    //private TextView resultOutput;
    //private FusedLocationProviderClient mFusedLocationClient;

    //private TextView latitudeTextView, longitudeTextView;
    //int PERMISSION_ID = 44;
    private EditText enterDegradation, enterStartQuantity, enterGrowTemp, enterHours, enterUVDose;
    private Button goButton;
    private Button getDataButton;
    Button signOutButton;

    BigDecimal uvFen, uvRate, rParam1, rParam2;

    ArrayList<String> tempCoveringSpinnerArray = new ArrayList<>();
    ArrayList<String> coveringSpinnerArray = new ArrayList<>();
    ArrayList<String> tempPesticideSpinnerArray = new ArrayList<>();
    ArrayList<String> pesticideSpinnerArray = new ArrayList<>();

    Spinner coveringSpinner;
    Spinner pesticideSpinner;

    String coveringType, pesticideType;

    ArrayList<Covering> coveringsObjectArray = new ArrayList<>();
    ArrayList<Pesticide> pesticidesObjectArray = new ArrayList<>();

    SignInButton btnSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    ActivityResultLauncher<Intent> activityResultLauncher;


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



        //coveringSpinner.setOnItemSelectedListener(this);
        ExecutorService coveringsService = Executors.newSingleThreadExecutor();
        Future<ArrayList<String>> future = coveringsService.submit(new getCoveringsAsFuture());

        //Stuff to do in background - Fucking nothing!

        try {
            //Okay, this is literally exactly the same as it is when you click the button, why doesn't it work here??
            coveringSpinnerArray = future.get();
            ArrayAdapter<String> coveringStringAdapterTemp = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, coveringSpinnerArray);
            coveringStringAdapterTemp.setDropDownViewResource(android.R.layout.simple_spinner_item);
            coveringSpinner.setAdapter(coveringStringAdapterTemp);
            //Nope. Once again. Fucking hell!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            coveringSpinner.setOnItemSelectedListener(MainActivity.this);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        pesticideSpinnerArray = new ArrayList<>();
        ArrayAdapter<String> pesticideStringAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pesticideSpinnerArray);
        pesticideStringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        pesticideSpinner.setAdapter(pesticideStringAdapter);
        pesticideSpinner.setOnItemSelectedListener(this);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                            db.collection("coverings")
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
                                                System.out.println("individual data: " + i);
                                                String[] coveringValues = i.split(",");
                                                System.out.println("UV Rate: " + coveringValues[0].split("=")[1] + "\nUV Fen: " + coveringValues[1].split("=")[1] + "\nName: " + coveringValues[2].split("=")[1]);
                                                BigDecimal rate = new BigDecimal(coveringValues[0].split("=")[1]);
                                                BigDecimal fen = new BigDecimal(coveringValues[1].split("=")[1]);
                                                String name = coveringValues[2].split("=")[1];
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
                                            service.shutdown();
                                        } else {System.out.println("Fail at check 1");}
                                    });

                            db.collection("pesticides")
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
                                            String[] coveringsData = dataStringTrim.split(getString(R.string.itemSplit));
                                            for (String i : coveringsData) {
                                                System.out.println("individual data: " + i);
                                                String[] coveringValues = i.split(",");
                                                System.out.println("Name: " + coveringValues[0].split("=")[1] + "\nRP1: " + coveringValues[1].split("=")[1] + "\nRP2: " + coveringValues[2].split("=")[1]);
                                                String name = coveringValues[0].split("=")[1];
                                                BigDecimal rp1 = new BigDecimal(coveringValues[1].split("=")[1]);
                                                BigDecimal rp2 = new BigDecimal(coveringValues[2].split("=")[1]);
                                                boolean newPesticide = true;
                                                for (String j : tempPesticideSpinnerArray) {
                                                    if (j.equals(name)) {
                                                        newPesticide = false;
                                                        break;
                                                    }
                                                }
                                                if (newPesticide) {
                                                    tempPesticideSpinnerArray.add(name);
                                                    pesticidesObjectArray.add(new Pesticide(name, rp1, rp2));
                                                }
                                            }
                                            //This never seems to run?? Or having this here stops the whole section running, WHAT???
                                            //Having both also makes it too fast? So it doesn't run this section unless it knows it'll close??
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
                            if(!(coveringType==null)) {
                                if (Float.parseFloat(enterDegradation.getText().toString()) < 100 && Float.parseFloat(enterDegradation.getText().toString()) >= 0) {
                                    //Okay, this works but remember it's using UVFen, not just a UV dose.
                                    //So, I need to check if UVFen is constant in growing environments or find a way to get it from the dose
                                    //Seems like the later would be easy if I got a breakdown of dose by wavelength, but don't know if that's possible
                                    Project project = new Project();
                                    project.saveToDatabase(new BigDecimal(enterStartQuantity.getText().toString()), new BigDecimal(enterUVDose.getText().toString()), new BigDecimal(enterHours.getText().toString()), new BigDecimal(enterGrowTemp.getText().toString()), new BigDecimal(enterDegradation.getText().toString()), coveringType, pesticideType, uvFen, uvRate, rParam1, rParam2, username);
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

        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

    class getCoveringsAsFuture implements Callable<ArrayList<String>>{

        @Override
        public ArrayList<String> call(){
            ArrayList<String> coveringSpinnerArrayUsingFuturesTemp = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                String dataStringTrim = dataString.substring(1,dataString.length()-1);
                                String[] coveringsData = dataStringTrim.split(getString(R.string.itemSplit));
                                for(String i : coveringsData){
                                    String[] coveringValues = i.split(",");
                                    BigDecimal rate = new BigDecimal(coveringValues[0].split("=")[1]);
                                    BigDecimal fen = new BigDecimal(coveringValues[1].split("=")[1]);
                                    String name = coveringValues[2].split("=")[1];
                                    boolean newCovering = true;
                                    for (String j: coveringSpinnerArrayUsingFuturesTemp){
                                        if (j.equals(name)) {
                                            newCovering = false;
                                            break;
                                        }
                                    }
                                    if(newCovering) {
                                        coveringSpinnerArrayUsingFuturesTemp.add(name);
                                        coveringsObjectArray.add(new Covering(name,fen,rate));
                                    }
                                }
                            } else {System.out.println("Fail at check 1");}
                        });
            } else{System.out.println("Fail at check 2");}
            return coveringSpinnerArrayUsingFuturesTemp;
        }
    }


    //I think this will need changing?
    //This is the problem
    //Yeah, it's the order they're in that's wrong
    @SuppressLint("NonConstantResourceId")
    private void spinnerSwitch(AdapterView<?> parent){
        switch (parent.getId()) {
            case R.id.covering_spinner:
                for(Covering i:coveringsObjectArray){
                    if(i.getCoveringName().equals(coveringSpinner.getSelectedItem().toString())){
                        uvFen = i.getUVFen();
                        uvRate = i.getUVRate();
                        coveringType = i.getCoveringName();
                        break;
                    }
                }
                break;
            case R.id.pesticide_spinner:
                for (Pesticide i:pesticidesObjectArray){
                    if (i.getName().equals(pesticideSpinner.getSelectedItem().toString())){
                        pesticideType = i.getName();
                        rParam1 = i.getRParam1();
                        rParam2 = i.getRParam2();
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
}