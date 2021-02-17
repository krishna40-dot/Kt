package com.taxiappclone.common.model;

/**
 * Created by Adee on 3/30/2016.
 */
public class GridListItem {

    public static final int GRID_LIST_LAYOUT = 0;
    public static final int GRID_LIST_LAYOUT_1 = 1;
    public static final int GRID_LIST_LAYOUT_2 = 2;
    public static final int GRID_CATRGORY_LIST_LAYOUT = 3;
    public static final int GRID_CATEGORY_TITLE = 4;
    public static final int GRID_LIST_LAYOUT_5 = 5;
    public static final int GRID_LIST_LAYOUT_SMALL = 6;

    private String itemName;
    private int itemIcon;
    private int layout;
    private String activity;

    public GridListItem(String itemName, int itemIcon,int layout, String activity)
    {
        this.itemName = itemName;
        this.itemIcon = itemIcon;
        this.layout = layout;
        this.activity = activity;
    }

    public GridListItem(String itemName, int itemIcon)
    {
        this.itemName = itemName;
        this.itemIcon = itemIcon;
    }

    public String getItemName()
    {
        return this.itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public int getItemIcon()
    {
        return this.itemIcon;
    }

    public void setItemIcon(int itemIcon)
    {
        this.itemIcon = itemIcon;
    }

    public int getLayout()
    {
        return this.layout;
    }
    public void setLayout(int layout)
    {
        this.layout = layout;
    }
    public String getActivityName()
    {
        return this.activity;
    }
    public void setActivityName(String activity)
    {
        this.activity = activity;
    }

}
