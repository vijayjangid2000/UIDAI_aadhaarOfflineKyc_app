package com.vijayjangid.aadharkyc.util;

import android.content.Context;
import android.content.Intent;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class RefreshPage {

    private static UpddateBankDownNewsBalanceListener listener;

    public static void getData(Context context) {
        final StringRequest request = new StringRequest(Request.Method.GET, APIs.REFRESH_PAGE
                + "?" + APIs.USER_TAG + "=" + SharedPref.getInstance(context).getId()
                + "&" + APIs.TOKEN_TAG + "=" + SharedPref.getInstance(context).getToken(),
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        if (status == 1) {
                            SharedPref sharedPref = SharedPref.getInstance(context);
                            String balance = jsonObject.getString("remainingBalance");
                            sharedPref.setUserBalance(balance);
                            String retailerNews = jsonObject.getString("retailerNews");
                            String distributorNews = jsonObject.getString("distributorNews");
                            String bankDownList = jsonObject.getString("bankDownList");
                            listener.onRefreshPage(true, balance, retailerNews, distributorNews, bankDownList);
                        } else if (status == 200) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            String message = jsonObject.getString("message");
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status == 300) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            String message = jsonObject.getString("message");
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        }

                    } catch (JSONException e) {
                        listener.onRefreshPage(false, "0.0", "N/A", "N/A", "N/A");
                    }
                },
                error -> {
                    listener.onRefreshPage(false, "0.0", "N/A", "N/A", "N/A");
                }) {

        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public static void setBankDownNewsBalanceListener(UpddateBankDownNewsBalanceListener listener) {
        RefreshPage.listener = listener;
    }

    public interface UpddateBankDownNewsBalanceListener {
        void onRefreshPage(boolean isRefresh, String balance, String distributorNews, String retailerNews, String bankDownList);
    }
}
