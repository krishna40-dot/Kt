package com.taxiappclone.driver.adapter;

/**
 * Created by Adee on 1/15/2018.
 */

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taxiappclone.common.app.CommonUtils;
import com.taxiappclone.common.model.RiderAddress;
import com.taxiappclone.driver.R;
import com.taxiappclone.driver.activity.SelectPlaceActivity;
import com.taxiappclone.driver.activity.StoredLocationsActivity;
import com.taxiappclone.common.app.AppConfig;
import com.taxiappclone.driver.dialog.AddLocationDialog;
import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.utils.ServerUtilities;

import java.util.HashMap;
import java.util.List;

import static com.taxiappclone.common.utils.ServerUtilities.DELETE;

public abstract class StoredLocationsListAdapter extends RecyclerView.Adapter<StoredLocationsListAdapter.MyViewHolder> {
    private final SessionManager session;
    private final ProgressDialog pDialog;
    private Context context;
    private List<RiderAddress> storedLocationItems;
    private AddLocationDialog editDialog;
    private AsyncTask<Void, Void, String> mRegisterTask;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, address;
        public ImageView imgAddressType;
        public ImageButton btnEdit, btnDelete;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txt_name);
            address = (TextView) view.findViewById(R.id.txt_address);
            imgAddressType = (ImageView) view.findViewById(R.id.img_address_type);
            btnEdit = (ImageButton) view.findViewById(R.id.btn_edit);
            btnDelete = (ImageButton) view.findViewById(R.id.btn_delete);
        }
    }

    public StoredLocationsListAdapter(Context context, List<RiderAddress> storedLocationItems) {
        this.context = context;
        this.storedLocationItems = storedLocationItems;
        session = new SessionManager(context);
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.list_item_stored_location;
        if (context.getClass().getSimpleName().equals(SelectPlaceActivity.class.getSimpleName())) {
            layout = R.layout.list_item_stored_location_2;
        }
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final RiderAddress location = storedLocationItems.get(position);
        holder.name.setText(location.getName());
        holder.address.setText(CommonUtils.trimString(location.getAddress_one(), 20));
        switch (location.getAddress_type().toLowerCase()) {
            case "home":
                //holder.imgAddressType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_home_black_24dp));
                holder.imgAddressType.setImageResource(R.drawable.ic_home_black_24dp);
                break;
            case "office":
                holder.imgAddressType.setImageResource(R.drawable.ic_work_black_24dp);
                break;
            default:
                holder.imgAddressType.setImageResource(R.drawable.ic_star_black_24dp);
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(location);
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit(location);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure want to delete?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        delete(location);

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public abstract void click(RiderAddress riderAddress);

    public abstract void edit(RiderAddress riderAddress);

    public abstract void delete(RiderAddress riderAddress);

    @Override
    public int getItemCount() {
        return storedLocationItems.size();
    }
}

