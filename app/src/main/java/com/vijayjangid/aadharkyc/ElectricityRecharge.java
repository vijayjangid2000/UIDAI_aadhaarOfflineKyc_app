package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;

import maes.tech.intentanim.CustomIntent;

import static maes.tech.intentanim.CustomIntent.customType;

public class ElectricityRecharge extends AppCompatActivity
        implements View.OnClickListener {

    final int REQUEST_CODE_BOARD = 98;
    final int REQUEST_CODE_STATE = 97;
    TextView tv_stateHint, tv_stateName, tv_boardHint, tv_boardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electrivity_recharge_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pay Electricity Bill");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);

        tv_stateHint = findViewById(R.id.selectstate_textview);
        tv_boardHint = findViewById(R.id.selectboard_textview);
        tv_stateName = findViewById(R.id.state_textview);
        tv_boardName = findViewById(R.id.board_textview);

        tv_stateName.setText("Rajasthan");
        tv_boardName.setText("Board Name");

        tv_stateHint.setOnClickListener(this);
        tv_boardHint.setOnClickListener(this);
        tv_stateName.setOnClickListener(this);
        tv_boardName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.selectstate_textview:

                break;
            case R.id.selectboard_textview:

                break;
            case R.id.board_textview:
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

                Intent intent = new Intent(ElectricityRecharge.this,
                        z_selectImageText.class);

                intent.putStringArrayListExtra("TEXT_LIST", textList);
                intent.putStringArrayListExtra("IMAGE_LINK_LIST", imageLinks);
                intent.putExtra("TOOLBAR_NAME", "Select Board");
                intent.putExtra("IMAGES_ALSO", false);

                startActivityForResult(intent, REQUEST_CODE_BOARD);
                customType(this, "bottom-to-up");

                break;
            case R.id.state_textview:
                ArrayList<String> textList2 = new ArrayList<>();
                final String STATES = "Andhra Pradesh,Assam,Bihar,Chandigarh," +
                        "Chhattisgarh,Dadra and Nagar Haveli,Daman and Diu,Goa,Gujarat,Haryana," +
                        "Himachal Pradesh,Jammu and Kashmir,Jharkhand,Karnataka,Kerala," +
                        "Madhya Pradesh,Maharashtra,Manipur,Meghalaya,Mizoram,Nagaland,New Delhi," +
                        "Odisha,Pondicherry,Punjab,Pakistan,Sikkim,Tamil Nadu,Telangana" +
                        ",Tripura,Uttar Pradesh,Uttarakhand,West Bengal";
                String[] array2 = STATES.split(",");
                Collections.addAll(textList2, array2);

                Intent intent1 = new Intent(ElectricityRecharge.this, z_state_choose_alert.class);
                intent1.putStringArrayListExtra("TEXT_LIST", textList2);

                startActivityForResult(intent1, REQUEST_CODE_BOARD);
                customType(this, "bottom-to-up");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == REQUEST_CODE_BOARD) {
            try {
                String message = data.getStringExtra("MESSAGE").trim();
                tv_boardName.setText(message);
            } catch (Exception ignore) {
            }
        } else if (resultCode == REQUEST_CODE_STATE) {
            try {

                String message = data.getStringExtra("MESSAGE").trim();
                tv_stateName.setText(message);

            } catch (Exception ignored) {
            }
        }
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