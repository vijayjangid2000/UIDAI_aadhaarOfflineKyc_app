package com.vijayjangid.aadharkyc.mobileRecharge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.adapter.ProviderAdapter;
import com.vijayjangid.aadharkyc.enums.ProviderType;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.model.Provider;
import com.vijayjangid.aadharkyc.util.MakeToast;
import com.vijayjangid.aadharkyc.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static maes.tech.intentanim.CustomIntent.customType;

public class ProviderActivity extends AppCompatActivity {

    public static final String PROVIDER_TYPE = "provider_type";
    public static final String PROVIDER = "provider";
    UserData userData;
    SessionManager sessionManager;
    private RecyclerView recyclerView;
    private ProviderAdapter adapter;
    private ProviderType providerType;
    private ProgressBar progressBar;
    private LinearLayout ll_something_went_wrong;
    private Spinner spn_electricity;
    private LinearLayout ll_electricity;
    private HashMap<String, String> stateListsHashMap = new HashMap<>();
    private String state_id = "";
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        sessionManager = new SessionManager(ProviderActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Provider");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        userData = new UserData(this);

        providerType = (ProviderType) getIntent().getSerializableExtra(PROVIDER_TYPE);

        ImageButton btn_refresh = findViewById(R.id.btn_refresh);
        ll_something_went_wrong = findViewById(R.id.ll_something);
        progressBar = findViewById(R.id.progressBar);
        spn_electricity = findViewById(R.id.spn_electricity);
        ll_electricity = findViewById(R.id.ll_electricity);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        getProviders();

        btn_refresh.setOnClickListener(view -> {
            getProviders();
        });

    }

    private void getProviders() {

        String requestType = "MOBILE_PREPAID";
        if (providerType == ProviderType.MOBILE_PREPAID)
            requestType = "MOBILE_PREPAID";
        else if (providerType == ProviderType.DTH)
            requestType = "DTH";
        else if (providerType == ProviderType.DATA_CARD) {
            requestType = "DATA_CARD";
        } else if (providerType == ProviderType.POSTPAID) {
            requestType = "POSTPAID";
        } else if (providerType == ProviderType.BROADBAND) {
            requestType = "BROADBAND";
        } else if (providerType == ProviderType.LANDLINE) {
            requestType = "LANDLINE";
        } else if (providerType == ProviderType.WATER) {
            requestType = "WATER";
        } else if (providerType == ProviderType.GAS) {
            requestType = "GAS";
        } else if (providerType == ProviderType.INSURANCE) {
            requestType = "INSURANCE";
        } else if (providerType == ProviderType.ELECTRICITY) {
            requestType = "ELECTRICITY";
        } else if (providerType == ProviderType.FASTTAG) {
            requestType = "FASTTAG";
        } else if (providerType == ProviderType.LOAN_REPAYMENT) {
            requestType = "LOAN_REPAYMENT";
        }
        progressBar.setVisibility(View.VISIBLE);
        ll_something_went_wrong.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        /*final StringRequest request = new StringRequest(Request.Method.GET,
                APIs.GET_RECHARGE_PROVIDER
                        + "?" + APIs.USER_TAG + "=" +
                        SharedPref.getInstance(this).getId()
                        + "&" + APIs.TOKEN_TAG + "=" + SharedPref.getInstance(this).getToken()
                        + "&state_id=" + state_id
                        + "&requestType=" + requestType,*/

        final StringRequest request = new StringRequest(Request.Method.GET,
                "https://prod.excelonestopsolution.com/mobileapp/api/" + "get-recharge-provider"
                        + "?" + "userId" + "=" +
                        userData.getId()
                        + "&" + "token" + "=" + userData.getToken()
                        + "&state_id=" + userData.getStateId()
                        + "&requestType=" + requestType,
                response -> {
                    try {

                        Log.e("response provider", "=" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        if (status == 1) {
                            String baseUrl = jsonObject.getString("baseUrl");
                            String serviceId = jsonObject.getString("serviceId");

                            ArrayList<Provider> providerList = new ArrayList<>();
                            JSONArray array = jsonObject.getJSONArray("provider");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject providerObject = array.getJSONObject(i);
                                Provider provider = new Provider();
                                provider.setId(providerObject.getString("id"));
                                provider.setProviderName(providerObject.getString("provider_name"));
                                provider.setProviderImage(baseUrl + providerObject.getString("provider_image"));
                                provider.setServiceId(serviceId);
                                provider.setDealer(providerObject.getString("dealer_name"));
                                provider.setExtraparams("" + providerObject.getJSONArray("extraparam").toString());
                                providerList.add(provider);
                            }

                            if (providerType == ProviderType.ELECTRICITY) {

                                if (isFirstLoad) {
                                    ll_electricity.setVisibility(View.VISIBLE);
                                    JSONObject stateObject = jsonObject.getJSONObject("stateList");
                                    parseStateList(stateObject);
                                }

                            }

                            recyclerView.setVisibility(View.VISIBLE);
                            adapter = new ProviderAdapter(this, providerList);
                            recyclerView.setAdapter(adapter);
                            adapter.setupProviderCallback(provider -> {

                                Intent intent = null;
                                if (sessionManager.getString("bbps").equalsIgnoreCase("1")) {
                                    intent = new Intent(ProviderActivity.this, RechargePrepaid.class);
                                    customType(this, "fadein-to-fadeout");
                                } else {
                                    //intent = new Intent(ProviderActivity.this, Recharge2Activity.class);
                                }

                                intent.putExtra(PROVIDER_TYPE, providerType);
                                intent.putExtra(PROVIDER, provider);
                                startActivity(intent);
                            });

                            if (adapter.getItemCount() == 0)
                                MakeToast.show(ProviderActivity.this, "No provider found for this circle");

                        } else if (status == 200) {

                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            String message = jsonObject.getString("message");
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);

                        } else if (status == 300) {

                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            String message = jsonObject.getString("message");
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);

                        }

                        progressBar.setVisibility(View.GONE);
                        ll_something_went_wrong.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        ll_something_went_wrong.setVisibility(View.VISIBLE);
                        MakeToast.show(this, e.getMessage());
                    }

                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    ll_something_went_wrong.setVisibility(View.VISIBLE);
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
                }) {
        };

        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void parseStateList(JSONObject stateObject) {

        try {
            stateListsHashMap.clear();
            Iterator iterator = stateObject.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                stateListsHashMap.put(stateObject.getString(key), key);
            }


            String[] prepaidStrings = stateListsHashMap.keySet().toArray(new String[0]);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_layout, prepaidStrings);
            dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
            spn_electricity.setAdapter(dataAdapter);


            int position = dataAdapter.getPosition("RAJASTHAN");
            spn_electricity.setSelection(position);

            spn_electricity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    state_id = stateListsHashMap.get(spn_electricity.getSelectedItem().toString());
                    if (!isFirstLoad)
                        getProviders();
                    isFirstLoad = false;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
