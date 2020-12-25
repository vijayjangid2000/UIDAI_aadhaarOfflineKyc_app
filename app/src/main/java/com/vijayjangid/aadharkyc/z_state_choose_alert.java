package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;

public class z_state_choose_alert extends AppCompatActivity {

    final int RESULT_CODE_STATE = 97;

    final String STATES = "Andhra Pradesh,Himachal Pradesh,Assam,Bihar,Chandigarh," +
            "Chhattisgarh, Dadra and Nagar Haveli,Daman and Diu,Goa,Gujarat,Haryana," +
            "Himachal Pradesh,Jammu and Kashmir,Jharkhand,Karnataka,Kerala," +
            "Madhya Pradesh,Maharashtra,Manipur,Meghalaya,Mizoram,Nagaland,New Delhi," +
            "Odisha,Pondicherry,Punjab,Pakistan,Sikkim,Tamil Nadu,Telangana" +
            ",Tripura,Uttar Pradesh,Uttarakhand,West Bengal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_state_choose_alert);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select State");
        toolbar.setBackgroundColor(getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final ArrayList<String> arrayList = new ArrayList<>();
        String[] array = STATES.split(",");
        Collections.addAll(arrayList, array);

        ListView listview = findViewById(R.id.state_listview);
        ArrayAdapter adapter = new ArrayAdapter(z_state_choose_alert.this,
                android.R.layout.simple_list_item_1, arrayList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", arrayList.get(position));
                setResult(RESULT_CODE_STATE, intent);
                finish();
            }
        });
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}