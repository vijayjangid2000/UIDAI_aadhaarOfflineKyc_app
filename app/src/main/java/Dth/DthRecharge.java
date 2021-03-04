package Dth;

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
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import MobRecharge.OperatorModel;

import static maes.tech.intentanim.CustomIntent.customType;

public class DthRecharge extends AppCompatActivity
        implements SheetBill.ConfirmInterface {

    final int REQUEST_CODE_BOARD = 100;
    final String INTENT_PUT_EXTRA = "Provider";
    int activity_layout = R.layout.activity_dth_recharge2;
    TextView tv_boardHint, tv_boardName, tv_amountRule;
    TextInputLayout etl_mobileElec, etl_amount;
    TextInputEditText et_mobileElec, et_amount;
    Button btn_fetchBill;

    String subscriberId, amount;
    AlertDialog dialogView;

    SheetBill sheetClass;
    OperatorModel operatorModel;
    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dth_recharge2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dth Recharge");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);

        initViews();

        // to reduce one step
        chooseBoard();
    }

    void initViews() {

        userData = new UserData(this);
        et_mobileElec = findViewById(R.id.et_mobileElec);
        etl_mobileElec = findViewById(R.id.etl_mobileElec);

        tv_boardHint = findViewById(R.id.tv_selectBoard);
        tv_boardName = findViewById(R.id.tvb_selectBoard);

        tv_amountRule = findViewById(R.id.amountRules);

        View.OnClickListener onClickListener = view -> {
            view.setAlpha(0.5f);
            chooseBoard();
        };

        tv_boardHint.setOnClickListener(onClickListener);
        tv_boardName.setOnClickListener(onClickListener);

        btn_fetchBill = findViewById(R.id.btn_fetchElec);
        btn_fetchBill.setOnClickListener(view -> {
            view.requestFocus();
            fetchBill();
        });

        etl_amount = findViewById(R.id.etl_connectionNo);
        et_amount = findViewById(R.id.et_connectionNo);

        etl_amount.setVisibility(View.GONE);
        etl_mobileElec.setVisibility(View.GONE);
        btn_fetchBill.setVisibility(View.GONE);
        tv_amountRule.setVisibility(View.GONE);
        textWatcher();
    }

    void chooseBoard() {

        Intent intent = new Intent(this,
                Providers.class);
        startActivityForResult(intent, REQUEST_CODE_BOARD);
        customType(this, "bottom-to-up");
    }

    void fetchBill() {

        showProgressBar(true, "Fetching Bill, It will take few seconds");

        String url = "https://prod.excelonestopsolution.com/mobileapp/api/" + "fetch"
                + "?" + "userId" + "=" + userData.getId() + "&" + "token" + "=" +
                userData.getToken() + "&number=" + subscriberId + "&dob=" + userData.getBirthDate()
                + "&customerMobileNumber=" + "" + "&provider=" + operatorModel.getId();

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
                            operatorModel.setMobileNumber(subscriberId);
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
        sheetClass = new SheetBill(tempModel, this);
        sheetClass.show(getSupportFragmentManager(), "TagName");
    }

    @Override
    public void makeRechargeInterface() {
        sheetClass.dismiss();
        makeRecharge();
    }

    private void makeRecharge() {

        String url = "https://prod.excelonestopsolution.com/mobileapp/api/";

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

    void textWatcher() {

        subscriberId = Objects.requireNonNull(et_mobileElec.getText()).toString().trim();

        et_mobileElec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                subscriberId = et_mobileElec.getText().toString().trim();

                if (!isValidId(subscriberId)) {
                    etl_amount.setVisibility(View.GONE);
                    btn_fetchBill.setVisibility(View.GONE);
                } else {
                    etl_amount.setVisibility(View.VISIBLE);
                    tv_amountRule.setVisibility(View.VISIBLE);
                }

            }
        });

        amount = Objects.requireNonNull(et_amount.getText()).toString();

        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                amount = Objects.requireNonNull(et_amount.getText()).toString();

                int amountInt = 0;
                if (!amount.equals("")) amountInt = Integer.parseInt(amount);

                if (amountInt >= 50 && amountInt < 49000) {
                    btn_fetchBill.setVisibility(View.VISIBLE);
                } else {
                    btn_fetchBill.setVisibility(View.GONE);
                }

            }
        });
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public boolean isValidId(String subscriberId) {
        if (subscriberId == null) return false;
        if (subscriberId.length() == 0) return false;
        if (subscriberId.length() >= 10) {
            return true;
        } else return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == REQUEST_CODE_BOARD) {
            // for getting operator name from the operator list activity
            try {

                String jsonString = data.getStringExtra(INTENT_PUT_EXTRA);
                operatorModel = new Gson().fromJson(jsonString, OperatorModel.class);
                tv_boardName.setText(operatorModel.getProviderName());
                etl_mobileElec.setVisibility(View.VISIBLE);

            } catch (Exception ignore) {

                Toast.makeText(this, "Unable to Select Operator"
                        , Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_boardName.setAlpha(1f);
    }

}