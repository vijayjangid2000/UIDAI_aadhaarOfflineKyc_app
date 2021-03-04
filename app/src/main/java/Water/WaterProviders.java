package Water;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.enums.ProviderType;
import com.vijayjangid.aadharkyc.in.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import MobRecharge.OperatorModel;

import static maes.tech.intentanim.CustomIntent.customType;

public class WaterProviders extends AppCompatActivity {

    final String INTENT_PUT_EXTRA = "Provider";
    final int REQUEST_CODE_OPERATOR = 100;
    RecyclerView recyclerView;
    AlertDialog dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_providers);

        showProgressBar(true, "Searching operators\nPlease wait");


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Service Board");
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
        provideType = ProviderType.WATER.toString();

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
                                operatorList.add(operatorModel);

                                /*here have created the list of the companies
                                 * now we will show them in the recycler view
                                 * other error will be in the catch and the volley errors,
                                 * in status also*/
                            }

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

    public void onBackPressed() {
        super.onBackPressed();
        customType(WaterProviders.this, "bottom-to-up");
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
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

    public void showCustomSnack(View view, String message,
                                boolean isIndefinite, boolean isError) {


        Snackbar snackbar = Snackbar.make(view.getRootView(), message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getColor(R.color.colorPrimary));
        if (isIndefinite) snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        if (isError) snackBarView.setBackgroundColor(getColor(R.color.Red));

        snackBarView.setOnClickListener(view1 -> snackbar.dismiss());
        snackbar.show();
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
            View view = layoutInflater.inflate(R.layout.listview_image_text_rv, parent, false);
            return new CustomAdapter.ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

            OperatorModel object = al_operatorModel.get(position);
            holder.tv_title.setText(object.getProviderName());


            Picasso.get()
                    .load(al_operatorModel.get(position).getProviderImage())
                    .resize(50, 50)
                    .centerCrop()
                    .placeholder(R.drawable.icon_no_image)
                    .error(R.drawable.icon_no_image)
                    .into(holder.iv_image);


            //holder.iv_image.setImageBitmap(object.getProviderImage());

            holder.itemView.setOnClickListener(view -> {
                view.setAlpha(0.5f);
                Intent intent = new Intent();
                intent.putExtra(INTENT_PUT_EXTRA, new Gson().toJson(al_operatorModel.get(position)));
                setResult(REQUEST_CODE_OPERATOR, intent);
                customType(WaterProviders.this, "bottom-to-up");
                finish();
            });

        }

        @Override
        public int getItemCount() {
            return al_operatorModel.size();
        }

        // HERE is the viewHolder Class
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_title;
            ImageView iv_image;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_title = itemView.findViewById(R.id.tv_commonView);
                iv_image = itemView.findViewById(R.id.iv_commonView);
            }
        }
    }
}