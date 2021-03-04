package a2z_wallet;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.adapter.AccountListAdapter;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.model.BankDetail;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.AppUitls;
import com.vijayjangid.aadharkyc.util.AutoLogoutManager;
import com.vijayjangid.aadharkyc.util.InternetConnection;
import com.vijayjangid.aadharkyc.util.MakeToast;
import com.vijayjangid.aadharkyc.util.SoftKeyboard;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class A2ZWalletActivity extends AppCompatActivity implements

        RemitterRegisterOtpFragmentA2ZWallet.OnFragmentInteractionListener,
        RegisterRemitterFragmetA2ZWallet.OnFragmentInteractionListener,
        BeneListFragmentA2ZWallet.OnFragmentInteractionListener,
        AddBeneFragmentA2ZWallet.OnFragmentInteractionListener,
        MoneyTransferFragmentA2ZWallet.OnFragmentInteractionListener {

    private static final String TAG = "A2ZWalletActivity";
    final int REQUEST_CODE_CONTACT = 1;
    EditText et_accNum;
    RecyclerView rv_accounts;
    UserData userData;
    private int layout = R.layout.activity_a2z_wallet;
    private boolean isTransferFragment = false;
    private TextInputEditText et_mobile;
    private TextInputLayout etl_mobile;
    private ImageButton btn_search, btn_refresh, btn_search_acc;
    private String strMobileNumber;
    private ProgressBar progressBarMobile;
    private TextView tv_errorHint, tv_senderName, tv_senderLimit;
    private FrameLayout dmt_fragmentContainer;
    private MenuItem menuItem_addBeneficiary, menuItem_history;
    private LinearLayout ll_searchRefreshNumber, ll_remitterInfo, ll_accSearch;
    private Fragment fragment;
    private Toolbar toolbar;
    private ArrayList<BankDetail> accList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a2z_wallet);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("A2Z Wallet");
        toolbar.setSubtitle("Balance - Rs. " + SharedPref.getInstance(this).getUserBalance());
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        userData = new UserData(this);
        initView();
        btn_search.setOnClickListener(view -> {
            if (InternetConnection.isConnected(this)) {
                if (validateNumber()) {
                    verifyNumberToServer();
                }
            }
        });
        btn_refresh.setOnClickListener(view -> {
            et_mobile.setText("");
            recreate();
        });
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().detach(fragment).commit();
            fragment = null;
        }
        if (savedInstanceState == null) {
            // Log.e("RemitterRegisterOtpFragmentA2ZWallet","enter");
            fragment = RegisterRemitterFragmetA2ZWallet.newInstance(strMobileNumber);
            getSupportFragmentManager().beginTransaction().add(R.id.dmt_fragmentContainer, fragment).commit();
        }


    }

    private void initView() {
        etl_mobile = findViewById(R.id.edl_numberdmt);
        et_mobile = findViewById(R.id.ed_numberdmt);
        btn_search = findViewById(R.id.btn_search);
        progressBarMobile = findViewById(R.id.progressBar);
        btn_search_acc = findViewById(R.id.btn_searchAcc);
        et_accNum = findViewById(R.id.ed_account_number);
        ll_accSearch = findViewById(R.id.lin_acc_search);
        rv_accounts = findViewById(R.id.recyclerView_acc);
        btn_refresh = findViewById(R.id.btn_refresh);
        tv_errorHint = findViewById(R.id.tv_error_hint);
        ll_remitterInfo = findViewById(R.id.ll_remitter_info);
        tv_senderLimit = findViewById(R.id.tv_sender_limit);
        tv_senderName = findViewById(R.id.tv_senderName);
        dmt_fragmentContainer = findViewById(R.id.dmt_fragmentContainer);
        ll_searchRefreshNumber = findViewById(R.id.ll_search_refresh_number);

        btn_search_acc.setOnClickListener(view -> {
            if (et_accNum.getText().toString() != "") {
                accountSearch(et_accNum.getText().toString());
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
                    SoftKeyboard.hide(A2ZWalletActivity.this);
                    btn_search.callOnClick();
                }


            }
        });

        // on end icon, contact choosing work
        etl_mobile.setEndIconOnClickListener(v -> getContacts());
    }

    private void accountSearch(String acc) {

        String url = APIs.ACCOUNT_SEARCH;

        Log.e("acc search", "" + url);
        progressBarMobile.setVisibility(View.VISIBLE);

        final StringRequest request = new StringRequest(Request.Method.POST,
                url,
                response -> {
                    try {
                        progressBarMobile.setVisibility(View.GONE);

                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("response acc", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {

                            accList = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                BankDetail bankDetail = new BankDetail();
                                bankDetail.setMobile(jsonObject1.getString("mobile_number"));
                                bankDetail.setAccountNumber(jsonObject1.getString("account_number"));
                                bankDetail.setIfsc(jsonObject1.getString("ifsc"));
                                bankDetail.setBeneName(jsonObject1.getString("name"));
                                bankDetail.setBalance(jsonObject1.getString("remaining_limit"));

                                accList.add(bankDetail);
                            }
                            if (accList.size() > 0) {
                                rv_accounts.setHasFixedSize(false);
                                rv_accounts.setLayoutManager(new LinearLayoutManager(A2ZWalletActivity.this));

                                AccountListAdapter adapter = new AccountListAdapter(A2ZWalletActivity.this, accList);
                                rv_accounts.setAdapter(adapter);

                                adapter.setupListener(new AccountListAdapter.OnAccountListAdapterClickListener() {
                                    @Override
                                    public void clickToMobile(int pos) {

                                        et_mobile.setText("" + accList.get(pos).getMobile());
                                    }
                                });
                            }
                        } else showConnectionError("message : " + message);
                    } catch (Exception e) {
                        progressBarMobile.setVisibility(View.GONE);
                        showConnectionError("Something went wrong! Try again");

                    }
                },
                error -> {
                    progressBarMobile.setVisibility(View.GONE);

                    showConnectionError("Something went wrong! Try again");

                }) {


            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", userData.getToken());
                params.put("userId", userData.getId());
                params.put("accountNumber", acc);
                Log.e("params", "=" + params.toString());
                return params;

            }

            ;
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void verifyNumberToServer() {

        String url = APIs.VERIFY_NUMBER_A2ZWallet + "?" + APIs.USER_TAG + "=" + userData.getId()
                + "&" + APIs.TOKEN_TAG + "=" + userData.getToken() + "&mobile_number=" + strMobileNumber;

        Log.e("verifyNumberToServer", "" + url);
        progressBarMobile.setVisibility(View.VISIBLE);
        btn_search.setVisibility(View.GONE);
        tv_errorHint.setVisibility(View.GONE);
        et_mobile.setAlpha(0.7f);
        et_mobile.setEnabled(false);
        final StringRequest request = new StringRequest(Request.Method.GET,
                url,
                response -> {
                    try {

                        btn_refresh.setVisibility(View.VISIBLE);
                        progressBarMobile.setVisibility(View.GONE);
                        btn_search.setVisibility(View.VISIBLE);
                        ll_accSearch.setVisibility(View.GONE);

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("13")) {

                            menuItem_addBeneficiary.setVisible(true);
                            menuItem_history.setVisible(true);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                            String strMobile = jsonObject1.getString("mobile");
                            String strFname = jsonObject1.getString("fname");
                            String strLname = jsonObject1.getString("lname");
                            String strRemBal = jsonObject1.getString("rem_bal");
                            String strVerify = jsonObject1.getString("verify");

                            if (strVerify.equalsIgnoreCase("1")) {
                                ll_remitterInfo.setVisibility(View.VISIBLE);
                                tv_senderName.setText(strFname + " " + strLname);

                                double amount = Double.parseDouble(strRemBal);
                                /*if(amount>25000){
                                    amount = amount-25000;
                                }*/
                                tv_senderLimit.setText(String.valueOf(amount));
                            }


                            fragment = BeneListFragmentA2ZWallet.newInstance(strMobileNumber, tv_senderName.getText().toString());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.dmt_fragmentContainer, fragment)
                                    .commit();
                            dmt_fragmentContainer.setVisibility(View.VISIBLE);


                        } else if (status.equalsIgnoreCase("12")) {

                            menuItem_addBeneficiary.setVisible(false);
                            fragment = RemitterRegisterOtpFragmentA2ZWallet.newInstance(strMobileNumber);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.dmt_fragmentContainer, fragment)
                                    .commit();
                            dmt_fragmentContainer.setVisibility(View.VISIBLE);
                        } else if (status.equalsIgnoreCase("11")) {
                            menuItem_addBeneficiary.setVisible(false);
                            fragment = RegisterRemitterFragmetA2ZWallet.newInstance(strMobileNumber);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.dmt_fragmentContainer, fragment)
                                    .commit();
                            dmt_fragmentContainer.setVisibility(View.VISIBLE);
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
                        } else showConnectionError("message : " + message);
                    } catch (Exception e) {
                        progressBarMobile.setVisibility(View.GONE);
                        btn_search.setVisibility(View.VISIBLE);
                        et_mobile.setAlpha(1f);
                        et_mobile.setEnabled(true);
                        showConnectionError("Something went wrong! Try again" + e.getMessage());
                        e.printStackTrace();

                    }
                },
                error -> {
                    progressBarMobile.setVisibility(View.GONE);
                    btn_search.setVisibility(View.VISIBLE);
                    et_mobile.setAlpha(1f);
                    et_mobile.setEnabled(true);
                    showConnectionError("Something went wrong! Try again ");

                }) {

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void showConnectionError(String message) {
        tv_errorHint.setText(message);
        tv_errorHint.setVisibility(View.VISIBLE);
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

    @Override
    public void onFragmentInteraction(int type, String data) {

        if (type == 1) {//register remitter(data = strMobileNumber)
            menuItem_addBeneficiary.setVisible(false);
            menuItem_history.setVisible(false);
            strMobileNumber = data;
            fragment = RemitterRegisterOtpFragmentA2ZWallet.newInstance(strMobileNumber);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dmt_fragmentContainer, fragment)
                    .commit();
        } else if (type == 2) {//remitter register otp

            Dialog dialog = AppDialogs.transactionStatus(this, "Remitter Added Successfully", 1);
            Button btn_ok = dialog.findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(view -> {
                dialog.dismiss();
            });
            dialog.setOnDismissListener(dialog1 -> {
                btn_search.callOnClick();
            });
            dialog.show();
        } else if (type == 3 && data.equalsIgnoreCase("AddBeneFragmentDMT2")) {// BeneList callback for add bene( data = " AddBeneFragmentDMT2")
            menuItem_addBeneficiary.setVisible(false);
            menuItem_history.setVisible(false);
            fragment = AddBeneFragmentA2ZWallet.newInstance(strMobileNumber);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dmt_fragmentContainer, fragment)
                    .commit();
        } else if (type == 4 && data.equalsIgnoreCase("BeneficiaryAdded")) {//beneficiary added successfully (data = "BeneficiaryAdded)
            btn_search.callOnClick();
        }
    }

    @Override
    public void onFragmentInteraction(int type, Beneficiary beneficiary) {
        if (type == 5) {//transfer money from beneListFragment
            fragment = MoneyTransferFragmentA2ZWallet.newInstance(beneficiary, strMobileNumber);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dmt_fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
            toolbar.setTitle("Transfer Money");
            //we don't want to show any reset and searchFundTranser option while transfer money
            menuItem_addBeneficiary.setVisible(false);
            menuItem_history.setVisible(false);
            isTransferFragment = true;
            ll_searchRefreshNumber.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_beneficiary, menu);

        menuItem_addBeneficiary = menu.findItem(R.id.add_beneficiary);
        menuItem_history = menu.findItem(R.id.history);

        menuItem_addBeneficiary.setVisible(false);
        menuItem_history.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuItem_addBeneficiary.setVisible(false);
        menuItem_history.setVisible(false);
        if (item.getItemId() == R.id.history) {
            fragment = RemTransactionsFragment.newInstance(strMobileNumber);
        } else {
            fragment = AddBeneFragmentA2ZWallet.newInstance(strMobileNumber);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dmt_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
        isTransferFragment = true;
        return true;
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

    @Override
    public void onBackPressed() {
        if (isTransferFragment) {// this thing is handling with fragment backstatck
            menuItem_addBeneficiary.setVisible(true);
            menuItem_history.setVisible(true);
            toolbar.setTitle("A2Z Wallet");
            isTransferFragment = false;
            ll_searchRefreshNumber.setVisibility(View.VISIBLE);
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

}
