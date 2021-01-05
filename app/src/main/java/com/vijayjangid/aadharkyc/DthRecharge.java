package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;

import maes.tech.intentanim.CustomIntent;

import static maes.tech.intentanim.CustomIntent.customType;

public class DthRecharge extends AppCompatActivity {

    Button btn_proceed;
    TextView tvbOperatorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dth_recharge);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dth Recharge");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);


        tvbOperatorName = findViewById(R.id.d2h_tv);
        tvbOperatorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> textList = new ArrayList<>();
                final String BOARDS = "Tata Sky,Airtel Digital TV," +
                        "Dish TV,Sun Direct,d2h";
                String[] array = BOARDS.split(",");
                Collections.addAll(textList, array);

                ArrayList<String> imageLinks = new ArrayList<String>();
                imageLinks.add("imagelink 1");

                Intent intent = new Intent(DthRecharge.this,
                        z_selectImageText.class);

                intent.putStringArrayListExtra("TEXT_LIST", textList);
                intent.putStringArrayListExtra("IMAGE_LINK_LIST", imageLinks);
                intent.putExtra("TOOLBAR_NAME", "Select Board");
                intent.putExtra("IMAGES_ALSO", false);

                startActivity(intent);
                customType(DthRecharge.this, "bottom-to-up");

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    }

}