package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Electricity_recharge_activity extends AppCompatActivity
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
        toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                startActivityForResult(new Intent(Electricity_recharge_activity.this,
                        z_selBoardElectricity.class), REQUEST_CODE_BOARD);
                break;
            case R.id.state_textview:
                startActivityForResult(new Intent(Electricity_recharge_activity.this,
                        z_state_choose_alert.class), REQUEST_CODE_BOARD);
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
}