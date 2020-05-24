package com.d.littleinfo;

import java.util.ArrayList;

public class User {
    String name, lat, lang, time;
    ArrayList<Location> loclist;

    public User() {
    }

    public User(String name, String lat, String lang, String time) {

    }

    public ArrayList<Location> getLoclist() {
        return loclist;
    }

    public void setLoclist(ArrayList<Location> loclist) {
        this.loclist = loclist;
    }


    public User(String name, String lat, String lang, String time, ArrayList<Location> loclist) {
        this.name = name;
        this.lat = lat;
        this.lang = lang;
        this.time = time;
        this.loclist = loclist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}


