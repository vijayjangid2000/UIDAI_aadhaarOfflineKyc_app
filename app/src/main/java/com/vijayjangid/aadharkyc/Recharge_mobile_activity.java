package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class Recharge_mobile_activity extends AppCompatActivity
        implements View.OnClickListener {

    // for operator and contact chooser
    final int REQUEST_CODE_CONTACT = 1;
    final int REQUEST_CODE_OPERATOR = 99;
    RadioGroup pre_post_RadioG;
    RadioButton prepaid_RadioB, postpaid_RadioB;
    TextInputEditText etMobileNumber, etAmount;
    TextInputLayout etMobileNumberLayout, etAmountLayout;
    TextView tv_operatorName, tvb_operatorChange, tv_contactName;
    String contactName, contact_number, temporaryNumber, amount;
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

        pre_post_RadioG = findViewById(R.id.pre_post_RG);
        prepaid_RadioB = findViewById(R.id.prepaid_RB);
        postpaid_RadioB = findViewById(R.id.postpaid_RB);
        etMobileNumber = findViewById(R.id.phoneEtRec);
        etMobileNumberLayout = findViewById(R.id.phoneEtLRec);
        tv_contactName = findViewById(R.id.contact_name);
        tv_operatorName = findViewById(R.id.operator_name_tv);
        tvb_operatorChange = findViewById(R.id.changeOperatorTv);
        btn_proceed = findViewById(R.id.proceedRecharge_btn);
        etAmount = findViewById(R.id.amountEtRec);
        etAmountLayout = findViewById(R.id.amountEtLRec);
        linearL_operatorChange = findViewById(R.id.operator_linearLayout);

        btn_proceed.setOnClickListener(this);
        tvb_operatorChange.setOnClickListener(this);

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
                                " on mobile - " + contact_number, Toast.LENGTH_LONG).show();
                break;

            case R.id.changeOperatorTv:
                // for changing operator (opens operator activity)
                tvb_operatorChange.setAlpha((float) 0.4);
                startActivityForResult(new Intent(Recharge_mobile_activity.this,
                        z_selOperatorRecharge.class), REQUEST_CODE_OPERATOR);
                break;
        }

    }

    // for changing the alpha values to normal after user choose operator
    @Override
    protected void onResume() {
        super.onResume();
        tvb_operatorChange.setAlpha((float) 1);
    }

    // this makes sure that there is 10 digits number
    void textWatcher() {

        contact_number = etMobileNumber.getText().toString().trim();

        if (contact_number.length() != 10 && contact_number.length() != 0)
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
                contact_number = etMobileNumber.getText().toString().trim();
                if (contact_number.length() != 10)
                    etMobileNumberLayout.setError("Enter 10 digit mobile number");
                else etMobileNumberLayout.setError(null);

                if (temporaryNumber == null) return;
                if (!temporaryNumber.equals(contact_number))
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
}