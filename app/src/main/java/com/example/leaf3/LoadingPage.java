package com.example.leaf3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

//Okay, this crashes for some reason. Compare with others

public class LoadingPage extends AppCompatActivity {
    TextView message;
    Button backButton, loadButton;
    int x = 0;
    ArrayList<String> tempCoveringSpinnerArray = new ArrayList<>();
    String dataStore = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);
        message = findViewById(R.id.loading_message);
        backButton = findViewById(R.id.backButton);
        loadButton = findViewById(R.id.loadButton);
        Intent intent = getIntent();
        String testMessage = intent.getStringExtra("test_message");
        Toast.makeText(LoadingPage.this, testMessage, Toast.LENGTH_SHORT).show();
        tempCoveringSpinnerArray.add("testing");
        //new MyTask().execute();
        backButton.setOnClickListener(v -> this.finish());
        loadButton.setOnClickListener(v ->{
            //new MyTask().execute();
            //System.out.println("final IT??? "+tempCoveringSpinnerArray.size());
            //for(String i:tempCoveringSpinnerArray) {
                //System.out.println("final IT??? "+i);
            //}

            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(() -> {
                //onPre
                runOnUiThread(() -> {
                    //
                    System.out.println("This is pre");
                    System.out.println(">>>"+tempCoveringSpinnerArray.size());
                });
                //background
                //
                System.out.println("This is background");
                System.out.println(">>>"+tempCoveringSpinnerArray.size());

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
                        message.setText(coveringNamesString);

                    } catch(InterruptedException e){
                        System.out.println("Fail at check 3");
                    }
                });
            });

        });
    }
    }

