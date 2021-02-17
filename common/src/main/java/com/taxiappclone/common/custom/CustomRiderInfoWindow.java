package com.taxiappclone.common.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.taxiappclone.common.R;

/**
 * Created by Mac on 1/8/2018.
 */

public class CustomRiderInfoWindow implements GoogleMap.InfoWindowAdapter {

    View myView;

    public CustomRiderInfoWindow(Context context)
    {
        myView = LayoutInflater.from(context)
                .inflate(R.layout.custom_rider_info_window,null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txtPickupTitle = (TextView)myView.findViewById(R.id.txt_pickup_info);
        txtPickupTitle.setText(marker.getTitle());

        TextView txtPickupSnippet = (TextView)myView.findViewById(R.id.txt_pickup_snippet);
        txtPickupSnippet.setText(marker.getSnippet());

        return myView;

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
