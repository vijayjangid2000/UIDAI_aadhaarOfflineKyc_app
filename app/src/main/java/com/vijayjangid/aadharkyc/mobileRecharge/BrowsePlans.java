package com.vijayjangid.aadharkyc.mobileRecharge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.model.Offer;

import java.util.ArrayList;

public class BrowsePlans extends AppCompatActivity {

    final int REQUEST_CODE_PLANS = 100;
    RecyclerView recyclerView;
    ArrayList<String> offerListString;
    ArrayList<Offer> offerArrayList;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_sel_browse_plans);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Plans");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = findViewById(R.id.rv_plans);

        initializeRecyclerView(false, "noSearchStringNeeded");
        setSearchViewForResults();
    }

    void setSearchViewForResults() {

        searchView = findViewById(R.id.searchview_list3);

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (searchView.getQuery().toString().length() == 0) {
                    searchView.setBackgroundColor(Color.TRANSPARENT);
                    initializeRecyclerView(false, "noNeedInThisCase");
                } else {
                    initializeRecyclerView(true, searchView.getQuery().toString());
                }
                return false;
            }
        });


    }

    void initializeRecyclerView(boolean isFilter, String filterPrice) {

        offerArrayList = new ArrayList<>();

        offerListString = getIntent().getStringArrayListExtra("browsePlans");
        // converting string List to Offer
        if (offerListString == null) {
            Toast.makeText(this, "No Plans available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        offerArrayList.clear(); // very important else result in duplicate

        for (int i = 0; i < offerListString.size(); i++) {
            Offer offer = new Offer();
            String[] array = offerListString.get(i).split(",,,,");
            offer.setCommAmount(array[0]);
            offer.setCommType(array[1]);
            offer.setOffer(array[2]);
            offer.setPrice(array[3]);
            if (isFilter) {
                if (filterPrice.equals(array[3])) offerArrayList.add(offer);
            } else offerArrayList.add(offer);
        }

        if (isFilter) {

            int results = offerArrayList.size();
            if (results == 0) searchView.setBackgroundColor(getColor(R.color.lightRedBackground));
            else searchView.setBackgroundColor(getColor(R.color.lightGreenBackground));

        }


        CustomAdapter customAdapter = new CustomAdapter(offerArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customAdapter);
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        ArrayList<Offer> arrayListPlans;

        public CustomAdapter(ArrayList<Offer> arrayListPlans) {
            this.arrayListPlans = arrayListPlans;
        }

        @NonNull
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(BrowsePlans.this);
            View view = layoutInflater.inflate(R.layout.listview_browse_plans, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

            Offer offer = arrayListPlans.get(position);
            holder.tv_planDetails.setText(offer.getOffer());
            //holder.tv_data.setText(" HEY ");
            holder.tv_cost.setText("Rs. " + offer.getPrice());
            //holder.tv_validity.setText(" HEY ");

        }

        @Override
        public int getItemCount() {
            return arrayListPlans.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_cost, tv_data,
                    tv_validity, tv_planDetails;
            Button btn_select;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_cost = itemView.findViewById(R.id.tv_planCost);
                tv_data = itemView.findViewById(R.id.tv_data);
                btn_select = itemView.findViewById(R.id.btn_selectPlan);
                tv_validity = itemView.findViewById(R.id.tv_validity);
                tv_planDetails = itemView.findViewById(R.id.tv_planDetail);

                btn_select.setOnClickListener(view -> {
                    btn_select.setAlpha(0.5f);
                    Intent intent = new Intent();
                    intent.putExtra("PlanPrice", offerArrayList.get(getAdapterPosition()).getPrice());
                    setResult(REQUEST_CODE_PLANS, intent);
                    finish();
                });
            }

        }
    }
}