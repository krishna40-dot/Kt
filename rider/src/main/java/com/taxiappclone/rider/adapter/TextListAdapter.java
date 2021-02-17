package com.taxiappclone.rider.adapter;

/**
 * Created by Adee on 3/31/2016.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxiappclone.rider.model.TextListItem;
import com.taxiappclone.common.view.CircleImageView;
import com.taxiappclone.rider.R;

import java.util.List;

/*************CHAT Adapter**************/
public class TextListAdapter extends RecyclerView.Adapter<TextListAdapter.MyViewHolder>
{
    private Context context;
    public int layout;
    private List<TextListItem> textListItems;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public CircleImageView imageView;
        public TextView phone;
        public TextView time;
        public TextView price;
        public MyViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.item_name);
            imageView = (CircleImageView) view.findViewById(R.id.item_icon);
        }
    }

    public TextListAdapter(Context context, int layout, List<TextListItem> textListItems) {
        this.context = context;
        this.textListItems = textListItems;
        this.layout = layout;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TextListItem item = textListItems.get(position);
        holder.itemName.setText(item.getItemText());
    }

    @Override
    public int getItemCount() {
        return textListItems.size();
    }
}

