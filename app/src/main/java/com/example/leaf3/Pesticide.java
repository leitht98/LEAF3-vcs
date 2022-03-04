package com.example.leaf3;

import java.math.BigDecimal;

public class Pesticide {
    String pesticideName;
    BigDecimal rParam1, rParam2;

    public Pesticide(String name, BigDecimal rp1, BigDecimal rp2){
        pesticideName = name;
        rParam1 = rp1;
        rParam2 = rp2;
    }

    public String getName(){
        return pesticideName;
    }

    public BigDecimal getRParam1(){
        return rParam1;
    }

    public BigDecimal getRParam2(){
        return rParam2;
    }
}
