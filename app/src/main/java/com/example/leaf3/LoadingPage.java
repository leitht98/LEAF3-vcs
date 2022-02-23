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
            service.execute(new Runnable() {
                @Override
                public void run() {
                    //onPre
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //
                            System.out.println("This is pre");
                            System.out.println(">>>"+tempCoveringSpinnerArray.size());
                        }
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                        }
                    });
                }
            });
            //This is the jankiest thing I've ever done. Of course it didn't work
            //Just need to learn how to make the damn thing wait. That'll be tomorrow's mission
            //Well done for putting in 3 hours today though.
            /*service.execute(new Runnable() {
                @Override
                public void run() {
                    //onPre
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //
                            System.out.println("This is pre");
                            System.out.println(">>>"+tempCoveringSpinnerArray.size());
                        }
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                        }
                    });
                }
            });*/
        });
    }

    //Pretty sure I can delete this. The whole class is probably going to get deleted.
    //I think the plan is - have one default covering and pesticide hardcoded
    //Then change the get data button into an update spinners button
    //use it to replace the default one with current data from firestore
    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<Void, Void, Void> {
        //String result;

        @SuppressLint("SetTextI18n")
        @Override
        protected Void doInBackground(Void... voids) {
            //Toast.makeText(LoadingPage.this, "This would be where you load the data", Toast.LENGTH_SHORT).show();
            x = 1;
            //Okay,so here we load in the data. Then we figure out how to pass it back.
            //Try launching this from the start, might be able to ditch the button. Works!
            // But use the button for now, seems a little less janky, also add a back button here for now
            //For some reason this breaks?
            System.out.println("1111$$$$$$$ "+dataStore);
            tempCoveringSpinnerArray = getCoveirngs();
            System.out.println("3333$$$$$$$ "+dataStore);
            return null;
        }

        public ArrayList<String> getCoveirngs(){
            //Toast.makeText(LoadingPage.this, "MaybE?????Please?", Toast.LENGTH_SHORT).show();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            ArrayList<String> tempCoveringSpinnerArray = new ArrayList<>();
            AtomicReference<String> testString = new AtomicReference<>("");

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
                                for(String i:tempCoveringSpinnerArray) {
                                    System.out.println("IT???~ "+i);
                                    dataStore += i;
                                    System.out.println("datastore being built: "+dataStore);
                                    testString.set(dataStore);
                                }
                                //tempCoveringSpinnerArray = coveringNamesTrimmed;
                            } else {
                                //Toast.makeText(MainActivity.this, "Please connect ot the internet.", Toast.LENGTH_SHORT).show();
                            }
                        /*Toast.makeText(MainActivity.this, ""+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
                        for(String j : tempCoveringSpinnerArray){
                            Toast.makeText(MainActivity.this, j, Toast.LENGTH_SHORT).show();
                        }*/
                        });
            } else{
                //Toast.makeText(MainActivity.this, "Please connect ot the internet.", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(MainActivity.this, "Okay, what's going on"+tempCoveringSpinnerArray.size(), Toast.LENGTH_SHORT).show();
            //System.out.println("IT??? "+tempCoveringSpinnerArray.size());
            //for(String i:tempCoveringSpinnerArray) {
                //System.out.println("IT??? "+i);
            //}
            System.out.println("2222$$$$$$$ "+dataStore);
            System.out.println("test string??? "+testString);
            return tempCoveringSpinnerArray;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(LoadingPage.this, "This should come at the end "+x, Toast.LENGTH_SHORT).show();

            for(String i:tempCoveringSpinnerArray) {
                Toast.makeText(LoadingPage.this, "IT??? "+i, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(LoadingPage.this, "##### "+dataStore, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Toast.makeText(LoadingPage.this, "This is the start", Toast.LENGTH_SHORT).show();
            //tempCoveringSpinnerArray = getCoveirngs();
            try {
                Toast.makeText(LoadingPage.this, "This would be a test", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                    Toast.makeText(LoadingPage.this, "fucked.", Toast.LENGTH_SHORT).show();
                    cancel(true);
                }
            }
        }
    }

