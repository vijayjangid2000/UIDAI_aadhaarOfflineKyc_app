package Dmt;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.AppUitls;
import com.vijayjangid.aadharkyc.util.AutoLogoutManager;
import com.vijayjangid.aadharkyc.util.InternetConnection;
import com.vijayjangid.aadharkyc.util.MakeToast;
import com.vijayjangid.aadharkyc.util.SoftKeyboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import a2z_wallet.Beneficiary;

public class DmtMain extends AppCompatActivity implements RegisterRemitterFragentDMT2.OnFragmentInteractionListener,
        RemitterRegisterOtpFramgnetDMT2.OnFragmentInteractionListener,
        AddBeneFragmentDMT2.OnFragmentInteractionListener,
        BeneListFragmentDMT2.OnFragmentInteractionListener,
        MoneyTransferFragmentDMT2.OnFragmentInteractionListener {

    private static final String TAG = "DmtMain";
    final int REQUEST_CODE_CONTACT = 1;
    int activity_layout = R.layout.activity_dmt_main;
    Toolbar toolbar;
    UserData userData;
    private ImageButton btn_search, btn_refresh;
    private TextInputEditText et_mobile;
    private TextInputLayout etl_mobile;
    private String strMobileNumber;
    private TextView tv_error_hint, tv_remitterName, tv_remainingLimit;
    private LinearLayout ll_remitter_info;
    private boolean isTransferFragment = false;
    private ArrayList<Beneficiary> beneficiaries;
    private ProgressBar progressBarMobile;
    private MenuItem menu_item_add_beneficairy, menu_item_history;
    private FrameLayout dmt_fragmentContainer;
    private Fragment fragment;
    private String strRemiterId;
    private LinearLayout ll_search_refresh_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmt_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        userData = new UserData(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Direct Money Transfer");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);
        toolbar.setSubtitle("Balance Rs. " + SharedPref.getInstance(this).getUserBalance());
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        dmt_fragmentContainer = findViewById(R.id.dmt_fragmentContainer);
        tv_remitterName = findViewById(R.id.tv_remitterName);
        tv_remainingLimit = findViewById(R.id.tv_remainingLimit);
        ll_remitter_info = findViewById(R.id.ll_remitter_info);

        progressBarMobile = findViewById(R.id.progressBar);
        tv_error_hint = findViewById(R.id.tv_error_hint);
        et_mobile = findViewById(R.id.ed_numberdmt);
        etl_mobile = findViewById(R.id.edl_numberdmt);
        btn_search = findViewById(R.id.btn_search);
        ll_search_refresh_number = findViewById(R.id.ll_search_refresh_number);

        btn_search.setOnClickListener(view -> {
            if (InternetConnection.isConnected(this)) {
                if (validateNumber()) {
                    dmt_fragmentContainer.setVisibility(View.GONE);
                    verifyNumberToServer();
                }
            }
        });

        // on end icon, contact choosing work
        etl_mobile.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts();
            }
        });


        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_mobile.length() == 10 && AppUitls.stringHasOnlyNumber(et_mobile.getText().toString())) {
                    SoftKeyboard.hide(DmtMain.this);
                    btn_search.callOnClick();

                }

            }
        });

        btn_refresh = findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(view -> {
            et_mobile.setText("");
            recreate();
        });

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().detach(fragment).commit();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.dmt_fragmentContainer,
                RegisterRemitterFragentDMT2.newInstance(strMobileNumber)).commit();


    }

    private void verifyNumberToServer() {

        String url = APIs.MOBILE_VERIFICATION_DMT2 + "?" + APIs.USER_TAG + "=" + userData.getId()
                + "&" + APIs.TOKEN_TAG + "=" + userData.getToken() + "&mobileNumber=" + strMobileNumber;

        progressBarMobile.setVisibility(View.VISIBLE);
        btn_search.setVisibility(View.GONE);
        tv_error_hint.setVisibility(View.GONE);
        et_mobile.setAlpha(0.7f);
        et_mobile.setEnabled(false);
        final StringRequest request = new StringRequest(Request.Method.GET,
                url,
                response -> {
                    try {
                        btn_refresh.setVisibility(View.VISIBLE);
                        progressBarMobile.setVisibility(View.GONE);
                        btn_search.setVisibility(View.VISIBLE);


                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);

                        } else if (status.equalsIgnoreCase("300")) {
                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("2")) {
                            String message = jsonObject.getString("message");
                            showConnectionError(message);
                        } else {
                            String statuscode = jsonObject.getString("statuscode");
                            if (statuscode.equalsIgnoreCase("RNF")) {
                                menu_item_add_beneficairy.setVisible(false);

                                dmt_fragmentContainer.setVisibility(View.VISIBLE);
                                fragment = RegisterRemitterFragentDMT2.newInstance(strMobileNumber);
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.dmt_fragmentContainer, fragment)
                                        .commit();

                            }
                            if (statuscode.equalsIgnoreCase("TXN")
                                    && status.equalsIgnoreCase("OTP sent successfully")) {
                                menu_item_add_beneficairy.setVisible(false);
                                dmt_fragmentContainer.setVisibility(View.VISIBLE);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("remitter");
                                String verifyId = jsonObject2.getString("id");

                                fragment = RemitterRegisterOtpFramgnetDMT2.newInstance(strMobileNumber, verifyId);
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.dmt_fragmentContainer, fragment)
                                        .commit();

                            }
                            if (statuscode.equalsIgnoreCase("TXN")
                                    && status.equalsIgnoreCase("Transaction Successful")) {
                                menu_item_add_beneficairy.setVisible(true);
                                dmt_fragmentContainer.setVisibility(View.VISIBLE);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                //remitter details
                                JSONObject remitterObject = jsonObject1.getJSONObject("remitter");
                                strRemiterId = remitterObject.getString("id");
                                String strRemainingLimit = remitterObject.getString("remaininglimit");
                                String strName = remitterObject.getString("name");
                                ll_remitter_info.setVisibility(View.VISIBLE);
                                tv_remitterName.setText(strName);
                                tv_remainingLimit.setText(strRemainingLimit);
                                //beneficiary details
                                JSONArray jsonArray = jsonObject1.getJSONArray("beneficiary");
                                setupBeneficiary(jsonArray, strName);
                            }
                        }

                    } catch (JSONException e) {
                        progressBarMobile.setVisibility(View.GONE);
                        btn_search.setVisibility(View.VISIBLE);
                        et_mobile.setAlpha(1f);
                        et_mobile.setEnabled(true);
                        showConnectionError("Something went wrong!\nTry again");
                        Toast.makeText(this, "URL " + url, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBarMobile.setVisibility(View.GONE);
                    btn_search.setVisibility(View.VISIBLE);
                    et_mobile.setAlpha(1f);
                    et_mobile.setEnabled(true);
                    showConnectionError("Something went wrong!\nTry again");
                    Toast.makeText(this, "URL " + url, Toast.LENGTH_SHORT).show();

                }) {

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private boolean validateNumber() {

        boolean isValid = false;

        strMobileNumber = et_mobile.getText().toString();
        if (!strMobileNumber.isEmpty()) {
            if (strMobileNumber.length() == 10) {
                isValid = true;
            } else MakeToast.show(this, "Mobile Number can't be less and more than 10 digit");


        } else MakeToast.show(this, "Mobile Number Can't be empty!");

        return isValid;
    }

    private void showConnectionError(String message) {
        tv_error_hint.setText(message);
        tv_error_hint.setVisibility(View.VISIBLE);
    }

    private void setupBeneficiary(JSONArray jsonArray, String remitterName) {


        beneficiaries = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setImps(jsonObject.getString("imps"));
                beneficiary.setStatus(jsonObject.getString("account"));
                beneficiary.setName(jsonObject.getString("name"));
                beneficiary.setMobile(jsonObject.getString("mobile"));
                beneficiary.setIfsc(jsonObject.getString("ifsc"));
                beneficiary.setRemitterName(remitterName);
                beneficiary.setBank(jsonObject.getString("bank"));
                beneficiary.setAccount(jsonObject.getString("account"));
                beneficiary.setLast_success_name(jsonObject.getString("last_success_name"));
                beneficiary.setId(jsonObject.getString("id"));
                beneficiary.setLast_success_date(jsonObject.getString("last_success_date"));
                beneficiary.setLast_success_imps(jsonObject.getString("last_success_imps"));
                beneficiary.setRemitterName(tv_remitterName.getText().toString());

                beneficiaries.add(beneficiary);


            } catch (JSONException e) {
                progressBarMobile.setVisibility(View.GONE);
                btn_search.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }

        }


        fragment = BeneListFragmentDMT2.newInstance(beneficiaries, strRemiterId);
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dmt_fragmentContainer, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_beneficiary, menu);

        menu_item_add_beneficairy = menu.findItem(R.id.add_beneficiary);
        menu_item_add_beneficairy.setVisible(false);
        menu_item_history = menu.findItem(R.id.history);
        menu_item_history.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menu_item_add_beneficairy.setVisible(false);
        fragment = AddBeneFragmentDMT2.newInstance(strMobileNumber, strRemiterId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dmt_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
        isTransferFragment = true;
        dmt_fragmentContainer.setVisibility(View.VISIBLE);

        return true;
    }

    @Override
    public void onFragmentInteraction(String data, int type) {

        if (type == 1) { //response from  register remitter

            fragment = RemitterRegisterOtpFramgnetDMT2.newInstance(strMobileNumber, data);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dmt_fragmentContainer, fragment)
                    .commit();

        } else if (type == 2) { // response from remitter verify

            Dialog dialog = AppDialogs.transactionStatus(this, "Remitter Added Successfully", 1);
            Button btn_ok = dialog.findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(view -> {
                dialog.dismiss();
            });
            dialog.setOnDismissListener(dialog1 -> {
                btn_search.callOnClick();
            });
            dialog.show();
        } else if (type == 3) {
            if (data.equalsIgnoreCase("reset")) {
                dmt_fragmentContainer.setVisibility(View.GONE);
                btn_search.callOnClick();
            }
        } else if (type == 5) {
            fragment = MoneyTransferFragmentDMT2.newInstance(beneficiaries.get(Integer.parseInt(data)), strMobileNumber);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dmt_fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
            toolbar.setTitle("Transfer Money");
            //we don't want to show any reset and searchFundTranser option while transfer money
            menu_item_add_beneficairy.setVisible(false);
            isTransferFragment = true;
            ll_search_refresh_number.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        if (isTransferFragment) {// this thing is handling with fragment backstatck
            menu_item_add_beneficairy.setVisible(true);
            toolbar.setTitle("DMT 2");
            isTransferFragment = false;
            ll_search_refresh_number.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        AutoLogoutManager.cancelTimer();
        if (AutoLogoutManager.isSessionTimeout) {
            AutoLogoutManager.logout(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AutoLogoutManager.isAppInBackground(this)) {
            AutoLogoutManager.startUserSession();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CONTACT) {
            // for choosing contact from contact list
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
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

                et_mobile.setText(number);
            }
        }
    }

    // opens choose contact intent
    void getContacts() {
        Uri uri = Uri.parse("content://contacts");
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_CODE_CONTACT);
    }

}