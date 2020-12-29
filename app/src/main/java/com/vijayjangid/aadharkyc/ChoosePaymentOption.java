package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ChoosePaymentOption extends AppCompatActivity implements View.OnClickListener {

    ListView lv_paymentOptions;
    Button btn_proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_payment_option);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("IO Lab");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lv_paymentOptions = findViewById(R.id.lv_paymentOptions);
        btn_proceed = findViewById(R.id.proceedPayment_btn);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Kotak Mahindra Bank,A/C No. •••• 0125");
        arrayList.add("Uco Bank,A/C No. •••• 2115");
        arrayList.add("SBI Bank,A/C No. •••• 0554");
        arrayList.add("UCO Bank Debit Card,Card No. •••• 0334");
        arrayList.add("ICICI Bank Credit Card,Card No. •••• 0323");
        lv_paymentOptions.setAdapter(new CustomAdapter(arrayList));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.proceedPayment_btn:
                startActivity(new Intent(this, HomePage_activity.class));
                finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }

    public class CustomAdapter implements ListAdapter {

        ArrayList<String> arrayList;

        public CustomAdapter(ArrayList<String> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(ChoosePaymentOption.this);
                convertView = layoutInflater.inflate(R.layout.listview_bank_card, null);

                TextView tvb_bankName, tvb_accountDetails, tvb_checkBalance;
                ImageView iv_upi;

                tvb_bankName = convertView.findViewById(R.id.tv_bankNamePayment);
                tvb_accountDetails = convertView.findViewById(R.id.tv_accountNumPayment);
                //tvb_checkBalance = convertView.findViewById(R.id.tv_checkbalance);
                iv_upi = convertView.findViewById(R.id.iv_upiPayment);

                String[] value = arrayList.get(position).split(",");

                if (arrayList.get(position).toLowerCase().contains("card"))
                    iv_upi.setImageDrawable(ContextCompat.getDrawable
                            (ChoosePaymentOption.this, R.drawable.ic_card_pay));

                tvb_bankName.setText(value[0]);
                tvb_accountDetails.setText(value[1]);
            }
            return convertView;
        }

        // below methods not useful

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return arrayList.size();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}