package com.taxiappclone.common.placesautocomplete;


public class PlaceAutoCompleteItem {

    private String place_id;
    private String description;
    private PlaceData structured_formatting;

    private class PlaceData{
        private String main_text;
        private String secondary_text;

        public String getMainText() {
            return main_text;
        }

        public String getSecondaryText() {
            return secondary_text;
        }
    }

    public String getPlaceDesc() {
        return description;
    }

    public String getPlaceID() {
        return place_id;
    }

    public String getTitle() {
        return structured_formatting.getMainText();
    }

    public String getDesc() {
        return structured_formatting.getSecondaryText();
    }
}