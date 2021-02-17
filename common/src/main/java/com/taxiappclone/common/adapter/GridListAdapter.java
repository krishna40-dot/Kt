package com.taxiappclone.common.adapter;

/**
 * Created by Adee on 3/31/2016.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxiappclone.common.R;
import com.taxiappclone.common.view.CircleImageView;
import com.taxiappclone.common.model.GridListItem;
import java.util.List;


public class GridListAdapter extends RecyclerView.Adapter<GridListAdapter.MyViewHolder>
{

    private Context context;
    private List<GridListItem> gridListItems;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public CircleImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.item_name);
            imageView = (CircleImageView) view.findViewById(R.id.item_icon);
        }
    }

    public GridListAdapter(Context context, List<GridListItem> gridListItems) {
        this.context = context;
        this.gridListItems = gridListItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layout = -1;
        switch (viewType)
        {
            case GridListItem.GRID_LIST_LAYOUT:
                layout = R.layout.grid_list_row;
                break;
            case GridListItem.GRID_LIST_LAYOUT_1:
                layout = R.layout.grid_list_1_row;
                break;
            case GridListItem.GRID_LIST_LAYOUT_2:
                layout = R.layout.grid_list_2_row;
                break;
            case GridListItem.GRID_CATRGORY_LIST_LAYOUT:
                layout = R.layout.category_item_row;
                break;
            case GridListItem.GRID_CATEGORY_TITLE:
                layout = R.layout.category_title_row;
                break;
            case GridListItem.GRID_LIST_LAYOUT_SMALL:
                layout = R.layout.list_item_small_grid;
                break;
        }
        /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new MyViewHolder(itemView);*/

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);


        return new MyViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GridListItem item = gridListItems.get(position);
        holder.itemName.setText(item.getItemName());
        holder.imageView.setImageResource(item.getItemIcon());
        //holder.imageView.setColorFilter(context.getResources()getColor(item.getColor()));
    }

    @Override
    public int getItemCount() {
        return gridListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return gridListItems.get(position).getLayout();
    }
}

