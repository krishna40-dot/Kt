package com.mbsindia.rider.adapter;

/**
 * Created by Adee on 3/15/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.VehicleType;
import com.mbsindia.rider.R;

import java.util.List;

import static com.mbsindia.rider.app.ModuleConfig.URL_UPLOAD_PATH;

public abstract class VehicleTypesListAdapter extends RecyclerView.Adapter<VehicleTypesListAdapter.MyViewHolder> {
    private final SessionManager session;
    private Context context;
    private List<VehicleType> vehicleTypeList;
    private int selectItemPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgVehicle;
        public TextView tvName, tvTime;
        public TextView tvCost;
        public View itemView;

        public MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvCost = view.findViewById(R.id.tvCost);
            tvTime = view.findViewById(R.id.tv_time);
            itemView = view.findViewById(R.id.itemView);
            imgVehicle = view.findViewById(R.id.img_vehicle);
        }
    }

    public VehicleTypesListAdapter(Context context, List<VehicleType> vehicleTypeList) {
        this.context = context;
        this.vehicleTypeList = vehicleTypeList;
        session = new SessionManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.list_item_vehicle_type;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final VehicleType item = vehicleTypeList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvTime.setText("");
        Glide.with(context).load(URL_UPLOAD_PATH + "vehicle_types/" + item.getImage()).into(holder.imgVehicle);
        setFare(holder.tvCost, item);

        if (item.isSelected()) {
            holder.imgVehicle.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_theme_color));
            holder.imgVehicle.setColorFilter(Color.argb(255, 255, 255, 255));
        } else {
            if (item.isAvailable()) {
                holder.imgVehicle.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_white_with_green_border));
                holder.imgVehicle.setColorFilter(Color.argb(255, 0, 0, 0));
            } else {
                holder.imgVehicle.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_gray));
                holder.imgVehicle.setColorFilter(Color.parseColor("#555555"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return vehicleTypeList.size();
    }

    public void setSelectedItem(int position) {
        this.selectItemPosition = position;

    }

    public int getSelectedItemPosition() {
        return this.selectItemPosition;
    }

    public abstract void setFare(TextView tvCost, VehicleType id);
}