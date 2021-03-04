package Aeps;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.HomePage_activity;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.adapter.AepsSettlementAdapter;
import com.vijayjangid.aadharkyc.adapter.MyBankListAdapter;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.model.BankDetail;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AepsSettllementActivity extends AppCompatActivity implements AddBankFragment.OnFragmentInteractionListener {

    RecyclerView recyclerView_bd;
    RecyclerView recyclerView;
    ArrayList<BankDetail> bankList;
    ArrayList<BankDetail> setBankList;
    TextView no_layout, no_layout2;
    UserData userData;
    boolean edit;
    MenuItem add_bank_menu;
    boolean isTransferFragment = false;
    private int layout = R.layout.activity_aeps_settllement;
    private ProgressBar progressBar;
    private Fragment fragment;
    private FrameLayout dmt_fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps_settllement);

        userData = new UserData(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("AEPS Settlement");
        getSupportActionBar().setSubtitle("Balance - Rs. " + SharedPref.getInstance(this).getUserBalance());
        //   getSupportActionBar().setNavigationOnClickListener(view -> onBackPressed());

        dmt_fragmentContainer = findViewById(R.id.dmt_fragmentContainer);
        no_layout = findViewById(R.id.no_layout);
        no_layout2 = findViewById(R.id.no_layout2);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView_bd = findViewById(R.id.recyclerView_bd);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(AepsSettllementActivity.this, LinearLayoutManager.HORIZONTAL, false));


        recyclerView_bd.setHasFixedSize(false);
        recyclerView_bd.setLayoutManager(new LinearLayoutManager(AepsSettllementActivity.this, LinearLayoutManager.HORIZONTAL, false));


        bankList = new ArrayList<>();
        setBankList = new ArrayList<>();


        getBankACList();
    }

    private void getBankACList() {
        progressBar.setVisibility(View.VISIBLE);
        String url = APIs.GET_MY_BANK_DETAILS + "?" + APIs.USER_TAG + "=" + userData.getId()
                + "&" + APIs.TOKEN_TAG + "=" + userData.getToken();
        Log.e("url bank details", "=" + url);
        final StringRequest request = new StringRequest(Request.Method.GET,
                url,
                response -> {
                    try {

                        Log.e("response settlement", "=" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {

                            JSONArray listArray = jsonObject.getJSONArray("data");

                            Log.e("list", "=" + listArray.length());
                            for (int i = 0; i < listArray.length(); i++) {
                                JSONObject finalObject = listArray.getJSONObject(i);

                                BankDetail serviceManagement = new BankDetail();
                                serviceManagement.setId(finalObject.getString("id"));
                                serviceManagement.setBeneName(finalObject.getString("name"));
                                serviceManagement.setStatus(finalObject.getString("status_id"));
                                serviceManagement.setAccountNumber(finalObject.getString("account_number"));
                                serviceManagement.setBranchName(finalObject.getString("branch_name"));
                                serviceManagement.setBankName(finalObject.getString("bank_name"));
                                serviceManagement.setIfsc(finalObject.getString("ifsc"));

                                bankList.add(serviceManagement);
                                if (finalObject.getString("status_id").equalsIgnoreCase("1")) {
                                    serviceManagement.setBalance(finalObject.getString("balance"));
                                    serviceManagement.setAeps_bloack_amount(finalObject.getString("aeps_bloacked_amount"));
                                    serviceManagement.setAeps_charge(finalObject.getString("aeps_charge"));

                                    setBankList.add(serviceManagement);
                                }

                            }
                            Log.e("list 1", "=" + bankList.size());
                            if (bankList.size() > 0) {
                                MyBankListAdapter adapter = new MyBankListAdapter(bankList, AepsSettllementActivity.this);
                                recyclerView_bd.setAdapter(adapter);

                                adapter.setupListener(new MyBankListAdapter.OnMyBankListAdapterClickListener() {
                                    @Override
                                    public void edit(int pos) {
                                        //Toast.makeText(AepsSettllementActivity.this, "click", Toast.LENGTH_SHORT).show();
                                        edit = true;
                                        setFragment(true, pos);
                                    }
                                });
                            } else {
                                recyclerView_bd.setVisibility(View.GONE);
                                no_layout.setVisibility(View.VISIBLE);
                            }

                            if (setBankList.size() > 0) {
                                AepsSettlementAdapter adapter1 = new AepsSettlementAdapter(setBankList, AepsSettllementActivity.this);
                                recyclerView.setAdapter(adapter1);

                                adapter1.setupListener(new AepsSettlementAdapter.OnAepsAdapterClickListener() {
                                    @Override
                                    public void transfer(int pos, String amount) {

                                        //  Toast.makeText(AepsSettllementActivity.this, "work is in stil progress", Toast.LENGTH_SHORT).show();
                                        showConfirmDialog(pos, amount);
                                    }
                                });
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                no_layout2.setVisibility(View.VISIBLE);
                            }


                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(AepsSettllementActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(AepsSettllementActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            no_layout2.setVisibility(View.VISIBLE);
                            //tv_error_hint.setVisibility(View.VISIBLE);
                        }


                    } catch (JSONException e) {

                        //tv_error_hint.setVisibility(View.VISIBLE);
                        // MakeToast.show(AepsSettllementActivity.this, e.getMessage());

                    }


                    progressBar.setVisibility(View.GONE);
                },
                error -> {
                    //tv_error_hint.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    //MakeToast.show(AepsSettllementActivity.this, "onError");
                }) {

        };
        RequestHandler.getInstance(AepsSettllementActivity.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void showConfirmDialog(int i, String amount) {
        Dialog dialog = AppDialogs.transferConformDetail(AepsSettllementActivity.this);

        TextView tv_senderMobile = dialog.findViewById(R.id.tv_senderMobile);
        tv_senderMobile.setText(setBankList.get(i).getBeneName());
        TextView tv_beneName = dialog.findViewById(R.id.tv_beneName);
        tv_beneName.setText(setBankList.get(i).getBeneName());
        TextView tv_ifsc = dialog.findViewById(R.id.tv_ifsc);
        tv_ifsc.setText(setBankList.get(i).getIfsc());
        TextView tv_accountNo = dialog.findViewById(R.id.tv_accountNo);
        tv_accountNo.setText(setBankList.get(i).getAccountNumber());
        TextView tv_bank_name = dialog.findViewById(R.id.tv_bank_name);
        tv_bank_name.setText(setBankList.get(i).getBankName());
        LinearLayout lin_trans = dialog.findViewById(R.id.trans_lin);
        lin_trans.setVisibility(View.GONE);
        EditText ed_confirmAmount = dialog.findViewById(R.id.ed_confirmAmount);

        TextView tv_confirm = dialog.findViewById(R.id.tv_confirm);
        tv_confirm.setText("Transfer Amount");
        TextView tv_txnCharge = dialog.findViewById(R.id.tv_txnCharge);
        tv_txnCharge.setVisibility(View.GONE);
        TextView tv_totalAmount = dialog.findViewById(R.id.tv_totalAmount);
        tv_totalAmount.setVisibility(View.GONE);

        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_payAndConfirm = dialog.findViewById(R.id.btn_transfer);

        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        btn_payAndConfirm.setOnClickListener(view -> {

            // if(ed_confirmAmount.getText().toString().equalsIgnoreCase(amount)) {

            Log.e("TRANSFER_MONEY", APIs.TRANSFER_MONEY_A2ZWallet);
            if (ed_confirmAmount.getText().toString().length() > 0) {
                transferMoneyConfirm(i, ed_confirmAmount.getText().toString());
                dialog.dismiss();
            }
            //}//else MakeToast.show(AepsSettllementActivity.this, "Amount does not matched!");

        });

        dialog.show();

    }

    private void transferMoneyConfirm(int i, String amount) {
        progressBar.setVisibility(View.VISIBLE);

        Log.e("url aeps settl", "=" + APIs.AEPS_SETTLEMENT);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.AEPS_SETTLEMENT,
                response -> {
                    try {


                        Log.e("response transfer", "sd " + response.toString());
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");


                        if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(AepsSettllementActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(AepsSettllementActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {

                            Dialog dialog = AppDialogs.transactionStatus(AepsSettllementActivity.this, message, Integer.parseInt(status));
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                            });

                            dialog.setOnDismissListener(dialog1 -> {
                                Intent intent = new Intent(AepsSettllementActivity.this, HomePage_activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });
                            dialog.show();
                        }


                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);

                        // AppDialogs.volleyErrorDialog(AepsSettllementActivity.this, 1);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    progressBar.setVisibility(View.GONE);

                },
                error -> {
                    progressBar.setVisibility(View.GONE);

                    //AppDialogs.volleyErrorDialog(AepsSettllementActivity.this, 0);
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();

                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", userData.getToken());
                params.put("userId", String.valueOf(userData.getId()));
                params.put("id", setBankList.get(i).getId());
                params.put("beneName", setBankList.get(i).getBeneName());
                params.put("ifsc", setBankList.get(i).getIfsc());
                params.put("bank_account", setBankList.get(i).getAccountNumber());
                //params.put("mobile_number", ""+);

                params.put("amount", "" + amount);
                params.put("channel", "2");

                Log.e("params", params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(AepsSettllementActivity.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onBackPressed() {
        if (isTransferFragment) {// this thing is handling with fragment backstatck
            dmt_fragmentContainer.setVisibility(View.GONE);
            add_bank_menu.setVisible(true);
            getSupportActionBar().setTitle("AEPS Settlement");
            isTransferFragment = false;
            if (edit) {
                if (bankList.size() > 0)
                    bankList.clear();

                getBankACList();
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bank_add, menu);
        add_bank_menu = menu.findItem(R.id.add_bank);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_bank) {
            edit = false;
            setFragment(false, 0);
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void setFragment(boolean edit, int pos) {
        add_bank_menu.setVisible(false);
        dmt_fragmentContainer.setVisibility(View.VISIBLE);
        fragment = AddBankFragment.newInstance();
        getSupportActionBar().setTitle("Add AEPS Settlement Bank");


        Bundle bundle = new Bundle();
        if (edit) {
            getSupportActionBar().setTitle("Edit AEPS Settlement Bank");
            BankDetail obj = bankList.get(pos);
            bundle.putSerializable("obj", obj);
        }
        bundle.putBoolean("edit", edit);
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dmt_fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
        isTransferFragment = true;
    }

    @Override
    public void onFragmentInteraction() {
        dmt_fragmentContainer.setVisibility(View.GONE);
        add_bank_menu.setVisible(true);
        getBankACList();
    }
}
