package com.example.leaf3;

public class Project {
    private String date;
    public Project(){}

    public Project(String date) {
        System.out.println("Step 1");
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
