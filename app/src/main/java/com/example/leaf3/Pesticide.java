package com.example.leaf3;

import java.math.BigDecimal;

public class Pesticide implements Comparable<Pesticide>{
    String pesticideName;
    //BigDecimal rParam1, rParam2;
    BigDecimal referenceVapourPressure, referenceTemp, molarMass, recommendedApplicationRate;

    public Pesticide(String name,BigDecimal rVapourPressure, BigDecimal rTemp, BigDecimal mMass, BigDecimal rAppRate){
        pesticideName = name;
        //rParam1 = rp1;
        //rParam2 = rp2;
        referenceVapourPressure = rVapourPressure;
        referenceTemp = rTemp;
        molarMass = mMass;
        recommendedApplicationRate = rAppRate;
    }

    public String getName(){return pesticideName;}
    //public BigDecimal getRParam1(){return rParam1;}
    //public BigDecimal getRParam2(){return rParam2;}
    public BigDecimal getReferenceVapourPressure(){return referenceVapourPressure;}
    public BigDecimal getReferenceTemp(){return referenceTemp;}
    public BigDecimal getMolarMass(){return molarMass;}
    public BigDecimal getRecommendedApplicationRate(){return recommendedApplicationRate;}

    @Override
    public int compareTo(Pesticide o) {
        return pesticideName.compareTo(o.pesticideName);
    }
}
