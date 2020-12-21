package com.vijayjangid.aadharkyc;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class Home_fragment extends Fragment {

    ImageView scanPay_iv, sendMoney_iv, sendAgain_iv,
            prepaid_iv, electricity_iv, water_iv, insurance_iv,
            landLine_iv, postpaid_iv, dth_iv, dataCard_iv, fasTag_iv;

    TextView accountBalance_tv, walletBalance_tv,
            scanPay_tv, sendMoney_tv, sendAgain_tv,
            prepaid_tv, electricity_tv, water_tv, insurance_tv,
            landLine_tv, postpaid_tv, dth_tv, dataCard_tv, fasTag_tv;

    Animation animation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        // setting id's for views here
        scanPay_iv = root.findViewById(R.id.scanpay_iv);
        sendMoney_iv = root.findViewById(R.id.sendmoney_iv);
        sendAgain_iv = root.findViewById(R.id.sendagain_iv);
        prepaid_iv = root.findViewById(R.id.prepaid_iv);
        electricity_iv = root.findViewById(R.id.electricity_iv);
        water_iv = root.findViewById(R.id.water_iv);
        insurance_iv = root.findViewById(R.id.insurance_iv);
        landLine_iv = root.findViewById(R.id.landline_iv);
        postpaid_iv = root.findViewById(R.id.postpaid_iv);
        dth_iv = root.findViewById(R.id.dth_iv);
        dataCard_iv = root.findViewById(R.id.datacard_iv);
        fasTag_iv = root.findViewById(R.id.fastag_iv);

        accountBalance_tv = root.findViewById(R.id.account_balance_tv);
        walletBalance_tv = root.findViewById(R.id.wallet_balance_tv);
        scanPay_tv = root.findViewById(R.id.scanpay_tv);
        sendMoney_tv = root.findViewById(R.id.sendmoney_tv);
        sendAgain_tv = root.findViewById(R.id.sendAgain_tv);
        prepaid_tv = root.findViewById(R.id.prepaid_tv);
        electricity_tv = root.findViewById(R.id.electricity_tv);
        water_tv = root.findViewById(R.id.water_tv);
        insurance_tv = root.findViewById(R.id.insurance_tv);
        landLine_tv = root.findViewById(R.id.landline_tv);
        postpaid_tv = root.findViewById(R.id.postpaid_tv);
        dth_tv = root.findViewById(R.id.dth_tv);
        dataCard_tv = root.findViewById(R.id.datacard_tv);
        fasTag_tv = root.findViewById(R.id.fastag_tv);

        // making animation
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_aplha);

        return root;
    }

    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.progressbar_viewxml, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(view);
        alert.setCancelable(false);
        alert.setIcon(null);
        alert.show();
    }

}