package Water;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.vijayjangid.aadharkyc.HomePage_activity;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.mobileRecharge.BottomSheetConfirm;
import com.vijayjangid.aadharkyc.mobileRecharge.OperatorModel;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.MakeToast;
import com.vijayjangid.aadharkyc.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import maes.tech.intentanim.CustomIntent;

public class Water_Bill_activity extends AppCompatActivity
        implements BottomSheetConfirm.ConfirmInterface {

    final int REQUEST_CODE_OPERATOR = 100;
    final String INTENT_PUT_EXTRA = "Provider";
    OperatorModel operatorModel;

    TextInputLayout etl_mobile, etl_consumerId;
    TextInputEditText et_mobile, et_consumerId;
    Button btn_fetchBill;

    String mobileNumber, consumerId;
    BottomSheetConfirm sheetClass;
    AlertDialog dialogView;
    UserData userData;


    TextView tvb_changeBoard, tv_boardName;

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

        tvb_changeBoard = findViewById(R.id.tvb_board);
        tv_boardName = findViewById(R.id.tv_boardName);
        et_mobile = findViewById(R.id.et_mobileWater);
        et_consumerId = findViewById(R.id.et_consumerId);
        etl_mobile = findViewById(R.id.etl_mobileWater);
        etl_consumerId = findViewById(R.id.etl_consumer);
        btn_fetchBill = findViewById(R.id.btn_fetchWaterBill);

        etl_consumerId.setVisibility(View.GONE);
        etl_mobile.setVisibility(View.GONE);
        btn_fetchBill.setVisibility(View.GONE);

        initView();

        // initially we will ask to choose provider
        chooseProvider();
    }

    void initView() {

        View.OnClickListener onClickListener = view -> {
            view.setAlpha(0.4f);
            chooseProvider();
        };

        tv_boardName.setOnClickListener(onClickListener);
        tvb_changeBoard.setOnClickListener(onClickListener);
        userData = new UserData(this);

        btn_fetchBill.setOnClickListener(view -> {
            et_mobile.clearFocus();
            et_consumerId.clearFocus();
            finalConfirmation();
        });

        textWatcher();
    }

    void finalConfirmation() {

        mobileNumber = Objects.requireNonNull(et_mobile.getText()).toString();
        consumerId = Objects.requireNonNull(et_consumerId.getText()).toString();
        fetchBill();
    }

    void fetchBill() {

        showProgressBar(true, "Fetching Bill, It will take few seconds");

        String url = "https://prod.excelonestopsolution.com/mobileapp/api/" + "fetch"
                + "?" + "userId" + "=" + userData.getId() + "&" + "token" + "=" +
                userData.getToken() + "&number=" + mobileNumber + "&dob=" + userData.getBirthDate()
                + "&customerMobileNumber=" + consumerId + "&prov@ider=" + operatorModel.getId();

        Log.e("fetch bill", "=" + url);

        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {

                    dialogView.cancel();

                    try {
                        Log.e("fetch bill", "=" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("1")) {

                            JSONObject object = jsonObject.getJSONObject("billInfo");

                            String dueDate = "N/A";
                            if (object.has("Duedate"))
                                dueDate = object.getString("Duedate");
                            operatorModel.setDueDate(dueDate);
                            operatorModel.setCustomerName(object.getString("CustomerName"));
                            operatorModel.setBillCost(object.getString("Billamount"));
                            operatorModel.setMobileNumber(mobileNumber);
                            operatorModel.setPostpaid(true);

                            onBillReceived(operatorModel);

                        } else if (status.equalsIgnoreCase("200")) {
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
                        } else if (status.equalsIgnoreCase("0")) {
                            String message = jsonObject.getString("message");
                            MakeToast.show(this, message);
                        } else MakeToast.show(this, "Unable to fetch bill\ntry it manually");


                    } catch (JSONException e) {
                        MakeToast.show(this, "Unable to fetch bill\ntry it manually");
                        dialogView.cancel();
                    }
                },

                error -> {
                    dialogView.cancel();
                    MakeToast.show(this, "Unable to fetch bill\ntry it manually");
                }) {

        };

        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    void onBillReceived(OperatorModel tempModel) {
        sheetClass = new BottomSheetConfirm(tempModel, this);
        sheetClass.show(getSupportFragmentManager(), "TagName");
    }

    @Override
    public void makeRechargeInterface() {
        sheetClass.dismiss();
        makeRecharge();
    }

    private void makeRecharge() {

        String url = "https://prod.excelonestopsolution.com/mobileapp/api/fetch";

        showProgressBar(true, "Please wait...");

        Log.e("url api", "=" + url);
        final StringRequest request = new StringRequest(Request.Method.POST,
                url,
                response -> {
                    try {
                        dialogView.cancel();

                        Log.e("reponse api", "=" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("statusId");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")
                                || status.equalsIgnoreCase("24")
                                || status.equalsIgnoreCase("34")
                                || status.equalsIgnoreCase("3")) {

                            Dialog dialog = AppDialogs.transactionStatus(this, message, 1);
                            dialog.show();
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                                startActivity(new Intent(this, HomePage_activity.class));
                                finish();
                            });

                        } else if (status.equalsIgnoreCase("200")) {
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
                            Dialog dialog = AppDialogs.transactionStatus(this, message, 2);
                            dialog.show();
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                            });
                        }


                    } catch (JSONException e) {
                        dialogView.cancel();
                        Toast.makeText(this, "Error: " + e.getMessage()
                                , Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    dialogView.cancel();
                    Toast.makeText(this, "Error: " + error.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", userData.getId());
                param.put("token", userData.getToken());
                param.put("number", operatorModel.getMobileNumber());
                param.put("provider", operatorModel.getId());
                param.put("amount", operatorModel.getBillCost());
                //if (isElectricity) {
                param.put("CustomerName", operatorModel.getCustomerName());
                param.put("bill_due_date", operatorModel.getDueDate());
                param.put("customerMobileNumber", operatorModel.getMobileNumber());
                //}
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

    void chooseProvider() {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.addString("bbps", "1");
        Intent intent = new Intent(this, WaterProviders.class);
        startActivityForResult(intent, REQUEST_CODE_OPERATOR);
    }

    // this makes sure that there is 10 digits number
    void textWatcher() {

        mobileNumber = et_mobile.getText().toString().trim();

        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mobileNumber = et_mobile.getText().toString().trim();

                if (!isValidMobile(mobileNumber)) {
                    etl_mobile.setError("Enter 10 digit mobile number");
                    btn_fetchBill.setVisibility(View.GONE);
                } else {
                    etl_mobile.setError(null);
                    btn_fetchBill.setVisibility(View.VISIBLE);
                }

            }
        });

        et_consumerId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Objects.requireNonNull(consumerId = et_consumerId.getText().toString().trim());
                if (consumerId.length() != 0) {
                    etl_mobile.setVisibility(View.VISIBLE);
                } else {
                    // Error case
                    etl_mobile.setVisibility(View.GONE);
                    btn_fetchBill.setVisibility(View.GONE);
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

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OPERATOR) {
            try {
                // for getting operator name from the operator list activity
                String jsonString = data.getStringExtra(INTENT_PUT_EXTRA);
                operatorModel = new Gson().fromJson(jsonString, OperatorModel.class);
                tv_boardName.setText(operatorModel.getProviderName());
                etl_consumerId.setVisibility(View.VISIBLE);
            } catch (Exception ignored) {

            }
        }

    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
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

}
