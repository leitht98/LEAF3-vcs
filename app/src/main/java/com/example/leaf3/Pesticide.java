package com.example.leaf3;

public class Pesticide {
    String pesticideName;
    float rParam1, rParam2;

    public Pesticide(String name, float rp1, float rp2){
        pesticideName = name;
        rParam1 = rp1;
        rParam2 = rp2;
    }

    public String getName(){
        return pesticideName;
    }

    public float getRParam1(){
        return rParam1;
    }

    public float getRParam2(){
        return rParam2;
    }
}
