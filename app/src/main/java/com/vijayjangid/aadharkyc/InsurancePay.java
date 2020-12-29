package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;

public class InsurancePay extends AppCompatActivity {

    TextView company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_pay);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pay Insurance Bill");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        company = findViewById(R.id.tv_insuranceCompany);

        company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> textList = new ArrayList<>();
                final String BOARDS = "Aditya Birla Sun Life Insurance," +
                        "Aegon Life Insurance," +
                        "Bajaj Life Insurance," +
                        "Bharti AXA Life Insurance," +
                        "Exide Life Insurance," +
                        "Future Generali India Life Insurance," +
                        "HDFC Life Insurance," +
                        "India First Life Insurance, LIC of India," +
                        "Max Life Life Insurance," +
                        "PNB Metlife Insurance," +
                        "Universal Sompo General Insurance";
                String[] array = BOARDS.split(",");
                Collections.addAll(textList, array);

                ArrayList<String> imageLinks = new ArrayList<String>();
                imageLinks.add("imagelink 1");

                Intent intent = new Intent(InsurancePay.this,
                        z_selectImageText.class);

                intent.putStringArrayListExtra("TEXT_LIST", textList);
                intent.putStringArrayListExtra("IMAGE_LINK_LIST", imageLinks);
                intent.putExtra("TOOLBAR_NAME", "Select Board");
                intent.putExtra("IMAGES_ALSO", false);

                startActivity(intent);
            }
        });
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}