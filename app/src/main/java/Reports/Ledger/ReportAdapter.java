package Reports.Ledger;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.enums.ReportTypes;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.model.Report;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static String REPORT_ID = "report_id", REPORT_API_ID = "report_api_id";
    // Some important intents are commented
    private final int ITEM = 0;
    private final int LOADING = 1;
    private int layout = R.layout.listview_transaction_history;
    private boolean isChecking = false, isChecking2 = false, isLoadingAdded = false;
    private ReportTypes reportType;
    private List<Report> reportList;
    private Context context;
    private String statusValue, rsn = "";

    public ReportAdapter(Context context, ReportTypes reportTypes) {
        this.context = context;
        reportList = new ArrayList<>();
        this.reportType = reportTypes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            // this will show the items easy
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;

            // this shows the progress bar in the recycler view
            // mainly used for pagination
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }

        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {

        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.listview_transaction_history,
                parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Report report = reportList.get(position);

        switch (getItemViewType(position)) {

            case ITEM:

                ViewHolder view = (ViewHolder) holder;

                view.tv_date.setText(report.getDate());
                view.tv_serviceName.setText(report.getOperator());
                view.tv_closingBalance.setText("Balance: ₹ " + report.getBalance());
                view.tv_amount.setText("₹ " + report.getAmount());
                view.tv_number.setText(report.getNumber());

                view.itemView.setOnClickListener(view1 -> {

                    startAnimation(view1);

                    TreeMap<String, String> treeMap = new TreeMap<>();
                    treeMap.put("Account Number", report.getBeneAccount());
                    treeMap.put("Amount", report.getAmount());
                    treeMap.put("Bank Name", report.getBankName());
                    treeMap.put("Beneficiary Name", report.getBeneName());
                    treeMap.put("Closing Balance", report.getBalance());
                    treeMap.put("Credit Amount", report.getCredtAmount());
                    treeMap.put("Credit/Debit", report.getCreditDebit());
                    treeMap.put("Date", report.getDate());
                    treeMap.put("Debit Amount", report.getDebitAmount());
                    treeMap.put("Description", report.getDescription());
                    treeMap.put("ID", report.getId());
                    treeMap.put("IFSC Code", report.getIfscCode());
                    treeMap.put("Opening Balance", report.getOpeningBalance());
                    treeMap.put("Operator", report.getOperator());
                    treeMap.put("Operator Id", report.getOperatorId());
                    treeMap.put("Remark", report.getRemark());
                    treeMap.put("Remitter Number", report.getMobileNumber());
                    treeMap.put("RR Number", report.getrRNo());
                    treeMap.put("R2R Transfer", report.getR2rTransfer());
                    treeMap.put("Service Tax", report.getServiceTax());
                    treeMap.put("Status", report.getStatus());
                    treeMap.put("TDS", report.getTds());
                    treeMap.put("TXN Type", report.getTxnType());

                    SheetClassReport sheetClass = new SheetClassReport(treeMap, report, context);
                    sheetClass.show(((FragmentActivity) context).getSupportFragmentManager(), "exampleBottomSheet");

                });

                if (report.getStatus().equalsIgnoreCase("success") ||
                        report.getStatus().equalsIgnoreCase("complete") ||
                        report.getStatus().equalsIgnoreCase("active") ||
                        report.getStatus().equalsIgnoreCase("successfully submitted") ||
                        report.getStatus().equalsIgnoreCase("credit")) {
                    view.tv_amount.setTextColor(context.getResources().getColor(R.color.success));
                } else if (
                        report.getStatus().equalsIgnoreCase("failure") ||
                                report.getStatus().equalsIgnoreCase("debit")) {
                    view.tv_amount.setTextColor(context.getResources().getColor(R.color.failed));
                } else {
                    view.tv_amount.setTextColor(context.getResources().getColor(R.color.warning));
                }


                /*view.ll_main_layout.setOnClickListener(view -> {
                    viewHideShow((ViewHolder) view, report);
                    // here show the sheet class but we need the data first here
                });

                view.btn_check.setOnClickListener(view -> {

                    if (!isChecking) {
                        isChecking = true;
                        checkImpsStatus((ViewHolder) view, view.getAdapterPosition());
                    } else {
                        MakeToast.show(context, "One item is already processing\nplease wait...");
                    }

                });

                if (reportList.get(view.getAdapterPosition()).isHide()) {
                    view.ll_arrow_hideable.setVisibility(View.VISIBLE);
                    view.ll_hideable.setVisibility(View.GONE);
                } else {
                    view.ll_arrow_hideable.setVisibility(View.GONE);
                    view.ll_hideable.setVisibility(View.VISIBLE);
                }

                if (report.getApiId().equalsIgnoreCase("25")) {
                    view.btn_check.setVisibility(View.GONE);
                } else {
                    if (report.getStatusId().equalsIgnoreCase("1")
                            || report.getStatusId().equalsIgnoreCase("3")
                            || report.getStatusId().equalsIgnoreCase("18")
                            || report.getStatusId().equalsIgnoreCase("34")
                    ) {
                        view.btn_check.setAlpha(1f);
                        view.btn_check.setEnabled(true);
                        view.btn_check.setVisibility(View.GONE);
                    } else {
                        view.btn_check.setAlpha(0.5f);
                        view.btn_check.setEnabled(false);
                        view.btn_check.setVisibility(View.GONE);
                    }
                }

                if (report.getApiId() != null) {
                    if (report.getApiId().equalsIgnoreCase("5")//a2z_wallet
                            || report.getApiId().equalsIgnoreCase("25")//a2z_wallet_plus
                            || report.getApiId().equalsIgnoreCase("4")//dmt1
                            || report.getApiId().equalsIgnoreCase("1")//electricity pay
                            || report.getApiId().equalsIgnoreCase("27")//BBPS1 Bill pay
                            || report.getApiId().equalsIgnoreCase("30")//BBPS1 Bill pay
                            || report.getApiId().equalsIgnoreCase("40")//BBPS2 Bill pay
                            || report.getApiId().equalsIgnoreCase("10")//AEPS
                            || report.getApiId().equalsIgnoreCase("28")//Aadhar pay
                            // ||report.getApiId().equalsIgnoreCase("13")//prepaid recharge
                            || report.getApiId().equalsIgnoreCase("16")//dmt2
                            || report.getApiId().equalsIgnoreCase("15")
                    ) {

                        if (report.getApiId().equalsIgnoreCase("10")//AEPS
                                || report.getApiId().equalsIgnoreCase("28")) {
                            if (report.getStatusId().equalsIgnoreCase("2")) {
                                view.btn_view_slip.setVisibility(View.GONE);
                            } else {
                                view.btn_view_slip.setVisibility(View.VISIBLE);

                                view.btn_view_slip.setOnClickListener(view -> {
                                    *//*Intent intent = new Intent(context, InvoiceSlipActivity.class);
                                    intent.putExtra(REPORT_ID, report.getId());
                                    intent.putExtra(REPORT_API_ID, Integer.parseInt(report.getApiId()));
                                    context.startActivity(intent);*//*

                                });
                            }
                        } else {
                            view.btn_view_slip.setVisibility(View.VISIBLE);

                            view.btn_view_slip.setOnClickListener(view -> {

                               *//* Intent intent = new Intent(context, InvoiceSlipActivity.class);
                                intent.putExtra(REPORT_ID, report.getId());
                                intent.putExtra(REPORT_API_ID, Integer.parseInt(report.getApiId()));
                                context.startActivity(intent);*//*

                            });
                        }

                    } else view.btn_view_slip.setVisibility(View.GONE);
                } else view.btn_view_slip.setVisibility(View.GONE);

                view.btn_action.setOnClickListener(view -> {
                    if (!isChecking2) {
                        isChecking2 = true;
                        updateTransactionStatus((ViewHolder) view, view.getAdapterPosition());
                    } else
                        MakeToast.show(context, "One item is already processing\nplease wait...");
                });

                if (report.getStatus().equalsIgnoreCase("Success")
                        || report.getStatus().equalsIgnoreCase("Pending")
                        || report.getStatusId().equalsIgnoreCase("Inprocess")) {
                    view.btn_action.setAlpha(1f);
                    view.btn_action.setEnabled(true);
                } else {
                    view.btn_action.setAlpha(0.5f);
                    view.btn_action.setEnabled(false);
                }*/
                break;


            case LOADING:
//                Do nothing, as this shows the progress bar only
                break;
        }
    }


   /*
   private void viewHideShow(@NonNull ReportAdapter.ViewHolder viewHolder, Report report) {
        if (report.isHide()) {
            viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
            viewHolder.ll_hideable.setVisibility(View.VISIBLE);
            reportList.get(viewHolder.getAdapterPosition()).setHide(false);
        } else {
            viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
            viewHolder.ll_hideable.setVisibility(View.GONE);
            reportList.get(viewHolder.getAdapterPosition()).setHide(true);
        }
    }
    */

    public void add(Report report) {
        reportList.add(report);
        notifyItemInserted(reportList.size() - 1);
    }

    public void addAll(List<Report> reportList) {
        for (Report report : reportList) {
            add(report);
        }
    }

    public void remove(Report report) {
        int position = reportList.indexOf(report);
        if (position > -1) {
            reportList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        reportList.add(new Report());
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {

        isLoadingAdded = false;

        int position = reportList.size() - 1;
        Report item = null;
        if (position > 0)
            item = getItem(position);

        if (item != null) {
            reportList.remove(position);
            notifyItemRemoved(position);
        }

    }

    public Report getItem(int position) {
        return reportList.get(position);
    }

    private void checkImpsStatus(ViewHolder viewHolder, int position) {

        String url = APIs.CHECK_IMPS_STATUS + "?" + APIs.USER_TAG + "=" +
                SharedPref.getInstance(context).getId()
                + "&" + APIs.TOKEN_TAG + "=" + SharedPref.getInstance(context).getToken()
                + "&id=" + reportList.get(position).getId();

        // Log.d("CheckStatusTesting","check status URL : "+url);

        //viewHolder.btn_check.setText("checking..");
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    //      Log.d("CheckStatusTesting","onResponse : "+ response);
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("msg");
                        if (status.equalsIgnoreCase("1")) {

                            Dialog dialog = AppDialogs.checkStatus(context);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            TextView tv_message = dialog.findViewById(R.id.tv_message);
                            tv_message.setText(message);
                            if (message.equalsIgnoreCase("SUCCESS")) {
                                tv_message.setTextColor(context.getResources().getColor(R.color.success));
                            } else
                                tv_message.setTextColor(context.getResources().getColor(R.color.warning));
                            btn_ok.setOnClickListener(view -> dialog.dismiss());
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
                        } else {
                            Dialog dialog = AppDialogs.checkStatus(context);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            TextView tv_message = dialog.findViewById(R.id.tv_message);
                            tv_message.setTextColor(context.getResources().getColor(R.color.text_color_3));
                            tv_message.setText(message);
                            btn_ok.setOnClickListener(view -> dialog.dismiss());
                            dialog.show();
                        }


                    } catch (JSONException e) {
                        // Log.d("CheckStatusTesting","onException : "+e.getMessage());
                    }
                    // viewHolder.btn_check.setText("CHECK");
                    isChecking = false;
                },
                error -> {
                    // Log.d("CheckStatusTesting","onError : "+error.getMessage());
                    isChecking = false;
                    //viewHolder.btn_check.setText("CHECK");
                }) {

        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void updateTransactionStatus(ViewHolder viewHolder, int position) {

        //viewHolder.btn_action.setText("Loading...");
        final StringRequest request = new StringRequest(Request.Method.GET,
                APIs.GET_TRANSACTION_DETAILS + "?" + APIs.USER_TAG + "=" +
                        SharedPref.getInstance(context).getId()
                        + "&" + APIs.TOKEN_TAG + "=" + SharedPref.getInstance(context).getToken()
                        + "&id=" + reportList.get(position).getId(),
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {

                            JSONObject results = jsonObject.getJSONObject("results");


                            Dialog dialog = AppDialogs.updateTransaction(context);

                            RelativeLayout rl_actionButton = dialog.findViewById(R.id.rl_action_button);
                            LinearLayout ll_processing = dialog.findViewById(R.id.ll_processing);

                            TextView tv_recordNumber = dialog.findViewById(R.id.tv_recordNumber);
                            TextView tv_number = dialog.findViewById(R.id.tv_number);
                            TextView tv_customerMobNumber = dialog.findViewById(R.id.tv_customerMobNumber);
                            TextView tv_txnAmount = dialog.findViewById(R.id.tv_txnAmount);
                            TextView tv_transaction_id = dialog.findViewById(R.id.tv_transaction_id);
                            TextView tv_providerName = dialog.findViewById(R.id.tv_providerName);
                            TextView tv_serviceName = dialog.findViewById(R.id.tv_serviceName);
                            TextView tv_userName = dialog.findViewById(R.id.tv_userName);
                            EditText ed_rrNumber = dialog.findViewById(R.id.ed_rrNumber);
                            Spinner spn_status = dialog.findViewById(R.id.spn_status);
                            Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                            Button btn_confirm = dialog.findViewById(R.id.btn_confirm);
                            getStatusFromSpinner(spn_status);

                            btn_cancel.setOnClickListener(view -> dialog.dismiss());

                            String number = results.getString("number");
                            String id = results.getString("id");
                            String txnId = results.getString("txnId");

                            tv_number.setText(number);
                            tv_txnAmount.setText(results.getString("amount"));
                            tv_transaction_id.setText(txnId);
                            tv_customerMobNumber.setText(results.getString("customerNumber"));
                            tv_recordNumber.setText(id);
                            tv_providerName.setText(results.getString("providerName"));
                            tv_serviceName.setText(results.getString("serviceName"));
                            tv_userName.setText(results.getString("userName"));

                            btn_confirm.setOnClickListener(view -> {

                                String rrNumber = ed_rrNumber.getText().toString();
                                update(id, number, txnId, rrNumber, dialog, rl_actionButton, ll_processing, viewHolder);

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
                        } else {
                            Dialog dialog = AppDialogs.checkStatus(context);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            TextView tv_message = dialog.findViewById(R.id.tv_message);
                            tv_message.setTextColor(context.getResources().getColor(R.color.text_color_3));
                            tv_message.setText(message);
                            btn_ok.setOnClickListener(view -> dialog.dismiss());
                            dialog.show();
                        }


                    } catch (JSONException e) {
                    }
                    //viewHolder.btn_action.setText("action");
                    isChecking2 = false;
                },
                error -> {

                    isChecking2 = false;
                    //viewHolder.btn_action.setText("action");
                }) {

        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void update(String id, String number, String txnId, String rrNumber, Dialog dialog,
                        RelativeLayout rl_actionButton, LinearLayout ll_processing, ViewHolder viewHolder) {

        rl_actionButton.setVisibility(View.GONE);
        ll_processing.setVisibility(View.VISIBLE);

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.UPDATE_TRANSACTION,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {

                            //{"status":1,"message":"Amount Refunded Successful"}
                            dialog.dismiss();
                            Dialog dialog1 = AppDialogs.transactionStatus(context, message, 1);
                            Button btn_ok = dialog1.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> dialog1.dismiss());
                            dialog1.show();

                           /* if (statusValue.equalsIgnoreCase("1")) {

                                viewHolder.tv_status.setText("Success");
                                viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.success));

                            } else if (statusValue.equalsIgnoreCase("2")) {
                                viewHolder.tv_status.setText("Refund-Suc");
                                viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.warning));

                                viewHolder.btn_check.setAlpha(0.5f);
                                viewHolder.btn_check.setEnabled(false);
                                viewHolder.btn_action.setAlpha(0.5f);
                                viewHolder.btn_action.setEnabled(false);
                                reportList.get(viewHolder.getAdapterPosition()).setStatusId("2");
                            } else if (statusValue.equalsIgnoreCase("3")) {
                                viewHolder.tv_status.setText("Pending");
                                viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.warning));
                            }
*/

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
                            MakeToast.show(context, message);
                        }

                        rl_actionButton.setVisibility(View.VISIBLE);
                        ll_processing.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        rl_actionButton.setVisibility(View.VISIBLE);
                        ll_processing.setVisibility(View.GONE);
                        MakeToast.show(context, e.getMessage());
                    }
                },
                error -> {
                    rl_actionButton.setVisibility(View.VISIBLE);
                    ll_processing.setVisibility(View.GONE);
                    MakeToast.show(context, "onError");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("userId", String.valueOf(SharedPref.getInstance(context).getId()));
                param.put("token", String.valueOf(SharedPref.getInstance(context).getToken()));
                param.put("id", id);
                param.put("number", number);
                param.put("txnid", txnId);
                param.put("status", statusValue);
                return param;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void getStatusFromSpinner(Spinner spn_status) {

        HashMap<String, String> values = new HashMap<>();
        values.put("Success", "1");
        values.put("Failure", "2");
        values.put("Pending", "3");

        String[] prepaidStrings = values.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_status.setAdapter(dataAdapter);

        spn_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusValue = values.get(spn_status.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tv_date;
        private TextView tv_closingBalance;
        private TextView tv_serviceName;
        private TextView tv_amount;
        private TextView tv_number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_date = itemView.findViewById(R.id.tv_date);
            tv_number = itemView.findViewById(R.id.tv_number);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_closingBalance = itemView.findViewById(R.id.tv_closingBalance);
            tv_serviceName = itemView.findViewById(R.id.tv_serviceName);

        }
    }

    @Override
    public int getItemCount() {
        return reportList == null ? 0 : reportList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == reportList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void startAnimation(View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(500);

        AnimationSet animation = new AnimationSet(false); //change to false
        //animation.addAnimation(fadeOut);
        animation.addAnimation(fadeIn);
        view.startAnimation(animation);
    }

    public class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
}

