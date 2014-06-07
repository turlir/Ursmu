package ru.ursmu.application.Activity;

public class UrsmuBuilding implements java.io.Serializable {

    private int build, floor;
    private String audience, mapFlagName, mOriginal;
    private double lat, longit;
    private float angle;

    public UrsmuBuilding(Integer number_build, Integer floor, String aud, String mapText, String original,
                         double lat, double longit, float angle) {
        this.build = number_build;
        this.floor = floor;
        this.audience = aud;
        this.mapFlagName = mapText;
        this.mOriginal = original;
        this.lat = lat;
        this.longit = longit;
        this.angle = angle;
    }

    public String getBuild() {
        return String.valueOf(build) + "е";
    }

    public String getAddress() {
        return mapFlagName;
    }

    public String getMapFlagName() {
        return mapFlagName;
    }

    public String getFloor() {
        return String.valueOf(floor);
    }

    public String getAudience() {
        return audience;
    }

    public String getOriginal() {
        return mOriginal;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return longit;
    }

    public float getAngle() {
        return angle;
    }
}
