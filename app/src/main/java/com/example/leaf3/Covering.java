package com.example.leaf3;

//Class to store Covering data
public class Covering {
    String coveringName;
    float uvFen, uvRate;

    public Covering(String name, float fen, float rate){
        coveringName = name;
        //These may be replaced with other variables later, uvFen and Rate aren't that easy to get
        uvFen = fen;
        uvRate = rate;
    }

    //Getters, don't need setters yet
    public String getCoveringName() {
        return coveringName;
    }

    public float getUVFen() {
        return uvFen;
    }

    public float getUVRate() {
        return uvRate;
    }
}
