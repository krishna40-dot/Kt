package com.mbsindia.rider.activity;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mbsindia.rider.R;
import com.mbsindia.rider.adapter.VehicleListAdapter;
import com.taxiappclone.common.helper.SessionManager;
import com.mbsindia.rider.model.VehicleListItem;
import com.taxiappclone.common.utils.RecyclerViewClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class SelectVehicleActivity extends AppCompatActivity {

    public static final String TAG = SelectVehicleActivity.class.getSimpleName();
    private Toolbar toolbar;
    private SessionManager session;
    private ActionBar actionBar;

    private RecyclerView listView;
    private VehicleListAdapter listAdapter;
    private List<VehicleListItem> listItems = new ArrayList<>();
    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vehicle);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Select Vehicle");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        listView = findViewById(R.id.list_view);
        listAdapter = new VehicleListAdapter(this, listItems);
        listView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        try {
            listView.setAdapter(listAdapter);
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        listView.addOnItemTouchListener(new RecyclerViewClickListener.RecyclerTouchListener(this, listView, new RecyclerViewClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                final VehicleListItem item = listItems.get(position);

                /*Intent intent = new Intent(SelectVehicleActivity.this,SelectPaymentActivity.class);
                //intent.putExtra("address",item.address);
                setResult(RESULT_OK,intent);
                startActivity(intent);*/
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        getVehicles();
    }

    private void getVehicles() {
        try {
            VehicleListItem item;
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("json_data"));
            JSONArray jArr = jsonObject.getJSONArray("results");

            for(int i =0; i<jArr.length();i++)
            {
                JSONObject jObj = jArr.getJSONObject(i);
                item = new VehicleListItem(jObj.getInt("id"),jObj.getInt("vehicle_type_id"),jObj.getString("vehicle_type_name"),jObj.getString("vehicle_type_description"),jObj.getJSONArray("vehicle_type_images").getString(0),jObj.getInt("passenger_capacity"),jObj.getInt("luggage_capacity"),jObj.getDouble("total_amount"));
                listItems.add(item);
            }
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}