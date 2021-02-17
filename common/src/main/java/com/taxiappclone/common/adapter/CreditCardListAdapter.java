package com.taxiappclone.common.adapter;

/**
 * Created by Adee on 1/15/2018.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taxiappclone.common.R;
import com.taxiappclone.common.helper.SessionManager;
import com.taxiappclone.common.model.CreditCardListItem;

import java.util.List;

public class CreditCardListAdapter extends RecyclerView.Adapter<CreditCardListAdapter.MyViewHolder>
{
    private final SessionManager session;
    private Context context;
    private List<CreditCardListItem> creditCardListItems;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvCardNumber, tvCardNetwork;
        public ImageView imgCardType;
        public ImageButton btnEdit, btnDelete;

        public MyViewHolder(View view) {
            super(view);
            tvCardNumber = view.findViewById(R.id.tv_card_number);
            tvCardNetwork = view.findViewById(R.id.tv_card_network);
            imgCardType = view.findViewById(R.id.img_taxi);
            btnEdit = view.findViewById(R.id.btn_edit);
            btnDelete = view.findViewById(R.id.btn_delete);
        }
    }

    public CreditCardListAdapter(Context context, List<CreditCardListItem> creditCardListItems) {
        this.context = context;
        this.creditCardListItems = creditCardListItems;
        session = new SessionManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.list_item_credit_card;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CreditCardListItem item = creditCardListItems.get(position);

        holder.tvCardNumber.setText(item.cardFirstDigit+"XXXXXXXXXXX"+item.cardLastDigits);
        String cardNetwork = item.cardNetwork.replace("_"," ").toUpperCase();
        holder.tvCardNetwork.setText(cardNetwork);
        holder.btnEdit.setVisibility(View.GONE);
        if(context.getClass().getSimpleName().equals("SelectPaymentActivity")){
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Toast.makeText(context,"Development in progress!",Toast.LENGTH_SHORT).show();
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Development in progress!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return creditCardListItems.size();
    }

}