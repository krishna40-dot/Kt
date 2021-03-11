package com.mbsindia.driver.adapter;

/**
 * Created by Adee on 3/15/2018.
 */

import android.app.Dialog;
import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taxiappclone.common.app.CommonUtils;
import com.mbsindia.driver.R;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.MyRidesListItem;

import java.util.List;

import static android.view.View.GONE;

public class MyRidesListAdapter extends RecyclerView.Adapter<MyRidesListAdapter.MyViewHolder> {
    private final SessionManager session;
    private Context context;
    private List<MyRidesListItem> myRidesListItems;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvPickUpAddress, tvDropOffAddress, tvAmount, tvStatus, tvDate;

        public MyViewHolder(View view) {
            super(view);
            tvPickUpAddress = view.findViewById(R.id.tv_pickup_address);
            tvDropOffAddress = view.findViewById(R.id.tv_dropoff_address);
            tvAmount = view.findViewById(R.id.tv_amount);
            tvStatus = view.findViewById(R.id.tv_status);
            tvDate = view.findViewById(R.id.tv_pickUpDate);
        }
    }

    public MyRidesListAdapter(Context context, List<MyRidesListItem> myRidesListItems) {
        this.context = context;
        this.myRidesListItems = myRidesListItems;
        session = new SessionManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.list_item_my_rides;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final MyRidesListItem item = myRidesListItems.get(position);

        holder.tvPickUpAddress.setText(item.getPickUpAddress());
        holder.tvDropOffAddress.setText(item.getDropOffAddress());
        holder.tvAmount.setText("₹" + item.getAmount());
        String date = CommonUtils.convertDateFormat(item.getPickUpDate(), "yyyy-MM-dd'T'HH:mm:ss", "E, dd MMM yyyy, hh:mm a");
        holder.tvDate.setText(date);

        holder.tvStatus.setVisibility(GONE);

        holder.itemView.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_ride_detail);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

            AppCompatTextView tvStatus = dialog.findViewById(R.id.tvStatus);
            AppCompatTextView tvDate = dialog.findViewById(R.id.tvDate);
            AppCompatTextView tvPickup = dialog.findViewById(R.id.tvPickup);
            AppCompatTextView tvDrop = dialog.findViewById(R.id.tvDrop);
            AppCompatTextView tvAmount = dialog.findViewById(R.id.tvAmount);
            AppCompatTextView tvDistance = dialog.findViewById(R.id.tvDistance);

            tvStatus.setText(Html.fromHtml(context.getString(R.string.status) + " " + item.getStatus()));
            tvDate.setText(Html.fromHtml(context.getString(R.string.date) + " " + item.getPickUpDate()));
            tvPickup.setText(Html.fromHtml(context.getString(R.string.pickup_l) + " " + item.getPickUpAddress()));
            tvDrop.setText(Html.fromHtml(context.getString(R.string.drop) + " " + item.getDropOffAddress()));
            tvAmount.setText(Html.fromHtml(context.getString(R.string.fare) + " " + item.getAmount() + " (" + String.format("%.2f", (item.getAmount() / 123) * 100) + "FARE + " + String.format("%.2f", (item.getAmount() / 123) * 23) + " GST)"));
            tvDistance.setText(Html.fromHtml(context.getString(R.string.distance) + " " + item.getDistance()));

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return myRidesListItems.size();
    }

}