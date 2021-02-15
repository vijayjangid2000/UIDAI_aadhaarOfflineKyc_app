package com.vijayjangid.aadharkyc.mobileRecharge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.model.Offer;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.MakeToast;
import com.vijayjangid.aadharkyc.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import maes.tech.intentanim.CustomIntent;

import static maes.tech.intentanim.CustomIntent.customType;

public class RechargePrepaid extends AppCompatActivity
        implements View.OnClickListener, BottomSheetConfirm.ConfirmInterface {

    // for operator and contact chooser
    final int REQUEST_CODE_CONTACT = 1;
    final int REQUEST_CODE_OPERATOR = 99;
    final int REQUEST_CODE_PLANS = 100;
    // name of intent extra
    final String INTENT_PUT_EXTRA = "OperatorModelObject";

    TextInputEditText etMobileNumber, etAmount;
    TextInputLayout etMobileNumberLayout, etAmountLayout;
    TextView tv_operatorName, tvb_operatorChange, tv_contactName,
            tvb_prepaid, tvb_postpaid, tvbViewPlans;
    String mobileNumber, temporaryNumber, amount;
    Button btn_proceed;
    LinearLayout linearL_operatorChange, linearL_recents;
    boolean isPostPaidMode;

    // To cancel any type of dialog like loading etc.
    AlertDialog dialogView;

    // this contains all possible userData
    UserData userData;
    // This is the bottom sheet for final confirmation
    BottomSheetConfirm sheetClass;
    // operatorModel object will be initialized when user choose the operator
    private OperatorModel operatorModel;

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
        linearL_recents = findViewById(R.id.ll_recents);

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

        userData = new UserData(this);

        // setting recent recharge, if found
        setRecent();

        etMobileNumber.clearFocus();
        etAmount.clearFocus();

        linearL_operatorChange.setVisibility(View.GONE);
        etAmountLayout.setVisibility(View.GONE);
        tvbViewPlans.setVisibility(View.GONE);
        btn_proceed.setVisibility(View.GONE);
        textWatcher();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.proceedRecharge_btn:
                onProceedRecharge();
                /*Toast.makeText(getApplicationContext(),
                        "Recharge Successful of Rs. " + amount +
                                " on mobile - " + mobileNumber, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, ChoosePaymentOption.class));
                customType(this, "bottom-to-up");*/
                break;

            case R.id.changeOperatorTv:
                // for changing operator (opens operator activity)
                tvb_operatorChange.setAlpha((float) 0.4);
                chooseOperator();
                customType(this, "bottom-to-up");
                break;

            case R.id.prepaid_TextB:
                tvb_prepaid.setTextColor(Color.WHITE);
                tvb_prepaid.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.btn_gradient_style));
                tvb_postpaid.setTextColor(getColor(R.color.colorPrimary));
                tvb_postpaid.setBackgroundColor(Color.TRANSPARENT);
                break;

            case R.id.postpaid_TextB:
                tvb_postpaid.setTextColor(Color.WHITE);
                tvb_postpaid.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.btn_gradient_style));
                tvb_prepaid.setTextColor(getColor(R.color.colorPrimary));
                tvb_prepaid.setBackgroundColor(Color.TRANSPARENT);
                startActivity(new Intent(RechargePrepaid.this,
                        RechargePostpaid.class));
                finish();
                break;

            case R.id.viewPlansTvb:
                tvbViewPlans.setAlpha((float) 0.4);
                customType(this, "bottom-to-up");
                getOffer(etMobileNumber.getText().toString());
                break;
        }
    }

    // open operator activity for choosing the company of the sim card
    // that will be recharged.
    void chooseOperator() {

        SessionManager sessionManager = new SessionManager(this);
        sessionManager.addString("bbps", "1");
        Intent intent = new Intent(RechargePrepaid.this, SelectOperator.class);
        startActivityForResult(intent, REQUEST_CODE_OPERATOR);

    }

    // opens choose contact intent
    void getContacts() {
        Uri uri = Uri.parse("content://contacts");
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_CODE_CONTACT);
    }

    // Calls the stringRequest
    private void getOffer(String number) {

        showProgressBar(true, "Finding Best Offers\nPlease wait");

        Log.e("provider", "=" + operatorModel.getProviderName());

        String url = "https://prod.excelonestopsolution.com/mobileapp/api/"
                + "get-special-offer" + "?" + "userId" + "=" + userData.getId()
                + "&" + "token" + "=" + userData.getToken()
                + "&mobile_number=" + number + "&provider=" + operatorModel.getProviderName();

        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("resp offer", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("SUCCESS")) {
                            ArrayList<Offer> offerList = new ArrayList<>();

                            JSONArray jsonArray = jsonObject.getJSONArray("Response");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Offer offer = new Offer();
                                JSONObject object = jsonArray.getJSONObject(i);
                                //offer.setCommAmount(object.getString("commAmount"));
                                //offer.setCommType(object.getString("commType"));
                                offer.setPrice(object.getString("price"));
                                offer.setOffer(object.getString("offer"));
                                offerList.add(offer);
                            }

                            viewPlanOpen(offerList);

                        } else if (status.equalsIgnoreCase("200")) {

                            dialogView.cancel();

                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);

                        } else if (status.equalsIgnoreCase("300")) {
                            dialogView.cancel();

                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);

                        } else if (status.equalsIgnoreCase("0")) {
                            dialogView.cancel();

                            String message = jsonObject.getString("content");
                            MakeToast.show(this, message);

                        } else {
                            dialogView.cancel();
                            MakeToast.show(this, "No offer found for this operator(c)");

                        }

                    } catch (JSONException e) {
                        dialogView.cancel();
                        e.printStackTrace();
                        MakeToast.show(this, "No offer found for this operator(c)");
                    }
                },

                error -> {
                    MakeToast.show(this, "No offer found for this operator(e)");
                    dialogView.cancel();
                }) {
        };


        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    // getOffer Calls it for opening the activity
    void viewPlanOpen(final ArrayList<Offer> offerList) {

        /*we are sending the arrayList to the plans activity
         * and there we will extract the information and set to views*/

        dialogView.cancel();

        ArrayList<String> convertedArrayList = new ArrayList<>();

        for (int i = 0; i < offerList.size(); i++) {

            // ,,,, will be used to split

            String toAdd = offerList.get(i).getCommAmount()
                    + ",,,," + offerList.get(i).getCommType() + ",,,,"
                    + offerList.get(i).getOffer() + ",,,,"
                    + offerList.get(i).getPrice();

            convertedArrayList.add(toAdd);
        }

        Intent intent = new Intent(this, BrowsePlans.class);
        intent.putStringArrayListExtra("browsePlans", convertedArrayList);
        startActivityForResult(intent, REQUEST_CODE_PLANS);

    }

    void onProceedRecharge() {
        // date related to operator is done in activity result

        // other data will be here

        operatorModel.setMobileNumber(mobileNumber);
        operatorModel.setBillCost(amount);
        operatorModel.setPostpaid(false);
        sheetClass = new BottomSheetConfirm(operatorModel, this);
        sheetClass.show(getSupportFragmentManager(), "TagName");
    }

    @Override
    public void makeRechargeInterface() {
        sheetClass.dismiss();
        showProgressBar(true, "Recharging...\nPlease wait");
        makeRecharge();
    }

    private void makeRecharge() {


        String url = "https://prod.excelonestopsolution.com/mobileapp/api/make-recharge";

        Log.e("url api", "=" + url);

        final StringRequest request = new StringRequest(Request.Method.POST,
                url,
                response -> {
                    try {
                        dialogView.cancel();
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")
                                || status.equalsIgnoreCase("2")
                                || status.equalsIgnoreCase("3")) {

                            Dialog dialog = AppDialogs.transactionStatus
                                    (this, message, Integer.parseInt(status));
                            dialog.setCancelable(false);
                            dialog.show();
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> dialog.cancel());

                        } else if (status.equalsIgnoreCase("200") || status.equalsIgnoreCase("300")) {
                            Toast.makeText(RechargePrepaid.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Dialog dialog = AppDialogs.transactionStatus(this, message, 2);
                            dialog.show();
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                            });
                        }

                    } catch (JSONException e) {
                        dialogView.cancel();
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    dialogView.cancel();
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(userData.getId()));
                param.put("token", String.valueOf(userData.getToken()));
                param.put("number", mobileNumber);
                param.put("provider", operatorModel.getId());
                param.put("amount", amount);

                Log.e("param", "=" + param.toString());
                return param;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    // this set the last numbers that are recharged
    void setRecent() {
        ListView listView = findViewById(R.id.recents_listView);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("apple");
        listView.setAdapter(new RecentListAdapter(arrayList));
    }

    /*Two tasks
     * 1. choose contact (result code 1)
     * 2. choose operator (result code 99)*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
                tv_contactName.setText("Contact Name: " + name);
                etMobileNumber.setText(number);
            }
        } else if (requestCode == REQUEST_CODE_OPERATOR) {
            // for getting operator name from the operator list activity
            try {
                String jsonString = intent.getStringExtra(INTENT_PUT_EXTRA);
                operatorModel = new Gson().fromJson(jsonString, OperatorModel.class);
                tv_operatorName.setText(operatorModel.getProviderName());
                etAmountLayout.setVisibility(View.VISIBLE);
                tvbViewPlans.setVisibility(View.VISIBLE);
            } catch (Exception ignore) {
            }

        } else if (requestCode == REQUEST_CODE_PLANS) {
            try {
                // when user choose plan for their mobile recharge
                String planPrice = intent.getStringExtra("PlanPrice");
                etAmount.setText(planPrice);
                etAmount.setSelection(planPrice.length());
                etAmount.clearFocus();
            } catch (Exception ignored) {

            }
        }
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void showCustomSnack(View view, String message,
                                boolean isIndefinite, boolean isError) {

        hideKeyboard(this);

        Snackbar snackbar = Snackbar.make(view.getRootView(), message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getColor(R.color.colorPrimary));
        if (isIndefinite) snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        if (isError) snackBarView.setBackgroundColor(getColor(R.color.Red));

        snackBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view,
        // so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one,
        // just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    // for checking internet connectivity
    public boolean checkConnected() {
        View view = getLayoutInflater().inflate(R.layout.layout_no_internet, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        TextView textView = view.findViewById(R.id.no_internet_tv);
        textView.setText("You are Offline!\nPlease Connect to Internet");

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setCancelable(false);
        final AlertDialog dialogViewInternet = ab.create();
        dialogViewInternet.setView(view);

        // this checks if connected or not as a service
        Tovuti.from(this).monitor(new Monitor.ConnectivityListener() {
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                if (isConnected)
                    if (dialogViewInternet.isShowing()) dialogViewInternet.cancel();
                    else if (!dialogViewInternet.isShowing()) dialogViewInternet.show();
            }
        });

        // this checks if connected or not, but instantly
        // so that we can return instantly
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null)
                return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ||
                        activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE ||
                        activeNetwork.getType() == ConnectivityManager.TYPE_VPN;
        }

        return false;
    }

    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.layout_progressbar, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        AlertDialog.Builder alertBldr_loading = new AlertDialog.Builder(this)
                .setCancelable(false);
        dialogView = alertBldr_loading.create();
        dialogView.setView(view);
        Window window = dialogView.getWindow();
        if (window != null) window.setBackgroundDrawableResource(R.color.Transparent);
        dialogView.show();
    }

    // this makes sure that there is 10 digits number
    void textWatcher() {

        mobileNumber = etMobileNumber.getText().toString().trim();

        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mobileNumber = etMobileNumber.getText().toString().trim();

                if (!isValidMobile(mobileNumber)) {
                    etMobileNumberLayout.setError("Enter 10 digit mobile number");
                    tvbViewPlans.setVisibility(View.GONE);
                    linearL_operatorChange.setVisibility(View.GONE);
                    btn_proceed.setVisibility(View.GONE);
                } else {
                    etMobileNumberLayout.setError(null);
                    linearL_operatorChange.setVisibility(View.VISIBLE);
                    if (operatorModel != null) tvbViewPlans.setVisibility(View.VISIBLE);
                }

                if (temporaryNumber != null) {
                    if (!temporaryNumber.equals(mobileNumber))
                        tv_contactName.setVisibility(View.GONE);
                    else tv_contactName.setVisibility(View.VISIBLE);
                }
            }
        });

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Objects.requireNonNull(amount = etAmount.getText().toString().trim());
                if (amount.length() != 0) {
                    btn_proceed.setVisibility(View.VISIBLE);
                } else {
                    btn_proceed.setVisibility(View.GONE);
                }
            }
        });
    }

    public boolean isValidMobile(String mobile) {
        if (mobile == null) return false;
        if (mobile.length() == 0) return false;
        char first = mobile.charAt(0);
        if ((first == '7' || first == '8' ||
                first == '9' || first == '6') && mobile.length() == 10) {
            return true;
        } else return false;
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

    // for changing the alpha values to normal after user choose operator
    @Override
    protected void onResume() {
        super.onResume();
        float alphaValue = 1;
        tvb_operatorChange.setAlpha(alphaValue);
        tvbViewPlans.setAlpha(alphaValue);
        tvb_prepaid.performClick();
    }

    // Recent List custom Adapter
    public class RecentListAdapter implements ListAdapter {

        ArrayList<String> arrayList;

        public RecentListAdapter(ArrayList<String> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(RechargePrepaid.this);
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