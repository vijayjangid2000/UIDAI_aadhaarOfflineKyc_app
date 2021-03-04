package a2z_wallet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.listener.OnBeneficiaryAdapterClickListener;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BeneficiaryAdapter extends
        RecyclerView.Adapter<BeneficiaryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Beneficiary> beneficiaryList;

    public BeneficiaryAdapter(Context context, ArrayList<Beneficiary> beneficiaryList) {
        this.context = context;
        this.beneficiaryList = beneficiaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_beneficiary, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Beneficiary beneficiary = beneficiaryList.get(i);
        viewHolder.tv_bank_name.setText(beneficiary.getBank());
        viewHolder.tv_bank_number.setText(beneficiary.getAccount());
        viewHolder.tv_name.setText(beneficiary.getName());
        viewHolder.tv_ifsce_code.setText(beneficiary.getIfsc());

        //listeners
        viewHolder.btn_transfer.setOnClickListener(view -> {
            listener.transfer(i);
        });
        viewHolder.btn_delete.setOnClickListener(view -> {
            listener.delete(i);
        });

        if (beneficiary.getIs_verified().equalsIgnoreCase("1")) {
            viewHolder.btn_verify.setText("Verified");
            viewHolder.iv_veri.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_veri.setVisibility(View.GONE);
        }
        viewHolder.btn_verify.setOnClickListener(view -> {
            if (!isChecking) {
                isChecking = true;
                verifyBankAccount(viewHolder, i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return beneficiaryList.size();
    }

    private OnBeneficiaryAdapterClickListener listener;
    private boolean isChecking = false;

    public void setupListener(OnBeneficiaryAdapterClickListener listener) {
        this.listener = listener;
    }

    public void updateList(ArrayList<Beneficiary> list) {
        beneficiaryList = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_bank_name;
        TextView tv_bank_number;
        TextView tv_ifsce_code;
        Button btn_delete;
        Button btn_verify;
        Button btn_transfer;
        ImageView iv_veri;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_bank_number = itemView.findViewById(R.id.tv_bank_number);
            tv_ifsce_code = itemView.findViewById(R.id.tv_ifse_code);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            btn_verify = itemView.findViewById(R.id.btn_verify);
            btn_transfer = itemView.findViewById(R.id.btn_transfer);
            iv_veri = itemView.findViewById(R.id.iv_veri);
        }
    }

    private void verifyBankAccount(ViewHolder viewHolder, int position) {

        viewHolder.btn_verify.setText("Please wait...");
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
                            viewHolder.btn_verify.setText("Verified");
                            String beneName = jsonObject.getString("beneName");
                            viewHolder.tv_name.setText(beneName);
                            Dialog dialog = AppDialogs.transactionStatus(context, beneName, 1);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                            });
                            dialog.show();
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
                        } else if (status.equalsIgnoreCase("pending")) {
                            if (jsonObject.getString("type").equalsIgnoreCase("2")) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something
                                        try {
                                            checkStatus(viewHolder, position, jsonObject.getString("txnId"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 10000);

                                return;
                            } else {
                                viewHolder.btn_verify.setText("Verify");
                                MakeToast.show(context, message);
                                Dialog dialog = AppDialogs.transactionStatus(context, message, 2);
                                Button btn_ok = dialog.findViewById(R.id.btn_ok);
                                btn_ok.setOnClickListener(view -> {
                                    dialog.dismiss();
                                });
                                dialog.show();
                            }
                        } else {
                            viewHolder.btn_verify.setText("Verify");
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
                params.put("mobile_number", beneficiaryList.get(position).getMobile());
                params.put("bank_account", beneficiaryList.get(position).getAccount());
                params.put("ifsc", beneficiaryList.get(position).getIfsc());
                params.put("bankCode", beneficiaryList.get(position).getBank());
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

    private void checkStatus(ViewHolder viewHolder, int position, String id) {

        viewHolder.btn_verify.setText("Please wait...");
        Log.e("CHECK_STATUS_VERI", "=" + APIs.CHECK_STATUS_VERI);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.CHECK_STATUS_VERI,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("jsonObject response", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("msg");

                        if (status.equalsIgnoreCase("1")) {
                            viewHolder.btn_verify.setText("Verified");
                            String beneName = jsonObject.getString("beneName");
                            viewHolder.tv_name.setText(beneName);
                            Dialog dialog = AppDialogs.transactionStatus(context, beneName, 1);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                            });
                            dialog.show();
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
                            viewHolder.btn_verify.setText("Verify");
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
                params.put("id", id);

                Log.e("check status", "=" + params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


}
