package com.taxiappclone.driver.placesautocomplete;

/**
 * Created by Adee on 3/31/2016.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxiappclone.driver.R;

import java.util.List;


public class PlaceAutoCompleteListAdapter extends RecyclerView.Adapter<PlaceAutoCompleteListAdapter.MyViewHolder>
{
    private Context context;
    private List<PlaceAutoCompleteItem> placeAutoCompleteItemListItems;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public PlaceAutoCompleteItem Place;
        public TextView name, location;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.place_name);
            location = (TextView) view.findViewById(R.id.place_detail);
        }
    }

    public PlaceAutoCompleteListAdapter(Context context, List<PlaceAutoCompleteItem> placeAutoCompleteItemListItems) {
        this.context = context;
        this.placeAutoCompleteItemListItems = placeAutoCompleteItemListItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_autoplace, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.Place = placeAutoCompleteItemListItems.get(position);

        holder.name.setText(holder.Place.getTitle());
        holder.location.setText(holder.Place.getDesc());
    }

    public void updateList(List<PlaceAutoCompleteItem> newItems)
    {
        placeAutoCompleteItemListItems = newItems;
    }

    public void clear()
    {
        placeAutoCompleteItemListItems.clear();
    }

    @Override
    public int getItemCount() {
        return placeAutoCompleteItemListItems.size();
    }

}

