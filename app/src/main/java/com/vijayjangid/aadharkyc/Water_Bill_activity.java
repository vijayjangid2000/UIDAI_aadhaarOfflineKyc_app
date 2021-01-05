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

import maes.tech.intentanim.CustomIntent;

import static maes.tech.intentanim.CustomIntent.customType;

public class Water_Bill_activity extends AppCompatActivity {

    TextView tvbWaterBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water__bill_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Water Bill Payment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);

        tvbWaterBoard = findViewById(R.id.selectBoard_textview2);
        tvbWaterBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> textList = new ArrayList<>();
                final String BOARDS = "Ajmer Vidyut Vitran Nigam Ltd. (AVVNL)," +
                        "Bharatpur Electricity Services Ltd. (BESL)," +
                        "Bikaner Electricity Supply Limited," +
                        "Jaipur Vidyut Vitran Nigam Ltd. (JVVNL)," +
                        "Jodhpur Vidyut Vitran Nigam Ltd. (JDVVNL)," +
                        "Kota Electricity Distribution Ltd. (KEDL)," +
                        "TP Ajmer Distribution Ltd. (TPADL)";
                String[] array = BOARDS.split(",");
                Collections.addAll(textList, array);

                ArrayList<String> imageLinks = new ArrayList<String>();
                imageLinks.add("imagelink 1");

                Intent intent = new Intent(Water_Bill_activity.this,
                        z_selectImageText.class);

                intent.putStringArrayListExtra("TEXT_LIST", textList);
                intent.putStringArrayListExtra("IMAGE_LINK_LIST", imageLinks);
                intent.putExtra("TOOLBAR_NAME", "Select Board");
                intent.putExtra("IMAGES_ALSO", false);

                startActivity(intent);
                customType(Water_Bill_activity.this, "bottom-to-up");
            }
        });
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    }

}