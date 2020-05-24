package com.d.littleinfo;

public class Location {
    String mlatitude,mlongitude;

    public Location() {
    }

    public String getMlatitude() {
        return mlatitude;
    }

    public void setMlatitude(String mlatitude) {
        this.mlatitude = mlatitude;
    }

    public String getMlongitude() {
        return mlongitude;
    }

    public void setMlongitude(String mlongitude) {
        this.mlongitude = mlongitude;
    }

    public Location(String mlatitude, String mlongitude) {
        this.mlatitude = mlatitude;
        this.mlongitude = mlongitude;
    }
}
