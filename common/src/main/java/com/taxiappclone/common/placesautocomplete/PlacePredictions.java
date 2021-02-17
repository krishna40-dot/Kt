package com.taxiappclone.common.placesautocomplete;

import java.util.ArrayList;

public class PlacePredictions {

    public ArrayList<PlaceAutoCompleteItem> getPlaces() {
        return predictions;
    }

    public void setPlaces(ArrayList<PlaceAutoCompleteItem> places) {
        this.predictions = places;
    }

    private ArrayList<PlaceAutoCompleteItem> predictions;
}