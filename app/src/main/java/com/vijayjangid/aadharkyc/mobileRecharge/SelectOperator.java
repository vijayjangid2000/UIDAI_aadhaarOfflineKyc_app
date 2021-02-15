package com.vijayjangid.aadharkyc.mobileRecharge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.enums.ProviderType;
import com.vijayjangid.aadharkyc.in.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static maes.tech.intentanim.CustomIntent.customType;

// this is for animation

public class SelectOperator extends AppCompatActivity {

    final int REQUEST_CODE_OPERATOR = 99;
    final String INTENT_PUT_EXTRA = "OperatorModelObject";
    RecyclerView recyclerView;
    AlertDialog dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_select_operator_recharge);

        showProgressBar(true, "Searching operators\nPlease wait");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Operator");
        //toolbar.setNavigationIcon(R.drawable.ic_cross_close);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.rv_operator);
        sendOperatorListRequest();
    }

    public void sendOperatorListRequest() {

        /* HERE WE CHECK IF REQUEST COMES FROM POSTPAID ACTIVITY*/
        String provideType;
        boolean isPostPaid = getIntent().getBooleanExtra("PostPaid", false);
        if (isPostPaid) provideType = ProviderType.POSTPAID.toString();
        else provideType = ProviderType.MOBILE_PREPAID.toString();

        UserData userData = new UserData(this);

        final StringRequest request = new StringRequest(Request.Method.GET,
                "https://prod.excelonestopsolution.com/mobileapp/api/get-recharge-provider"
                        + "?" + "userId" + "=" +
                        userData.getId()
                        + "&" + "token" + "=" + userData.getToken()
                        + "&state_id=" + ""
                        + "&requestType=" + provideType,

                response -> {
                    try {

                        dialogView.cancel();

                        Log.e("response provider", "=" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");


                        if (status == 1) {

                            String baseUrl = jsonObject.getString("baseUrl");
                            String serviceId = jsonObject.getString("serviceId");

                            ArrayList<OperatorModel> operatorList = new ArrayList<>();
                            JSONArray array = jsonObject.getJSONArray("provider");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject providerObject = array.getJSONObject(i);

                                OperatorModel operatorModel = new OperatorModel();
                                operatorModel.setId(providerObject.getString("id"));
                                operatorModel.setProviderName(providerObject.getString("provider_name"));
                                operatorModel.setProviderImage(baseUrl + providerObject.getString("provider_image"));
                                operatorModel.setServiceId(serviceId);
                                operatorModel.setDealer(providerObject.getString("dealer_name"));
                                operatorModel.setStateAvailable(false);
                                operatorList.add(operatorModel);
                            }

                            /*here have created the list of the companies
                             * now we will show them in the recycler view
                             * other error will be in the catch and the volley errors,
                             * in status also*/

                            onOperatorModelCreated(operatorList);

                        } else if (status == 200 || status == 300) {
                            String message = jsonObject.getString("message");
                            showCustomSnack(recyclerView.getRootView(), "Error: " + message, true, true);
                        }

                    } catch (JSONException e) {
                        dialogView.cancel();
                        showCustomSnack(recyclerView.getRootView(), "Error: " + e.getMessage(), true, true);
                    }
                },

                error -> {
                    showCustomSnack(recyclerView.getRootView(), "Error: " + error.getMessage(), true, true);
                    dialogView.cancel();
                }) {
        };

        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    void onOperatorModelCreated(ArrayList<OperatorModel> operatorModelArrayList) {
        // now we will create the adapter for the recycler as we received the data
        dialogView.cancel();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CustomAdapter(operatorModelArrayList, this));
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SelectOperator.this, "bottom-to-up");
    }

    public void showCustomSnack(View view, String message,
                                boolean isIndefinite, boolean isError) {


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

    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.layout_progressbar, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        android.app.AlertDialog.Builder alertBldr_loading = new android.app.AlertDialog.Builder(this)
                .setCancelable(false);
        dialogView = alertBldr_loading.create();
        dialogView.setView(view);
        Window window = dialogView.getWindow();
        if (window != null) window.setBackgroundDrawableResource(R.color.Transparent);
        dialogView.show();
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        // al represents arrayList here :)
        ArrayList<OperatorModel> al_operatorModel;
        Context context;

        public CustomAdapter(ArrayList<OperatorModel> al_operatorModel, Context context) {
            this.al_operatorModel = al_operatorModel;
            this.context = context;
        }

        @NonNull
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.listview_operator_alert, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

            OperatorModel object = al_operatorModel.get(position);
            holder.tv_operatorCompanyName.setText(object.getProviderName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setAlpha(0.5f);
                    Intent intent = new Intent();
                    intent.putExtra(INTENT_PUT_EXTRA, new Gson().toJson(al_operatorModel.get(position)));
                    setResult(REQUEST_CODE_OPERATOR, intent);
                    customType(SelectOperator.this, "bottom-to-up");
                    finish();
                }
            });

            // we are showing a simple listView here for states
            if (!object.isStateAvailable()) return; // only if available

            holder.itemView.setOnClickListener(null);
            holder.tv_operatorCompanyName.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_down_arrow, 0, 0, 0); // adding icon

            // set the arrayList for sublist here
            final ArrayList<String> arrayList = new ArrayList<>();
            final ArrayAdapter adapter = new ArrayAdapter(SelectOperator.this,
                    android.R.layout.simple_list_item_1, arrayList.toArray());

            holder.subListView.setVisibility(View.GONE);
            holder.subListView.setBackgroundColor(Color.parseColor("#EDEBEB"));
            holder.tv_operatorCompanyName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.subListView.getVisibility() == View.GONE) {
                        holder.tv_operatorCompanyName.setCompoundDrawablesWithIntrinsicBounds
                                (R.drawable.ic_cross_close, 0, 0, 0);
                        holder.subListView.setVisibility(View.VISIBLE);
                        holder.subListView.setAdapter(adapter);

                        int numberOfItems = holder.subListView.getAdapter().getCount();

                        // for setting the list height equal to the number of objects in it
                        int totalItemsHeight = 0;
                        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                            View item = holder.subListView.getAdapter().
                                    getView(itemPos, null, holder.subListView);
                            item.measure(0, 0);
                            totalItemsHeight += item.getMeasuredHeight();
                        }

                        int totalDividersHeight = holder.subListView.getDividerHeight() *
                                (numberOfItems - 1);

                        ViewGroup.LayoutParams params = holder.subListView.getLayoutParams();
                        params.height = totalItemsHeight + totalDividersHeight;
                        holder.subListView.setLayoutParams(params);
                        holder.subListView.requestLayout();

                    } else if (holder.subListView.getVisibility() == View.VISIBLE) {
                        holder.subListView.setVisibility(View.GONE);
                        holder.tv_operatorCompanyName.setCompoundDrawablesWithIntrinsicBounds
                                (R.drawable.ic_down_arrow, 0, 0, 0);
                    }
                }
            });

            holder.subListView.setOnItemClickListener((parent, view, position1, id) -> {
                view.setAlpha(0.4f);
                Intent intent = new Intent();
                intent.putExtra(INTENT_PUT_EXTRA, new Gson().toJson(al_operatorModel.get(position)));
                setResult(REQUEST_CODE_OPERATOR, intent);
                customType(SelectOperator.this, "bottom-to-up");
                finish();
            });

        }

        @Override
        public int getItemCount() {
            return al_operatorModel.size();
        }

        // HERE is the viewHolder Class
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_operatorCompanyName;
            ListView subListView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_operatorCompanyName = itemView.findViewById(R.id.operator_textview);
                subListView = findViewById(R.id.subListview_operators);
            }
        }
    }
}