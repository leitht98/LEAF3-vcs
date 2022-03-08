package com.example.leaf3;

import java.math.BigDecimal;

//Class to store Covering data
public class Covering {
    String coveringName;
    BigDecimal uvFen, uvRate;

    public Covering(String name, BigDecimal fen, BigDecimal rate){
        coveringName = name;
        //These may be replaced with other variables later, uvFen and Rate aren't that easy to get
        uvFen = fen;
        uvRate = rate;
    }

    //Getters, don't need setters yet
    public String getCoveringName() {return coveringName;}
    public BigDecimal getUVFen() {return uvFen;}
    public BigDecimal getUVRate() {return uvRate;}
}
