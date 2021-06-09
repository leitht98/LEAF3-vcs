package com.example.leaf3;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProjectDAO {
    private DatabaseReference databaseReference;

    public ProjectDAO(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Project.class.getSimpleName());
    }

    public Task<Void> add(Project prj){
        //if(prj==null) throw exception //Data validation here
        return databaseReference.push().setValue(prj);
    }
}
