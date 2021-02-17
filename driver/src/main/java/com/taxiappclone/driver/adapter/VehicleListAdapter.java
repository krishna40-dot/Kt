package com.taxiappclone.driver.adapter;

/**
 * Created by Adee on 1/15/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.taxiappclone.driver.R;
import com.taxiappclone.driver.activity.SelectPaymentActivity;
import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.driver.model.VehicleListItem;

import java.util.List;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.MyViewHolder>
{
    private final SessionManager session;
    private Context context;
    private List<VehicleListItem> vehicleListItems;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvVehicleName, tvVehicleDescription, tvPassengerCapacity, tvLuggageCapacity, tvAmount;
        public ImageView imgVehicle;
        public Button btnBookNow, btnViewMore;

        public MyViewHolder(View view) {
            super(view);
            tvVehicleName = (TextView) view.findViewById(R.id.tv_vehicle_name);
            tvVehicleDescription = (TextView) view.findViewById(R.id.tv_vehicle_description);
            tvPassengerCapacity = (TextView) view.findViewById(R.id.tv_passenger_capacity);
            tvLuggageCapacity = view.findViewById(R.id.tv_luggage_capacity);
            tvAmount = view.findViewById(R.id.tv_amount);
            imgVehicle = (ImageView)view.findViewById(R.id.img_vehicle);
            btnBookNow = view.findViewById(R.id.btn_book_now);
            btnViewMore = view.findViewById(R.id.btn_view_more);
        }
    }

    public VehicleListAdapter(Context context, List<VehicleListItem> vehicleListItems) {
        this.context = context;
        this.vehicleListItems = vehicleListItems;
        session = new SessionManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.list_item_vehicles;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final VehicleListItem item = vehicleListItems.get(position);

        holder.tvVehicleName.setText(item.vehicleName);
        holder.tvVehicleDescription.setText(Html.fromHtml(CommonUtils.trimString(item.vehicleDescription,30)));
        Glide.with(context).load(item.imgUrl).into(holder.imgVehicle);
        holder.tvPassengerCapacity.setText(String.valueOf(item.passengerCapacity));
        holder.tvLuggageCapacity.setText(String.valueOf(item.luggageCapacity));
        holder.tvAmount.setText("₹"+String.valueOf(item.amount));
        holder.btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) context;
                Intent intent = new Intent(activity,SelectPaymentActivity.class);
                intent.putExtra("search_result_id",item.vehicleId);
                intent.putExtra("original_amount",item.amount);
                activity.startActivity(intent);
            }
        });
        holder.btnViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.tvVehicleDescription.getText().length()>33)
                    holder.tvVehicleDescription.setText(Html.fromHtml(CommonUtils.trimString(item.vehicleDescription,30)));
                else
                    holder.tvVehicleDescription.setText(Html.fromHtml(item.vehicleDescription));
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleListItems.size();
    }

}