package com.taxiappclone.rider.Fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxiappclone.common.remote.IGoogleAPI;
import com.taxiappclone.rider.R;
import com.taxiappclone.common.remote.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mac on 1/7/2018.
 */
public class BottomSheetRiderFragment extends BottomSheetDialogFragment{

    String mSource, mDestination;
    private IGoogleAPI mService;
    private TextView tvSource, tvDestination, tvDistance;

    public static BottomSheetRiderFragment newInstance(String source, String destination)
    {
        BottomSheetRiderFragment f = new BottomSheetRiderFragment();
        Bundle args = new Bundle();
        args.putString("source",source);
        args.putString("destination",destination);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSource = getArguments().getString("source");
        mDestination = getArguments().getString("destination");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottom_sheet_rider,container,false);

        tvSource = (TextView)view.findViewById(R.id.tv_source);
        tvDestination = (TextView)view.findViewById(R.id.tv_destination);
        tvDistance = (TextView)view.findViewById(R.id.tv_distance);

        mService = Common.getGoogleAPI();

        tvSource.setText(mSource);
        tvDestination.setText(mDestination);

        getDistance(mSource,mDestination);
        return view;
    }

    private void getDistance(String mSource, String mDestination) {
        String requestUrl = null;
        try {
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+mSource+"&"+
                    "destination="+mDestination+"&"+
                    "key="+getResources().getString(R.string.google_api_browser_key);
            mService.getPath(requestUrl).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        JSONArray routes = jsonObject.getJSONArray("routes");

                        JSONObject object = routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");

                        JSONObject legsObject = legs.getJSONObject(0);

                        //Get Distance
                        JSONObject distance = legsObject.getJSONObject("distance");
                        String distance_text =  distance.getString("text");
                        Double distance_value = Double.parseDouble(distance_text.replaceAll("[^0-9\\\\.]+",""));

                        //Get Time
                        JSONObject time = legsObject.getJSONObject("duration");
                        String time_text =  distance.getString("text");

                        Integer time_value = Integer.parseInt(time_text.replaceAll("\\D+",""));

                        tvDistance.setText("Distance: "+distance_value+" km (Travel Time: "+time_value+" mins)");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("ERROR",t.getMessage());
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
