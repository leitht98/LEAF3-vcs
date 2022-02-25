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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {

    //Hardcoded username to store projects in individual databases, eventually this will be entered by the user
    String username = "Paola";
    //To store the project location
    Location currentLocation = new Location("");
    //protected String latitude,longitude;
    protected float uvFen, regressionParam1, regressionParam2;
    //private TextView resultOutput;
    private FusedLocationProviderClient mFusedLocationClient;

    //private TextView latitudeTextView, longitudeTextView;
    //int PERMISSION_ID = 44;
    private EditText enterDegradation, enterStartQuantity, enterGrowTemp, enterHours, enterUVDose;
    private Button goButton, getDataButton;
    float uvRate = (float) 1;

    ArrayList<String> tempCoveringSpinnerArray = new ArrayList<>();
    ArrayList<String> coveringSpinnerArray = new ArrayList<>();
    ArrayList<String> coveringSpinnerArrayUsingFutures = new ArrayList<>();

    Spinner coveringSpinner;

    String coveringType, pesticideType;

    ArrayList<Covering> coveringsObjectArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //You should probably do the loading page here?

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /////////////////////////////////////////////////////////////////////////////////////////////////
        //Run the callable here.
        //Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
        //Spinner coveringSpinner = findViewById(R.id.covering_spinner);
        coveringSpinner = findViewById(R.id.covering_spinner);
        //coveringSpinner.setOnItemSelectedListener(this);
        ExecutorService coveringsService = Executors.newSingleThreadExecutor();
        Future<ArrayList<String>> future = coveringsService.submit(new getCoveringsAsFuture());

        //Stuff to do in background - Fucking nothing!

        try {
            //Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();
            /*coveringSpinnerArrayUsingFutures = future.get(); //Blocking???
            ArrayAdapter<String> coveringStringAdapterFutures = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, coveringSpinnerArrayUsingFutures);
            coveringStringAdapterFutures.setDropDownViewResource(android.R.layout.simple_spinner_item);
            coveringSpinner.setAdapter(coveringStringAdapterFutures);
            //This must be the issue but no matter where I put it it still doesn't work.
            coveringSpinner.setOnItemSelectedListener(this);*/


            //Okay, this is literally exactly the same as it is when you click the button, why doesn't it work here??
            coveringSpinnerArray = future.get();
            ArrayAdapter<String> coveringStringAdapterTemp = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, coveringSpinnerArray);
            coveringStringAdapterTemp.setDropDownViewResource(android.R.layout.simple_spinner_item);
            coveringSpinner.setAdapter(coveringStringAdapterTemp);
            //Nope. Once again. Fucking hell!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            coveringSpinner.setOnItemSelectedListener(MainActivity.this);

            //This doesn't work either????
            /*coveringSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i) {
                        case 3: //Transparent
                            uvFen = (float) 0.035078273;
                            uvRate = (float) 46.48309932;
                            coveringType = "Transparent";
                            break;
                        case 0: //Opaque
                            uvFen = (float) 0.0003045083;
                            uvRate = (float) 2.707452846;
                            coveringType = "Opaque";
                            break;
                        case 2: //Standard
                            uvFen = (float) 0.00801709;
                            uvRate = (float) 26.48524698;
                            coveringType = "Standard";
                            break;
                        case 1: //No film
                            uvFen = (float) 0.043617209;
                            uvRate = (float) 55.94852496;
                            coveringType = "No film";
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "Error fetching covering type.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });*/

            //Toast.makeText(MainActivity.this, "3", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Toast.makeText(MainActivity.this, "4", Toast.LENGTH_SHORT).show();
        /*ArrayAdapter<String> coveringStringAdapterFutures = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, coveringSpinnerArrayUsingFutures);
        coveringStringAdapterFutures.setDropDownViewResource(android.R.layout.simple_spinner_item);
        coveringSpinner.setAdapter(coveringStringAdapterFutures);
        coveringSpinner.setOnItemSelectedListener(this);*/
        //Toast.makeText(MainActivity.this, "5", Toast.LENGTH_SHORT).show();


        /////////////////////////////////////////////////////////////////////////////////////////////////

        //tempCoveringSpinnerArray = new ArrayList<>();

        /*if(tempCoveringSpinnerArray.size()!=0){
            for(String i : tempCoveringSpinnerArray){
                coveringSpinnerArray.add(i);
            }
        } else{
            coveringSpinnerArray.add("Please press Get Data Button");
        }*/

        //Putting pesticide/ covering data into spinners once you've got it - yet to figure that part out
        //Spinner coveringSpinner = findViewById(R.id.covering_spinner);
        //ArrayAdapter<String> coveringStringAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, coveringSpinnerArray);
        //coveringStringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        //coveringSpinner.setAdapter(coveringStringAdapter);
        //coveringSpinner.setOnItemSelectedListener(this);

        ArrayList<String> pesticideSpinnerArray = new ArrayList<>();
        pesticideSpinnerArray.add("Fenitrothion");
        //pesticideSpinnerArray.add("Desperation");

        Spinner pesticideSpinner = findViewById(R.id.pesticide_spinner);
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
        getDataButton = findViewById(R.id.getDataButton);
        goButton = findViewById(R.id.goButton);
        Button databaseButton = findViewById(R.id.databaseButton);

        //This works, clearly pulling the data correctly because it's showing up, find code in SpareCode if needed

        //Ideally, I'll be able to get rid of this.
        getDataButton.setOnClickListener(v->{
            //openLoadActivity("A loading screen!");
            //Move code from LoadingPage here?


            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(() -> {
                //onPre
                //I don't actually think I need this?
                runOnUiThread(() -> {
                    //
                    System.out.println("This is pre");
                    System.out.println(">>>"+tempCoveringSpinnerArray.size());
                });
                //background
                //
                System.out.println("This is background");
                System.out.println(">>>"+tempCoveringSpinnerArray.size());

                //FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                    System.out.println("Raw data string" + dataString);
                                    String[] coveringNamesRaw = dataString.toString().split("name=");
                                    String[] coveringNamesTrimmed = Arrays.copyOfRange(coveringNamesRaw, 1, coveringNamesRaw.length);

                                    for(String i : coveringNamesTrimmed) {
                                        System.out.println("Trimmed names: "+i);
                                        String[] tempArray = i.split("\\}");
                                        //Okay now we're getting somewhere! At the very least I can get it to load the data, you just have to press the button twice
                                        //Now need to get this data back to the spinner
                                        //Or copy this all into MainActivity instead of having a loading screen
                                        //What happens if I just run the service twice?
                                        boolean newCovering = true;
                                        for (String j: tempCoveringSpinnerArray){
                                            if(j.equals(tempArray[0])){
                                                newCovering = false;
                                            }
                                        }
                                        if(newCovering) {
                                            tempCoveringSpinnerArray.add(tempArray[0]);
                                        }
                                    }
                                    for(String i:tempCoveringSpinnerArray) {
                                        System.out.println("IT???~ "+i);
                                    }
                                    //This never seems to run?? Or having this here stops the whole section running, WHAT???
                                    //Having both also makes it too fast? So it doesn't run this section unless it knows it'll close??
                                    service.shutdown();
                                } else {
                                    System.out.println("Fail at check 1");
                                }
                            });
                    //This shuts it down too fast
                    service.shutdown();
                    //The if just breaks it. Why do I need this to allow the other section to run?
                    if(tempCoveringSpinnerArray.size()==5) {
                        service.shutdown();
                    }
                } else{
                    System.out.println("Fail at check 2");
                }

                System.out.println("!>>>"+tempCoveringSpinnerArray.size());
                for(String i:tempCoveringSpinnerArray) {
                    System.out.println("IT??? "+i);
                }

                //onPost
                runOnUiThread(() -> {
                    //
                    try {
                        service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                        System.out.println("This is post");
                        System.out.println(">>>" + tempCoveringSpinnerArray.size());
                        String coveringNamesString = "";
                        //If you press load a second time it works! I just need to get it to bloody wait and I'm there
                        for(String i:tempCoveringSpinnerArray) {
                            coveringNamesString+=i;
                        }
                        if(coveringNamesString==""){
                            getDataButton.setText("Press Again");
                        }else {
                            getDataButton.setText("GET DATA");
                            coveringSpinnerArray = tempCoveringSpinnerArray;
                            ArrayAdapter<String> coveringStringAdapterTemp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, coveringSpinnerArray);
                            coveringStringAdapterTemp.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            coveringSpinner.setAdapter(coveringStringAdapterTemp);
                        }

                    } catch(InterruptedException e){
                        System.out.println("Fail at check 3");
                    }
                });
            });


        });

        //ArrayList<String> finalTempCoveringSpinnerArray = tempCoveringSpinnerArray;
        goButton.setOnClickListener(v->{
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) {
                //This is where you call MyTask, the Async thing, so just do another one for getting covering data
                //new MyTask().execute();


                //This is where you try to add an ExecutorService to replace MyTask.
                //Seems to work? Feel like I need the last two functions that were at the end of MyTask but apparently not
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(() -> {
                    //onPre
                    runOnUiThread(() -> {
                        //
                        //System.out.println("This is pre");
                        try {
                            Float.parseFloat(enterDegradation.getText().toString());
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Values must be numbers", Toast.LENGTH_SHORT).show();
                            //cancel(true);
                        }
                    });
                    //background
                    //
                    //System.out.println("This is background");
                    goButton.setText("Calculating...");

                    //onPost
                    runOnUiThread(() -> {
                        //
                        //System.out.println("This is post");
                        goButton.setText("GO!");

                        //Toast.makeText(MainActivity.this, "!!!!!!!!"+coveringType, Toast.LENGTH_SHORT).show();
                        try {
                            if(!(coveringType==null)) {
                                if (Float.parseFloat(enterDegradation.getText().toString()) < 100 && Float.parseFloat(enterDegradation.getText().toString()) >= 0) {
                                    //Okay, this works but remember it's using UVFen, not just a UV dose.
                                    //So, I need to check if UVFen is constant in growing environments or find a way to get it from the dose
                                    //Seems like the later would be easy if I got a breakdown of dose by wavelength, but don't know if that's possible
                                    Project project = new Project();
                                    //Need to add error message if it fails to write to the database.
                                    //project.saveToDatabase(Float.parseFloat(enterStartQuantity.getText().toString()), Float.parseFloat(enterUVDose.getText().toString()), Float.parseFloat(enterHours.getText().toString()), Float.parseFloat(enterGrowTemp.getText().toString()), Float.parseFloat(enterDegradation.getText().toString()), resultOutput.getText().toString(), coveringType, latitude, longitude, pesticideType, uvFen);
                                    project.saveToDatabase(Float.parseFloat(enterStartQuantity.getText().toString()), Float.parseFloat(enterUVDose.getText().toString()), Float.parseFloat(enterHours.getText().toString()), Float.parseFloat(enterGrowTemp.getText().toString()), Float.parseFloat(enterDegradation.getText().toString()), coveringType, pesticideType, uvFen, username);
                                    //Toast.makeText(MainActivity.this, "Calculation Finished.\n" + resultOutput.getText().toString(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(MainActivity.this, "Calculation Finished.\nProject is being saved...", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Degradation must be between 0 and 100%", Toast.LENGTH_SHORT).show();
                                }
                            } else{
                                //I don't know why this isn't showing, I think it's because the IF is wrong and that's what's crashing.
                                Toast.makeText(MainActivity.this, "Please press GET DATA", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Values must be numbers", Toast.LENGTH_SHORT).show();
                        }
                    });
                });


            }
            else {
                Toast.makeText(MainActivity.this, "Please connect ot the internet.", Toast.LENGTH_SHORT).show();
            }
        });

        //ArrayList<String> finalTempCoveringSpinnerArray1 = tempCoveringSpinnerArray;
        //ArrayList<String> finalTempCoveringSpinnerArray2 = tempCoveringSpinnerArray;
        databaseButton.setOnClickListener(v->{


            //Check if the app is connected
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
                                //Start new activity, saving the project with DataLog, which is the database view
                                //Copy this section to open the loading page
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
    }

    public void openNewActivity(String dataString){
        Intent intent = new Intent(this, DataLog.class);
        intent.putExtra("data_string", dataString);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    //Oh they're here too that explains it.
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){spinnerSwitch(parent,pos,id);}
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onLocationChanged(Location location) {currentLocation = location;}

    class getCoveringsAsFuture implements Callable<ArrayList<String>>{

        @Override
        public ArrayList<String> call() throws Exception {
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
                                System.out.println("Raw data string" + dataString);
                                //This is super janky
                                String[] coveringNamesRaw = dataString.toString().split("name=");
                                String[] coveringNamesTrimmed = Arrays.copyOfRange(coveringNamesRaw, 1, coveringNamesRaw.length);
                                //lets try and improve
                                String dataStringTrim = dataString.toString().substring(1,dataString.length()-1);
                                System.out.println("Raw and trimmed: "+dataStringTrim);
                                String[] coveringsData = dataStringTrim.toString().split("\\}\\{");
                                for(String i : coveringsData){
                                    System.out.println("individual data: "+i);
                                    String[] coveringValues = i.split(",");
                                    System.out.println("UV Rate: "+coveringValues[0].split("=")[1]+"\nUV Fen: "+coveringValues[1].split("=")[1]+"\nName: "+coveringValues[2].split("=")[1]);
                                    Float rate = Float.parseFloat(coveringValues[0].split("=")[1]);
                                    Float fen = Float.parseFloat(coveringValues[1].split("=")[1]);
                                    String name = coveringValues[2].split("=")[1];
                                    boolean newCovering = true;
                                    for (String j: coveringSpinnerArrayUsingFuturesTemp){
                                        if(j.equals(name)){
                                            newCovering = false;
                                        }
                                    }
                                    if(newCovering) {
                                        coveringSpinnerArrayUsingFuturesTemp.add(name);
                                        coveringsObjectArray.add(new Covering(name,fen,rate));
                                    }
                                    /*for (String j : coveringValues){
                                        System.out.println("    individual data: "+j);
                                        System.out.println("        individual data: "+j.split("=")[1]);
                                        try {
                                            System.out.println("        individual data: " + Float.parseFloat(j.split("=")[1]) + 10000);
                                        } catch(Exception e){
                                            System.out.println("        individual data NAME: " + j.split("=")[1]);
                                        }
                                    }*/
                                }

                                /*
                                for(String i : coveringNamesTrimmed) {
                                    System.out.println("Trimmed names: "+i);
                                    String[] tempArray = i.split("\\}");
                                    boolean newCovering = true;
                                    for (String j: coveringSpinnerArrayUsingFuturesTemp){
                                        if(j.equals(tempArray[0])){
                                            newCovering = false;
                                        }
                                    }
                                    if(newCovering) {
                                        coveringSpinnerArrayUsingFuturesTemp.add(tempArray[0]);
                                        coveringsObjectArray.add(new Covering(tempArray[0],1,2));
                                    }
                                }*/
                            } else {
                                System.out.println("Fail at check 1");
                            }
                        });
            } else{
                System.out.println("Fail at check 2");
            }
            return coveringSpinnerArrayUsingFuturesTemp;
        }
    }


    //I think this will need changing?
    //This is the problem
    //Yeah, it's the order they're in that's wrong
    @SuppressLint("NonConstantResourceId")
    private void spinnerSwitch(AdapterView<?> parent, int pos, long id){
        switch (parent.getId()) {
            case R.id.covering_spinner:
                for(Covering i:coveringsObjectArray){
                    if(i.getCoveringName().equals(coveringSpinner.getSelectedItem().toString())){
                        //Toast.makeText(MainActivity.this,i.getCoveringName(),Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this,"UV Fen: "+i.getUVFen(),Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this,"UV Rate: "+i.getUVRate(),Toast.LENGTH_SHORT).show();
                        uvFen = i.getUVFen();
                        uvRate = i.getUVRate();
                        coveringType = i.getCoveringName();
                        break;
                    }
                }
                //Toast.makeText(MainActivity.this, "Spinning! coverings", Toast.LENGTH_SHORT).show();
                /*parent.getItemAtPosition(pos);
                switch ((int) id) {
                    case 3: //Transparent
                        uvFen = (float) 0.035078273;
                        uvRate = (float) 46.48309932;
                        coveringType = "Transparent";
                        break;
                    case 0: //Opaque
                        uvFen = (float) 0.0003045083;
                        uvRate = (float) 2.707452846;
                        coveringType = "Opaque";
                        break;
                    case 2: //Standard
                        uvFen = (float) 0.00801709;
                        uvRate = (float) 26.48524698;
                        coveringType = "Standard";
                        break;
                    case 1: //No film
                        uvFen = (float) 0.043617209;
                        uvRate = (float) 55.94852496;
                        coveringType = "No film";
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Error fetching covering type.", Toast.LENGTH_SHORT).show();
                        break;
                }*/
                break;
            case R.id.pesticide_spinner:
                //Toast.makeText(MainActivity.this, "Spinning! pesticides", Toast.LENGTH_SHORT).show();
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