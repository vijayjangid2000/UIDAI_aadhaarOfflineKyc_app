package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

import static maes.tech.intentanim.CustomIntent.customType;

public class Recharge_mobile_activity extends AppCompatActivity
        implements View.OnClickListener {

    // for operator and contact chooser
    final int REQUEST_CODE_CONTACT = 1;
    final int REQUEST_CODE_OPERATOR = 99;
    TextInputEditText etMobileNumber, etAmount;
    TextInputLayout etMobileNumberLayout, etAmountLayout;
    TextView tv_operatorName, tvb_operatorChange, tv_contactName,
            tvb_prepaid, tvb_postpaid, tvbViewPlans;
    String contactName, contactNumber, temporaryNumber, amount;
    Button btn_proceed;
    LinearLayout linearL_operatorChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_mobile_act);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mobile Recharge");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);

        tvb_prepaid = findViewById(R.id.prepaid_TextB);
        tvb_postpaid = findViewById(R.id.postpaid_TextB);
        etMobileNumber = findViewById(R.id.phoneEtRec);
        etMobileNumberLayout = findViewById(R.id.phoneEtLRec);
        tv_contactName = findViewById(R.id.contact_name);
        tv_operatorName = findViewById(R.id.operator_name_tv);
        tvb_operatorChange = findViewById(R.id.changeOperatorTv);
        btn_proceed = findViewById(R.id.proceedRecharge_btn);
        etAmount = findViewById(R.id.amountEtRec);
        etAmountLayout = findViewById(R.id.amountEtLRec);
        linearL_operatorChange = findViewById(R.id.operator_linearLayout);
        tvbViewPlans = findViewById(R.id.viewPlansTvb);

        tvbViewPlans.setOnClickListener(this);
        btn_proceed.setOnClickListener(this);
        tvb_operatorChange.setOnClickListener(this);
        tvb_prepaid.setOnClickListener(this);
        tvb_postpaid.setOnClickListener(this);

        // on end icon, contact choosing work
        etMobileNumberLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts();
            }
        });

        // setting recent recharge, if found
        setRecent();

        etMobileNumber.clearFocus();
        etAmount.clearFocus();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.proceedRecharge_btn:
                textWatcher();
                amount = etAmount.getText().toString().trim();
                Toast.makeText(getApplicationContext(),
                        "Recharge Successful of Rs. " + amount +
                                " on mobile - " + contactNumber, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, ChoosePaymentOption.class));
                customType(this, "bottom-to-up");
                break;

            case R.id.changeOperatorTv:
                // for changing operator (opens operator activity)
                tvb_operatorChange.setAlpha((float) 0.4);
                startActivityForResult(new Intent(Recharge_mobile_activity.this,
                        z_selOperatorRecharge.class), REQUEST_CODE_OPERATOR);
                customType(this, "bottom-to-up");
                break;

            case R.id.prepaid_TextB:
                tvb_prepaid.setTextColor(Color.WHITE);
                tvb_prepaid.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.rounded_shape_2));

                tvb_postpaid.setTextColor(getColor(R.color.colorPrimary));
                tvb_postpaid.setBackgroundColor(Color.TRANSPARENT);
                break;

            case R.id.postpaid_TextB:
                tvb_postpaid.setTextColor(Color.WHITE);
                tvb_postpaid.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.rounded_shape_2));

                tvb_prepaid.setTextColor(getColor(R.color.colorPrimary));
                tvb_prepaid.setBackgroundColor(Color.TRANSPARENT);
                break;

            case R.id.viewPlansTvb:
                tvbViewPlans.setAlpha((float) 0.4);
                startActivity(new Intent(this, z_selBrowsePlans.class));
                customType(this, "bottom-to-up");
                break;
        }
    }

    // for changing the alpha values to normal after user choose operator
    @Override
    protected void onResume() {
        super.onResume();
        float alphaValue = 1;
        tvb_operatorChange.setAlpha(alphaValue);
        tvbViewPlans.setAlpha(alphaValue);
    }

    // this makes sure that there is 10 digits number
    void textWatcher() {

        contactNumber = etMobileNumber.getText().toString().trim();

        if (contactNumber.length() != 10 && contactNumber.length() != 0)
            etMobileNumberLayout.setError("Enter 10 digit mobile number");

        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                contactNumber = etMobileNumber.getText().toString().trim();
                if (contactNumber.length() != 10)
                    etMobileNumberLayout.setError("Enter 10 digit mobile number");
                else etMobileNumberLayout.setError(null);

                if (temporaryNumber == null) return;
                if (!temporaryNumber.equals(contactNumber))
                    tv_contactName.setVisibility(View.GONE);
                else tv_contactName.setVisibility(View.VISIBLE);
            }
        });
    }

    // opens choose contact intent
    void getContacts() {
        Uri uri = Uri.parse("content://contacts");
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_CODE_CONTACT);
    }

    /*Two tasks
     * 1. choose contact (result code 1)
     * 2. choose operator (result code 99)*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_CONTACT) {
            // for choosing contact from contact list
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                if (cursor == null) {
                    Toast.makeText(this, "No contacts found"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                cursor.moveToFirst();

                int numberColumnIndex = cursor.
                        getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.
                        getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);

                cursor.close();

                if (number.contains("+91")) number = number.substring(3, 13);

                temporaryNumber = number;
                tv_contactName.setVisibility(View.VISIBLE);
                tv_contactName.setText(name);
                etMobileNumber.setText(number);
            }
        } else if (requestCode == REQUEST_CODE_OPERATOR) {
            // for getting operator name from the operator list activity
            try {
                String message = intent.getStringExtra("MESSAGE");
                tv_operatorName.setText(message);
            } catch (Exception ignore) {
            }
        }
    }

    // this set the last numbers that are recharged
    void setRecent() {
        ListView listView = findViewById(R.id.recents_listView);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("apple");
        listView.setAdapter(new CustomAdapter(arrayList));
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    // Recent List custom Adapter
    public class CustomAdapter implements ListAdapter {

        ArrayList<String> arrayList;

        public CustomAdapter(ArrayList<String> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(Recharge_mobile_activity.this);
            convertView = layoutInflater.inflate(R.layout.listview_recents, null);
            return convertView;
        }

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
            return arrayList.size();
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    }
}