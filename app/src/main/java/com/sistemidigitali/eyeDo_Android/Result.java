package com.sistemidigitali.eyeDo_Android;

public class Result {
    private String outClass;
    private float[] coordinates;

    public Result(String outClass, float[] coordinates) {
        this.outClass = outClass;
        this.coordinates = coordinates;
    }

    public String getOutClass() {
        return outClass;
    }

    public float[] getCoordinates() {
        return coordinates;
    }
}
