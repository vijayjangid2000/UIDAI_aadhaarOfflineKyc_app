package Aeps;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.HomePage_activity;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class AepsActivity extends AppCompatActivity {

    private static final String TAG = "AepsActivity";
    private static final int RD_SERVICE_RESPONSE_DODE = 10;
    UserData userData;
    String str = "";
    int strOldlen = 0;
    private int layout = R.layout.activity_aeps;
    private EditText ed_aadharNumber;
    private Button btnCapture;
    private ImageButton btnReset;
    private Spinner spn_serviceType;
    private LinearLayout ll_amount;
    private EditText ed_customerNumber;
    private EditText ed_Amount;
    private AutoCompleteTextView atv_bankName;
    private ProgressBar progress_bank;
    private ProgressBar progressProceed;
    private ScrollView sv_layout;
    private Spinner spn_selectDevice;
    private String deviceName = "MANTRA";
    private String customerMobileNumber = "455";
    private String amount = "";
    private String aadharNumber = "";
    private String bankCode = "";
    private String bankName = "";
    private String pidData = "asdsadsa";
    private String transactionType = "BE";
    private Dialog dialog_processing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps);

        userData = new UserData(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("AEPS");
        getSupportActionBar().setSubtitle("Balance - Rs. " + SharedPref.getInstance(this).getUserBalance());


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        initView();
        setReqtuestTo();
        setDeviceType();
        getAepsBankList();
        ed_aadharNumber.setText("");
        ed_aadharNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                str = ed_aadharNumber.getText().toString();
                int strLen = str.length();


                if (strOldlen < strLen) {

                    if (strLen > 0) {
                        if (strLen == 4 || strLen == 9) {

                            str = str + "-";

                            ed_aadharNumber.setText(str);
                            ed_aadharNumber.setSelection(ed_aadharNumber.getText().length());

                        } else {

                            if (strLen == 5) {
                                if (!str.contains("-")) {
                                    String tempStr = str.substring(0, strLen - 1);
                                    tempStr += "-" + str.substring(strLen - 1, strLen);
                                    ed_aadharNumber.setText(tempStr);
                                    ed_aadharNumber.setSelection(ed_aadharNumber.getText().length());
                                }
                            }
                            if (strLen == 10) {
                                if (str.lastIndexOf("-") != 9) {
                                    String tempStr = str.substring(0, strLen - 1);
                                    tempStr += "-" + str.substring(strLen - 1, strLen);
                                    ed_aadharNumber.setText(tempStr);
                                    ed_aadharNumber.setSelection(ed_aadharNumber.getText().length());
                                }
                            }
                            strOldlen = strLen;
                        }
                    } else {
                        return;
                    }

                } else {
                    strOldlen = strLen;


                    Log.i("MainActivity ", "keyDel is Pressed ::: strLen : " + strLen + "\n old Str Len : " + strOldlen);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnCapture.setOnClickListener(view -> captureData());
        btnReset.setOnClickListener(view -> resetData());

    }

    private void initView() {

        ed_aadharNumber = findViewById(R.id.ed_aadharNumber);
        btnCapture = findViewById(R.id.btnCapture);
        btnReset = findViewById(R.id.btnReset);
        spn_serviceType = findViewById(R.id.spn_serviceType);
        ll_amount = findViewById(R.id.ll_amount);
        ed_customerNumber = findViewById(R.id.ed_customerNumber);
        ed_Amount = findViewById(R.id.ed_Amount);
        /*        spn_bank = findViewById(R.id.spn_bank);*/
        atv_bankName = findViewById(R.id.atv_bank_name);
        spn_selectDevice = findViewById(R.id.spn_selectDevice);
        progress_bank = findViewById(R.id.progress_bank);
        progressProceed = findViewById(R.id.progressProceed);
        sv_layout = findViewById(R.id.sv_layout);
    }

    private void setReqtuestTo() {
        String[] requestToList = {"Enquiry", "Withdrawal", "Mini Statement"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, requestToList);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_serviceType.setAdapter(dataAdapter);
        spn_serviceType.setSelection(1);
        spn_serviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spn_serviceType.getSelectedItem().toString().equalsIgnoreCase(requestToList[1])) {
                    ll_amount.setVisibility(View.VISIBLE);
                    transactionType = "CW";

                } else {
                    ll_amount.setVisibility(View.GONE);
                    if (spn_serviceType.getSelectedItem().toString().equalsIgnoreCase(requestToList[0]))
                        transactionType = "BE";
                    else
                        transactionType = "MS";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setDeviceType() {
        String[] deviceList = {"MANTRA", "MORPHO"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, deviceList);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_selectDevice.setAdapter(dataAdapter);
        spn_selectDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spn_selectDevice.getSelectedItem().toString().equalsIgnoreCase(deviceList[0])) {
                    deviceName = "MANTRA";
                } else {
                    deviceName = "MORPHO";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getAepsBankList() {

        String url = APIs.GET_AEPS_BANK_LIST + "?" + APIs.USER_TAG + "=" + userData.getId()
                + "&" + APIs.TOKEN_TAG + "=" + userData.getToken();
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {

                            sv_layout.setVisibility(View.VISIBLE);
                            progress_bank.setVisibility(View.GONE);
                            JSONObject aepsBankList = jsonObject.getJSONObject("data");
                            setBankList(aepsBankList);

                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else MakeToast.show(this, message);


                    } catch (JSONException e) {
                        MakeToast.show(this, "Something went wrong! please contanct to admin");
                        finish();
                    }
                },
                error -> {
                    MakeToast.show(this, "Something went wrong! please contanct to admin");
                    finish();
                }) {

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void setBankList(JSONObject aepsObject) {
        try {
            HashMap<String, String> bankListHashMap = new HashMap<>();
            bankListHashMap.clear();
            Iterator iterator = aepsObject.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                bankListHashMap.put(aepsObject.getString(key), key);
            }
            String[] bankLists = bankListHashMap.keySet().toArray(new String[0]);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.select_dialog_item, bankLists);
            atv_bankName.setThreshold(1);
            atv_bankName.setAdapter(arrayAdapter);
            atv_bankName.setOnItemClickListener((parent, view, position, id) -> {

                bankCode = bankListHashMap.get(atv_bankName.getText().toString());
                bankName = atv_bankName.getText().toString();
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidInput() {
        boolean isValid = false;
        customerMobileNumber = ed_customerNumber.getText().toString();
        aadharNumber = ed_aadharNumber.getText().toString().replace("-", "");
        amount = ed_Amount.getText().toString();
        Log.e("adhar", "" + aadharNumber);

        if (!customerMobileNumber.isEmpty() && customerMobileNumber.length() >= 10) {
            if (!atv_bankName.getText().toString().isEmpty() && !bankCode.isEmpty()) {
                if (aadharNumber.length() == 12) {
                    if ((!amount.isEmpty() && !amount.equalsIgnoreCase("0") && Integer.parseInt(amount) >= 100 && Integer.parseInt(amount) <= 10000) ||
                            !spn_serviceType.getSelectedItem().toString().equalsIgnoreCase("Withdrawal")) {
                        isValid = true;
                    } else MakeToast.show(this, "Amount should be between 100 - 10000 only.");
                } else
                    MakeToast.show(this, "Aadhar number can't be empty! and length should be 12 digit");
            } else
                MakeToast.show(this, "Select a valid bank name! bank can't be empty!");
        } else
            MakeToast.show(this, "Customer number can't be empty! and length should be 10 digit");
        return isValid;
    }

    private void captureData() {
        if (isValidInput()) {
            final String MANTRA_PACKAGE_URL = "com.mantra.rdservice";
            final String MORPHO_PACKAGE_URL = "com.scl.rdservice";

            try {
                String pidOption = "<PidOptions ver=\"1.0\">\n" +
                        "       <Opts env=\"P\" fCount=\"1\" fType=\"0\" format=\"0\" iCount=\"0\" iType=\"0\" pCount=\"0\" pType=\"0\" pidVer=\"2.0\" posh=\"UNKNOWN\" timeout=\"10000\"/>\n" +
                        "    </PidOptions>";

                Intent intent = new Intent();
                if (deviceName.equalsIgnoreCase("MANTRA"))
                    intent.setPackage(MANTRA_PACKAGE_URL);
                else intent.setPackage(MORPHO_PACKAGE_URL);
                intent.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                intent.putExtra("PID_OPTIONS", pidOption);
                startActivityForResult(intent, RD_SERVICE_RESPONSE_DODE);


            } catch (Exception e) {
                Dialog dialog = AppDialogs.transactionStatus(this, e.getMessage(), 2);
                Button btnOK = dialog.findViewById(R.id.btn_ok);
                btnOK.setOnClickListener(view -> dialog.dismiss());
                dialog.show();
                MakeToast.show(this, "No device is connected! please reconnect and try again");
            }
        }
    }

    public void resetData() {
        ed_aadharNumber.setText("");
        ed_customerNumber.setText("");
        atv_bankName.setText("");
        ed_Amount.setText("");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RD_SERVICE_RESPONSE_DODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    if (data != null) {
                        String result = data.getStringExtra("PID_DATA");
                        if (result != null) {
                            try {
                                String[] respString = PidParser.parse(result);
                                if (respString[0].equalsIgnoreCase("0")) {
                                    pidData = result;
                                    confirmDialog();
                                } else MakeToast.show(this, respString[1]);
                            } catch (XmlPullParserException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private void showProcessingDialog() {
        dialog_processing = AppDialogs.processing(AepsActivity.this);
        dialog_processing.show();
    }

    private void aepsTransaction() {
        showProcessingDialog();
        setViewEnableDisable(false);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.AEPS_TRANSACTION,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        String message = object.optString("message");

                        if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else {


                            if (status.equalsIgnoreCase("Success")) {

                                if (transactionType.equalsIgnoreCase("MS")) {

                                    Intent intent = new Intent(AepsActivity.this, MiniStatementActivity.class);
                                    intent.putExtra("balance", "" + object.optString("availableBalance"));
                                    intent.putExtra("data", "" + object.optString("statments"));
                                    intent.putExtra("id", "" + object.optString("txnid"));
                                    startActivity(intent);
                                } else {


                                    Dialog dialog = AppDialogs.aepsResponseDialog(this, 1);

                                    TextView tv_mobile = dialog.findViewById(R.id.tv_mobile);
                                    TextView tv_aadharNumber = dialog.findViewById(R.id.tv_aadharNumber);
                                    TextView tv_bankRRNumber = dialog.findViewById(R.id.tv_bankRRNumber);
                                    TextView tv_transactionAmount = dialog.findViewById(R.id.tv_transactionAmount);
                                    TextView tv_transactionId = dialog.findViewById(R.id.tv_transactionId);
                                    TextView tv_transactionType = dialog.findViewById(R.id.tv_transactionType);
                                    TextView tv_transactionTime = dialog.findViewById(R.id.tv_transactionTime);
                                    TextView tv_availBalance = dialog.findViewById(R.id.tv_availBalance);

                                    tv_mobile.setText(customerMobileNumber);
                                    tv_aadharNumber.setText(aadharNumber);
                                    tv_bankRRNumber.setText(object.optString("bankRRN"));
                                    tv_transactionAmount.setText(object.optString("transactionAmount"));
                                    tv_transactionType.setText(object.optString("transactionType"));
                                    tv_transactionId.setText(object.optString("fpTransactionId"));
                                    tv_transactionTime.setText(object.optString("txnTime"));
                                    tv_availBalance.setText(object.optString("availableBalance"));


                                    Button btnOk = dialog.findViewById(R.id.btn_ok);
                                    btnOk.setOnClickListener(view -> {
                                        dialog.dismiss();
                                    });
                                    dialog.setOnDismissListener(dialogInterface -> {
                                        if (transactionType.equalsIgnoreCase("CW")) {
                                            Intent intent = new Intent(this, HomePage_activity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    });
                                    dialog.show();
                                }
                            } else {
                                Dialog dialog = AppDialogs.transactionStatus(this, message, 2);
                                Button btnOK = dialog.findViewById(R.id.btn_ok);
                                btnOK.setOnClickListener(view -> dialog.dismiss());
                                dialog.show();
                            }
                        }
                        dialog_processing.dismiss();

                    } catch (JSONException e) {
                        Dialog dialog = AppDialogs.transactionStatus(this, response, 2);
                        Button btnOK = dialog.findViewById(R.id.btn_ok);
                        btnOK.setOnClickListener(view -> dialog.dismiss());
                        dialog.show();
                        dialog_processing.dismiss();
                        setViewEnableDisable(true);
                        //AppDialogs.volleyErrorDialog(this, 1);
                    }
                    setViewEnableDisable(true);
                },
                error -> {
                    /*Dialog dialog = AppDialogs.transactionStatus(this,response,2);
                    Button btnOK = dialog.findViewById(R.id.btn_ok);
                    btnOK.setOnClickListener(view->dialog.dismiss());
                    dialog.show();*/
                    dialog_processing.dismiss();
                    //AppDialogs.volleyErrorDialog(this, 0);
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    setViewEnableDisable(true);

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        MakeToast.show(this, "no internet connection");
                    } else if (error instanceof AuthFailureError) {
                        MakeToast.show(this, "AuthFailureError");
                    } else if (error instanceof ServerError) {
                        MakeToast.show(this, "ServerError");
                    } else if (error instanceof NetworkError) {
                        MakeToast.show(this, "NetworkError");
                    } else if (error instanceof ParseError) {
                        MakeToast.show(this, "ParseError");
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                if (amount.equalsIgnoreCase("")) amount = "0";
                param.put("userId", String.valueOf(userData.getId()));
                param.put("token", String.valueOf(userData.getToken()));
                param.put("customerNumber", customerMobileNumber);
                param.put("bankName", bankCode);
                param.put("bankNameText", bankName);
                param.put("transactionType", transactionType);
                param.put("txtPidData", pidData);
                param.put("amount", amount);
                param.put("deviceName", deviceName);
                param.put("aadhaarNumber", aadharNumber);
                return param;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(240),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void setViewEnableDisable(boolean enable) {
        if (enable) {
            progressProceed.setVisibility(View.GONE);
            btnCapture.setVisibility(View.VISIBLE);
            btnReset.setVisibility(View.VISIBLE);
            ed_customerNumber.setAlpha(1f);
            ed_customerNumber.setEnabled(true);
            ed_aadharNumber.setAlpha(1f);
            ed_aadharNumber.setEnabled(true);
            ed_Amount.setAlpha(01f);
            ed_Amount.setEnabled(true);
        } else {
            progressProceed.setVisibility(View.VISIBLE);
            btnCapture.setVisibility(View.GONE);
            btnReset.setVisibility(View.GONE);
            ed_customerNumber.setAlpha(0.5f);
            ed_customerNumber.setEnabled(false);
            ed_aadharNumber.setAlpha(0.5f);
            ed_aadharNumber.setEnabled(false);
            ed_Amount.setAlpha(0.5f);
            ed_Amount.setEnabled(false);
        }

    }

    private void confirmDialog() {
        Dialog dialog = AppDialogs.confirmTransaction(this);

        TextView tvHeading = dialog.findViewById(R.id.tv_heading);
        String heading = "Confirmation Aeps Transaction";
        tvHeading.setText(heading);
        TextView tvSubHeading = dialog.findViewById(R.id.tv_subHeading);

        String subHeading;
        if (transactionType.equalsIgnoreCase("CW"))
            subHeading = "Aeps AMOUNT WITHDRAWAL of Rs. " + amount + " with AADHAR NO. " + aadharNumber + " and  BANK " + atv_bankName.getText().toString();
        else if (transactionType.equalsIgnoreCase("MS"))
            subHeading = "Aeps MINI STATEMENT of Rs. " + amount + " with AADHAR NO. " + aadharNumber + " and  BANK " + atv_bankName.getText().toString();

        else
            subHeading = "Aeps BALANCE ENQUIRY with AADHAR NO. " + aadharNumber + "and  BANK " + atv_bankName.getText().toString();
        tvSubHeading.setText(subHeading);

        ImageButton btnDismiss = dialog.findViewById(R.id.btn_dismiss);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOK = dialog.findViewById(R.id.btn_confirm);

        btnDismiss.setOnClickListener(view -> dialog.dismiss());
        btnCancel.setOnClickListener(view -> dialog.dismiss());
        btnOK.setOnClickListener(view -> {
            dialog.dismiss();
            aepsTransaction();
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
