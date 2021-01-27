package com.vijayjangid.aadharkyc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class WalletManage extends AppCompatActivity implements View.OnClickListener {

    TextView tvb_upgradeNow, tvb_balanceDetails, tvb_transferToBank, tvb_addNewCard;
    Button btn_logout, btn_addMoney;
    ArrayList<NewCard> arrayList;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wallet_fragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Manage Wallet");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);


        idAndListeners();

        recyclerView = (RecyclerView) findViewById(R.id.rv_SavedCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();
        arrayList.add(new NewCard("1111 1111 1111 2345", "12/25", "UCO Bank Debit Card"));
        arrayList.add(new NewCard("1111 1111 1111 2345", "11/30", "Kotak Bank Debit Card"));

        recyclerView.setAdapter(new RecyclerViewAdapter(arrayList));
    }

    void idAndListeners() {
        tvb_upgradeNow = findViewById(R.id.tvb_upgradeNow);
        tvb_balanceDetails = findViewById(R.id.tvb_balanceDetails);
        tvb_transferToBank = findViewById(R.id.tvb_transferToBank);
        btn_addMoney = findViewById(R.id.btn_addMoney);
        btn_logout = findViewById(R.id.btn_logout_wallet);
        tvb_addNewCard = findViewById(R.id.tvb_addNewCard);
        tvb_upgradeNow = findViewById(R.id.tvb_upgradeNow);


        tvb_upgradeNow.setOnClickListener(this);
        tvb_balanceDetails.setOnClickListener(this);
        tvb_transferToBank.setOnClickListener(this);
        btn_addMoney.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        tvb_addNewCard.setOnClickListener(this);
        tvb_upgradeNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvb_upgradeNow:
                startActivity(new Intent(this, Kyc.class));
                break;

            case R.id.tvb_balanceDetails:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new WalletTransactionFragment());
                transaction.commit();
                break;

            case R.id.tvb_transferToBank:
                Toast.makeText(this, "Open money transfer page"
                        , Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_addMoney:
                startActivity(new Intent(this, Add_money_activity.class));
                break;

            case R.id.btn_logout_wallet:
                Toast.makeText(this, "Logout Successful, User will go to login Page"
                        , Toast.LENGTH_SHORT).show();
                break;

            case R.id.tvb_addNewCard:
                startActivity(new Intent(this, AddCardObject.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            while (arrayList.size() != 2) {
                arrayList.remove(arrayList.size() - 1);
            }

            SharedPreferences sp = this.getSharedPreferences("SavedCardInformation",
                    Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sp.getString("Card", "");
            if (!json.equals("")) {
                NewCard obj = gson.fromJson(json, NewCard.class);
                arrayList.add(obj);
                recyclerView.setAdapter(new RecyclerViewAdapter(arrayList));
            }
        } catch (Exception ignored) {

        }
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private ArrayList<NewCard> arrayList1;

        // RecyclerView recyclerView;
        public RecyclerViewAdapter(ArrayList<NewCard> listData) {
            this.arrayList1 = listData;
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.listview_saved_card, parent, false);
            RecyclerViewAdapter.ViewHolder viewHolder = new RecyclerViewAdapter.ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

            NewCard newCard = arrayList1.get(position);
            String cardNumber = newCard.cardNumber.substring(15, 19);
            holder.tv_cardNumber.setText("xxxx xxxx xxxx " + cardNumber);
            holder.tv_cardName.setText(newCard.cardName);
            holder.tv_expires.setText("Expires\n" + newCard.expiryDate);

        }

        @Override
        public int getItemCount() {
            return arrayList1.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_cardNumber, tv_cardName, tv_expires;
            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_cardName = itemView.findViewById(R.id.tvrv_cardName);
                tv_cardNumber = itemView.findViewById(R.id.tvrv_cardNumber);
                tv_expires = itemView.findViewById(R.id.tvrv_expires);
                imageView = itemView.findViewById(R.id.rvtv_imageview);
            }
        }

    }

    public class NewCard {

        String cardNumber;
        String expiryDate;
        String cardName;

        public NewCard(String cardNumber, String expiryDate, String cardName) {
            this.cardNumber = cardNumber;
            this.expiryDate = expiryDate;
            this.cardName = cardName;
        }

    }
}