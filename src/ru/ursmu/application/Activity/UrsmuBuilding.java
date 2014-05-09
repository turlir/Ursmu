package ru.ursmu.application.Activity;

public class UrsmuBuilding {

    private int build, floor;
    private String audience, mapFlagName, mOriginal;

    public UrsmuBuilding(Integer number_build, Integer floor, String aud, String mapText, String original) {
        this.build = number_build;
        this.floor = floor;
        this.audience = aud;
        this.mapFlagName = mapText;
        this.mOriginal = original;
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
}
