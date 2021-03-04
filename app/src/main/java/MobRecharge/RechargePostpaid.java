package MobRecharge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.TimeUnit;

public class RechargePostpaid extends AppCompatActivity implements BottomSheetConfirm.ConfirmInterface {

    final int REQUEST_CODE_CONTACT = 1;
    final int REQUEST_CODE_OPERATOR = 99;
    final String INTENT_PUT_EXTRA = "OperatorModelObject";

    Button btn_fetchBill;
    TextInputEditText et_mobile;
    TextInputLayout etl_mobile;
    LinearLayout ll_operatorPostPaid;
    TextView tvb_changeOperatorPostPaid, tv_operatorName, tv_contactName;
    String mobileNumber, temporaryNumber;
    OperatorModel operatorModel;
    BottomSheetConfirm sheetClass;
    Spinner spn_payMode;

    AlertDialog dialogView;

    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_postpaid);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("PostPaid");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);
        initViews();
    }

    void initViews() {

        btn_fetchBill = findViewById(R.id.fetchBillPostpaid);
        etl_mobile = findViewById(R.id.phoneEtLPost);
        et_mobile = findViewById(R.id.phoneEtPost);
        tvb_changeOperatorPostPaid = findViewById(R.id.tvb_changeOperatorPostPaid);
        tv_operatorName = findViewById(R.id.tv_operatorNamePostPaid);
        ll_operatorPostPaid = findViewById(R.id.ll_operatorPostPaid);
        tv_contactName = findViewById(R.id.contact_name);
        userData = new UserData(this);

        // setting the spinner for choosing the payment type
        spn_payMode = findViewById(R.id.spn_payMode);
        String[] requestToList = new String[2];
        requestToList[0] = "Payment System: BBPS ONE";
        requestToList[1] = "Payment System: BBPS TWO";
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, requestToList);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_payMode.setAdapter(dataAdapter);

        spn_payMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                btn_fetchBill.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                btn_fetchBill.setVisibility(View.GONE);
                Toast.makeText(RechargePostpaid.this, "Please select Payment Type"
                        , Toast.LENGTH_SHORT).show();
            }

        });

        tv_contactName.setVisibility(View.GONE);
        textWatcher();

        btn_fetchBill.setOnClickListener(view -> {
            fetchBill();
        });

        tvb_changeOperatorPostPaid.setOnClickListener(view -> {
            changeOperatorRequest();
            view.setAlpha(0.4f);
        });

        // on end icon, contact choosing work
        etl_mobile.setEndIconOnClickListener(v -> getContacts());

        /* THESE VIEW WILL BE HIDDEN INITIALLY*/
        spn_payMode.setVisibility(View.GONE);
        btn_fetchBill.setVisibility(View.GONE);
        tvb_changeOperatorPostPaid.setVisibility(View.GONE);
    }

    // open operator activity for choosing the company of the sim card
    // that will be recharged.
    void changeOperatorRequest() {
        Intent intent = new Intent(RechargePostpaid.this, SelectOperator.class);
        intent.putExtra("PostPaid", true);
        startActivityForResult(intent, REQUEST_CODE_OPERATOR);
    }

    void fetchBill() {

        showProgressBar(true, "Fetching Bill, It will take few seconds");

        String url = "https://prod.excelonestopsolution.com/mobileapp/api/" + "fetch"
                + "?" + "userId" + "=" + userData.getId() + "&" + "token" + "=" +
                userData.getToken() + "&number=" + mobileNumber + "&dob=" + userData.getBirthDate()
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

    void unknownWorkRelatedCommission(OperatorModel operatorModel) {

        String url = "";
        if (spn_payMode.getSelectedItemPosition() == 0) {
            url = "https://prod.excelonestopsolution.com/mobileapp/api/bbps-one-preslip";
        } else {
            url = "https://prod.excelonestopsolution.com/mobileapp/api/bbps-two-preslip";
        }

        url = url + "?" + "userId" + "=" + userData.getId()
                + "&" + "token" + "=" + userData.getToken()
                + "&number=" + operatorModel.getMobileNumber()
                + "&amount=" + operatorModel.getBillCost()
                + "&provider=" + operatorModel.getId();

        Log.e("url slip", "=" + url);

        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("resp slip", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            String charge = (jsonObject.getJSONObject("billDetails").has("Convenience Fee")) ?
                                    jsonObject.getJSONObject("billDetails").getString("Convenience Fee") : "0";
                            String comm = (jsonObject.getJSONObject("billDetails").has("Commission")) ?
                                    jsonObject.getJSONObject("billDetails").getString("Commission") : "0";

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
                        } else {

                            MakeToast.show(this, "No offer found for this operator(c)");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        MakeToast.show(this, "No offer found for this operator(c)");
                    }
                },

                error -> {
                    MakeToast.show(this, "Unable to Fetch Bill");
                }) {

        };

        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void makeRechargeInterface() {
        sheetClass.dismiss();
        makeRecharge();
    }

    private void makeRecharge() {

        String url;
        if (spn_payMode.getSelectedItemPosition() == 0)
            url = "https://prod.excelonestopsolution.com/mobileapp/api/bbps-one-preslip";
        else
            url = "https://prod.excelonestopsolution.com/mobileapp/api/bbps-two-preslip";

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

    @Override
    protected void onResume() {
        super.onResume();
        float alphaValue = 1;
        tvb_changeOperatorPostPaid.setAlpha(alphaValue);
        btn_fetchBill.setAlpha(alphaValue);
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
                    tvb_changeOperatorPostPaid.setVisibility(View.GONE);
                } else {
                    etl_mobile.setError(null);
                    tvb_changeOperatorPostPaid.setVisibility(View.VISIBLE);
                }

                if (temporaryNumber != null) {
                    if (!temporaryNumber.equals(mobileNumber))
                        tv_contactName.setVisibility(View.GONE);
                    else tv_contactName.setVisibility(View.VISIBLE);
                }
            }
        });

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
                et_mobile.setText(number);
            }
        } else if (requestCode == REQUEST_CODE_OPERATOR) {
            // for getting operator name from the operator list activity
            try {
                String jsonString = intent.getStringExtra(INTENT_PUT_EXTRA);
                operatorModel = new Gson().fromJson(jsonString, OperatorModel.class);
                tv_operatorName.setText(operatorModel.getProviderName());
                spn_payMode.setVisibility(View.VISIBLE);
            } catch (Exception ignore) {
                spn_payMode.setVisibility(View.GONE);
                Toast.makeText(this, "Unable to Select Operator, Can't connect to server!"
                        , Toast.LENGTH_LONG).show();
            }

        }
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

    // opens choose contact intent
    void getContacts() {
        Uri uri = Uri.parse("content://contacts");
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_CODE_CONTACT);

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

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}