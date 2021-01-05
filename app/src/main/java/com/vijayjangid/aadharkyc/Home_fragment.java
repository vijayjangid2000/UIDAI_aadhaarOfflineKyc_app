package com.vijayjangid.aadharkyc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vijayjangid.aadharkyc.databinding.FragmentHomeFragmentBinding;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static maes.tech.intentanim.CustomIntent.customType;

public class Home_fragment extends Fragment
        implements View.OnClickListener {

    FragmentHomeFragmentBinding fragmentHomeFragmentBinding;

    ImageView iv_scanPay, iv_sendMoney, iv_sendAgain,
            iv_prepaid, iv_electricity, iv_water, iv_insurance,
            iv_landLine, iv_postpaid, iv_dth, iv_dataCard, iv_fasTag;

    TextView tv_scanPay, tv_sendMoney, tv_sendAgain,
            tv_prepaid, tv_electricity, tv_water, tv_insurance,
            tv_landLine, tv_postpaid, tv_dth, tv_dataCard, tv_fasTag;

    ViewPager2 viewPager2;
    TabLayout tabLayout;
    Animation animation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentHomeFragmentBinding = FragmentHomeFragmentBinding.inflate(getLayoutInflater());
        View root = fragmentHomeFragmentBinding.getRoot();

        // setting id's for views here

        iv_scanPay = fragmentHomeFragmentBinding.scanpayIv;
        iv_sendMoney = fragmentHomeFragmentBinding.sendmoneyIv;
        iv_sendAgain = fragmentHomeFragmentBinding.sendagainIv;
        iv_prepaid = fragmentHomeFragmentBinding.prepaidIv;
        iv_electricity = fragmentHomeFragmentBinding.electricityIv;
        iv_water = fragmentHomeFragmentBinding.waterIv;
        iv_insurance = fragmentHomeFragmentBinding.insuranceIv;
        iv_landLine = fragmentHomeFragmentBinding.landlineIv;
        iv_postpaid = fragmentHomeFragmentBinding.postpaidIv;
        iv_dth = fragmentHomeFragmentBinding.dthIv;
        iv_dataCard = fragmentHomeFragmentBinding.datacardIv;
        iv_fasTag = fragmentHomeFragmentBinding.fastagIv;

        tv_scanPay = fragmentHomeFragmentBinding.scanpayTv;
        tv_sendMoney = fragmentHomeFragmentBinding.sendmoneyTv;
        tv_sendAgain = fragmentHomeFragmentBinding.sendAgainTv;
        tv_prepaid = fragmentHomeFragmentBinding.prepaidTv;
        tv_electricity = fragmentHomeFragmentBinding.electricityTv;
        tv_water = fragmentHomeFragmentBinding.waterTv;
        tv_insurance = fragmentHomeFragmentBinding.insuranceTv;
        tv_landLine = fragmentHomeFragmentBinding.landlineTv;
        tv_postpaid = fragmentHomeFragmentBinding.postpaidTv;
        tv_dth = fragmentHomeFragmentBinding.dthTv;
        tv_dataCard = fragmentHomeFragmentBinding.datacardTv;
        tv_fasTag = fragmentHomeFragmentBinding.fastagTv;
        viewPager2 = fragmentHomeFragmentBinding.viewpagerHome;
        tabLayout = fragmentHomeFragmentBinding.tablayout;

        // making animation
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_aplha);

        iv_prepaid.setOnClickListener(this);
        iv_scanPay.setOnClickListener(this);
        iv_sendMoney.setOnClickListener(this);
        iv_sendAgain.setOnClickListener(this);
        iv_electricity.setOnClickListener(this);
        iv_water.setOnClickListener(this);
        iv_landLine.setOnClickListener(this);
        iv_insurance.setOnClickListener(this);
        iv_postpaid.setOnClickListener(this);
        iv_dth.setOnClickListener(this);
        iv_fasTag.setOnClickListener(this);
        iv_dataCard.setOnClickListener(this);

        tv_scanPay.setOnClickListener(this);
        tv_sendMoney.setOnClickListener(this);
        tv_sendAgain.setOnClickListener(this);
        tv_prepaid.setOnClickListener(this);
        tv_electricity.setOnClickListener(this);
        tv_water.setOnClickListener(this);
        tv_insurance.setOnClickListener(this);
        tv_landLine.setOnClickListener(this);
        tv_postpaid.setOnClickListener(this);
        tv_dth.setOnClickListener(this);
        tv_dataCard.setOnClickListener(this);
        tv_fasTag.setOnClickListener(this);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        viewPager2.setAdapter(new ViewPagerAdapter(getContext(), arrayList, viewPager2));

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });

        TabLayoutMediator tabLayoutMediator1 = new TabLayoutMediator(tabLayout, viewPager2, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        });

        tabLayoutMediator1.attach();

        return root;
    }

    @Override
    public void onClick(View v) {
        float alphaVal = (float) 0.5;

        int id = v.getId();
        switch (id) {
            case R.id.scanpay_tv:
            case R.id.scanpay_iv:
                iv_scanPay.setAlpha(alphaVal);
                tv_scanPay.setAlpha(alphaVal);

                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }

                break;

            case R.id.sendmoney_tv:
            case R.id.sendmoney_iv:
                iv_sendMoney.setAlpha(alphaVal);
                tv_sendMoney.setAlpha(alphaVal);
                break;

            case R.id.sendAgain_tv:
            case R.id.sendagain_iv:
                iv_sendAgain.setAlpha(alphaVal);
                tv_sendAgain.setAlpha(alphaVal);
                break;

            case R.id.prepaid_tv:
            case R.id.prepaid_iv:
                iv_prepaid.setAlpha(alphaVal);
                tv_prepaid.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), Recharge_mobile_activity.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.electricity_tv:
            case R.id.electricity_iv:
                iv_electricity.setAlpha(alphaVal);
                tv_electricity.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), ElectricityRecharge.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.water_tv:
            case R.id.water_iv:
                iv_water.setAlpha(alphaVal);
                tv_water.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), Water_Bill_activity.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.insurance_tv:
            case R.id.insurance_iv:
                iv_insurance.setAlpha(alphaVal);
                tv_insurance.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), InsurancePay.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.landline_tv:
            case R.id.landline_iv:
                iv_landLine.setAlpha(alphaVal);
                tv_landLine.setAlpha(alphaVal);
                break;

            case R.id.postpaid_tv:
            case R.id.postpaid_iv:
                iv_postpaid.setAlpha(alphaVal);
                tv_postpaid.setAlpha(alphaVal);
                break;

            case R.id.dth_tv:
            case R.id.dth_iv:
                iv_dth.setAlpha(alphaVal);
                tv_dth.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), DthRecharge.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.datacard_tv:
            case R.id.datacard_iv:
                iv_dataCard.setAlpha(alphaVal);
                tv_dataCard.setAlpha(alphaVal);

                break;

            case R.id.fastag_iv:
            case R.id.fastag_tv:
                iv_fasTag.setAlpha(alphaVal);
                tv_fasTag.setAlpha(alphaVal);
                break;
        }
    }

    public static class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;
        private ViewPager2 viewPager2;

        ViewPagerAdapter(Context context, List<String> data, ViewPager2 viewPager2) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.viewPager2 = viewPager2;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.layout_viewpager, parent, false);
            return new ViewHolder(view);
        }

        /*this comes again and again*/
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {


            ViewHolder(View itemView) {
                super(itemView);
                /*here will be id and on click listeners*/
            }
        }
    }

    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.alertview_progressbar, null);
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

    @Override
    public void onResume() {
        super.onResume();

        float normalDark = 1;

        iv_scanPay.setAlpha(normalDark);
        tv_scanPay.setAlpha(normalDark);
        iv_sendMoney.setAlpha(normalDark);
        tv_sendMoney.setAlpha(normalDark);
        iv_sendAgain.setAlpha(normalDark);
        tv_sendAgain.setAlpha(normalDark);
        iv_prepaid.setAlpha(normalDark);
        tv_prepaid.setAlpha(normalDark);
        iv_electricity.setAlpha(normalDark);
        tv_electricity.setAlpha(normalDark);
        iv_water.setAlpha(normalDark);
        tv_water.setAlpha(normalDark);
        iv_insurance.setAlpha(normalDark);
        tv_insurance.setAlpha(normalDark);
        iv_landLine.setAlpha(normalDark);
        tv_landLine.setAlpha(normalDark);
        iv_postpaid.setAlpha(normalDark);
        tv_postpaid.setAlpha(normalDark);
        iv_dth.setAlpha(normalDark);
        tv_dth.setAlpha(normalDark);
        iv_dataCard.setAlpha(normalDark);
        tv_dataCard.setAlpha(normalDark);
        iv_fasTag.setAlpha(normalDark);
        tv_fasTag.setAlpha(normalDark);
    }

    // for scanner

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(getContext(), contents, Toast.LENGTH_SHORT).show();
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
                Toast.makeText(getContext(), "Scanner cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

}