package com.mbsindia.rider.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.app.Constants;
import com.taxiappclone.common.model.VehicleType;
import com.mbsindia.rider.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.mbsindia.rider.app.ModuleConfig.URL_UPLOAD_PATH;

public class VehicleTypeDetailFragment extends BottomSheetDialogFragment {
    private static final String ARG_PARAM1 = "vehicle_detail";
    @BindView(R.id.img_vehicle)
    ImageView imgVehicle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tvCapacity)
    TextView tvCapacity;
    @BindView(R.id.tvFare)
    TextView tvFare;
    @BindView(R.id.btnMoreDetails)
    Button btnMoreDetails;
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.itemView)
    LinearLayout itemView;

    private VehicleType vehicleType;
    private Unbinder unbinder;
    private double distance;
    private int duration;
    private double totalFare;

    public VehicleTypeDetailFragment() {
    }

    public static VehicleTypeDetailFragment newInstance(String param1,double distance,int duration) {
        VehicleTypeDetailFragment fragment = new VehicleTypeDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putDouble("distance",distance);
        args.putInt("duration",duration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicleType = new Gson().fromJson(getArguments().getString(ARG_PARAM1), VehicleType.class);
            distance = getArguments().getDouble("distance");
            duration = getArguments().getInt("duration");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vehicle_type_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Glide.with(getActivity()).load(URL_UPLOAD_PATH+"vehicle_types/"+vehicleType.getImage()).into(imgVehicle);
        tvName.setText(vehicleType.getName());
        tvType.setText(vehicleType.getType());
        tvCapacity.setText(String.valueOf(vehicleType.getCapacity()));
        totalFare = vehicleType.getBase_fare()+(distance*vehicleType.getPer_km_charge())+((duration/60)*vehicleType.getPer_min_charge());
        tvFare.setText(Constants.CURRENCY_SYMBOL + CommonUtils.roundDouble(totalFare));
        btnMoreDetails.setVisibility(View.GONE);
        return rootView;
    }

    @OnClick({R.id.btnMoreDetails, R.id.btnBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnMoreDetails:
                break;
            case R.id.btnBack:
                this.dismiss();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}