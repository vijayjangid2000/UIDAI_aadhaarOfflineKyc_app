package com.vijayjangid.aadharkyc.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.model.BankDetail;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyBankListAdapter extends RecyclerView.Adapter<MyBankListAdapter.ViewHolder> {

    private ArrayList<BankDetail> list;
    private Context context;
    private boolean isChecking = false;
    private OnMyBankListAdapterClickListener listener;

    public MyBankListAdapter(ArrayList<BankDetail> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_my_banks, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        BankDetail bankDetail = list.get(i);
        viewHolder.tv_name.setText(bankDetail.getBeneName());
        viewHolder.tv_bank_name.setText(bankDetail.getBankName());
        viewHolder.tv_branch.setText(bankDetail.getBranchName());
        viewHolder.tv_accountNo.setText(bankDetail.getAccountNumber());

        viewHolder.tv_ifsc.setText(bankDetail.getIfsc());

        if (bankDetail.getStatus().equalsIgnoreCase("1"))
            viewHolder.status.setText("Success");
        else if (bankDetail.getStatus().equalsIgnoreCase("29"))
            viewHolder.status.setText("Rejected");
        else if (bankDetail.getStatus().equalsIgnoreCase("3")) {
            viewHolder.edit.setVisibility(View.VISIBLE);
            viewHolder.status.setText("Pending");
        }

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.edit(i);
            }
        });

        viewHolder.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isChecking) {
                    isChecking = true;
                    verifyBankAccount(viewHolder, i);
                }
            }
        });

    }

    private void verifyBankAccount(MyBankListAdapter.ViewHolder viewHolder, int position) {

        viewHolder.verify.setText("Please wait...");
        Log.e("BANK_ACCOUNT_INFO_DMT", "=" + APIs.BANK_ACCOUNT_INFO_DMT);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.BANK_ACCOUNT_INFO_DMT,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("jsonObject response", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            viewHolder.verify.setText("Verified");
                            String beneName = jsonObject.getString("beneName");
                            viewHolder.tv_name.setText(beneName);
                            updateBank(beneName, position);

                            Log.e("beneName", "=" + beneName);
                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        } else {
                            viewHolder.verify.setText("Verify");
                            MakeToast.show(context, message);
                            Dialog dialog = AppDialogs.transactionStatus(context, message, 2);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                            });
                            dialog.show();
                        }

                        isChecking = false;
                        // viewHolder.btn_verify.setText("Verified");
                    } catch (JSONException e) {
                        isChecking = false;
                        //  viewHolder.btn_verify.setText("Verified");
                    }
                },
                error -> {
                    isChecking = false;

                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(context).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(context).getId()));
                params.put("mobile_number", String.valueOf(SharedPref.getInstance(context).getMobile()));
                params.put("bank_account", list.get(position).getAccountNumber());
                params.put("ifsc", list.get(position).getIfsc());
                params.put("bankCode", list.get(position).getBankName());
                Log.e("verfiy", "=" + params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void updateBank(String name, int pos) {


        String url = "";


        url = APIs.UPDATE_BANK_DETAILS;


        Log.e("addBank", "" + url);
        final StringRequest request = new StringRequest(Request.Method.POST,
                url,
                response -> {
                    try {


                        Log.e("response post", "=" + response.toString());
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("success")) {
                            Dialog dialog = AppDialogs.transactionStatus(context, name, 1);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                            });
                            dialog.show();
                        } else if (status.equalsIgnoreCase("200")) {

                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {

                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        } else
                            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
                        ;
                    } catch (JSONException e) {

                    }
                },
                error -> {

                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(context).getToken());

                params.put("name", name);
                params.put("ifsc", list.get(pos).getIfsc());
                params.put("bank_name", list.get(pos).getBankName());
                params.put("branch_name", list.get(pos).getBranchName());
                params.put("account_number", list.get(pos).getAccountNumber());

                params.put("user_id", String.valueOf(SharedPref.getInstance(context).getId()));
                params.put("id", list.get(pos).getId());

                Log.e("addBank post", "=" + params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setupListener(OnMyBankListAdapterClickListener listener) {
        this.listener = listener;
    }

    public interface OnMyBankListAdapterClickListener {
        void edit(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_bank_name;
        TextView tv_ifsc;
        TextView tv_accountNo;
        TextView tv_branch;
        Button status;
        Button verify;
        Button edit;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_beneName);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_ifsc = itemView.findViewById(R.id.tv_ifsc);
            tv_accountNo = itemView.findViewById(R.id.tv_accountNo);
            tv_branch = itemView.findViewById(R.id.tv_branch_code);

            status = itemView.findViewById(R.id.btn_status);
            verify = itemView.findViewById(R.id.btn_verify);
            edit = itemView.findViewById(R.id.btn_edit);


        }
    }

}
