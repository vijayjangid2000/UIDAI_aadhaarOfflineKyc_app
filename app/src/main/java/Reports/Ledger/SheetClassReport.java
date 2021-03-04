package Reports.Ledger;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
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
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class SheetClassReport extends BottomSheetDialogFragment {

    ArrayList<Data> al_data;
    SheetClassReport sheetClass;
    Report report;
    View view;
    Context context;
    private int layout = R.layout.layout_transaction_summary;

    // constructor so that we can send data here
    public SheetClassReport(TreeMap<String, String> hashMap, Report report, Context context) {

        this.context = context;

        this.report = report;

        al_data = new ArrayList<>();
        hashMap.forEach((k, v) -> {
            al_data.add(new Data(k, v));
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_transaction_summary, container, false);

        final ImageView ivb_close, ivb_share, ivb_copy;
        TextView tvb_complain;
        RecyclerView rv_summary;

        // ivb -> imageViewButton, tvb -> textViewButton
        ivb_close = view.findViewById(R.id.ivb_ClosetranSumLayt);
        ivb_share = view.findViewById(R.id.ivb_SharetranSumLayt);
        ivb_copy = view.findViewById(R.id.ivb_CopytranSumLayt);
        tvb_complain = view.findViewById(R.id.tvb_ComplaintranSumLayt0);
        rv_summary = view.findViewById(R.id.rv_summary);

        ivb_close.setOnClickListener(v -> {
            sheetClass = SheetClassReport.this;
            sheetClass.dismiss();
        });

        ivb_share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "OILab Complain");

            StringBuilder sb = new StringBuilder("");
            for (Data al_datum : al_data) {
                sb.append(al_datum.getName());
                sb.append(" : ");
                sb.append(al_datum.getValue());
                sb.append("\n");
            }

            shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(shareIntent, "Share via "));
        });

        ivb_copy.setOnClickListener(v -> {

            Toast.makeText(getContext(), "Transaction details copied to clipboard", Toast.LENGTH_SHORT).show();
            StringBuilder sb = new StringBuilder("");

            for (Data al_datum : al_data) {
                sb.append(al_datum.getName());
                sb.append(" : ");
                sb.append(al_datum.getValue());
                sb.append("\n");
            }

            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Transaction Details", sb.toString());
            clipboard.setPrimaryClip(clip);


        });

        tvb_complain.setOnClickListener(v -> {

            ivb_close.performClick();
            final String[] rsn = {""};

            final Dialog[] dialog = {AppDialogs.complain(context)};
            Spinner spinner = dialog[0].findViewById(R.id.spn_searchType);
            EditText remark = dialog[0].findViewById(R.id.ed_remark);
            Button submit = dialog[0].findViewById(R.id.btn_submit);
            ImageButton btn_cancel = dialog[0].findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(view3 -> dialog[0].dismiss());

            String[] prepaidStrings = {"Amount Not Credit", "Recharge Not Credit", "Pending Txn", "Others"};
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(context),
                    R.layout.spinner_layout, prepaidStrings);
            dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
            spinner.setAdapter(dataAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    rsn[0] = prepaidStrings[position];

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            submit.setOnClickListener(view -> {
                submit.setText("Loading...");
                submitComplain(rsn[0], remark.getText().toString(), report.getId(), dialog[0]);
            });

            dialog[0].show();

        });

        rv_summary.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_summary.setAdapter(new MyListAdapter(al_data));

        return view;
    }


    private void submitComplain(String rsn, String remark, String txnid, Dialog dialog) {

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.COMPLAIN,
                response -> {
                    try {
                        Button button = dialog.findViewById(R.id.btn_submit);
                        button.setText("Submit");
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
                            MakeToast.showCenter(context, message);
                        }


                    } catch (JSONException e) {

                        MakeToast.showCenter(context, e.getMessage());
                    }
                },
                error -> {

                    MakeToast.showCenter(context, "onError");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("userId", String.valueOf(SharedPref.getInstance(context).getId()));
                param.put("token", String.valueOf(SharedPref.getInstance(context).getToken()));
                param.put("complainTxnId", "" + txnid);
                param.put("issueType", rsn);
                param.put("complainRemark", remark);
                return param;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    // adapter for the items for the transaction
    public static class MyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<Data> al_data;

        public MyListAdapter(ArrayList<Data> al_data) {
            this.al_data = al_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.listview_transaction_data, parent, false);
            return new MyListAdapter.ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.tvRow1.setText(al_data.get(position).name);
            viewHolder.tvRow2.setText(al_data.get(position).value);
        }

        @Override
        public int getItemCount() {
            return al_data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvRow1, tvRow2;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvRow1 = itemView.findViewById(R.id.tv_row1);
                tvRow2 = itemView.findViewById(R.id.tv_row2);
            }

        }
    }

    public class Data {
        String name, value;

        public Data(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }


}