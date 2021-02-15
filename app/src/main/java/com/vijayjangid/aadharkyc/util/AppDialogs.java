package com.vijayjangid.aadharkyc.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.vijayjangid.aadharkyc.R;

import java.util.Objects;

public class AppDialogs {


    public static ProgressDialog progressDialog(Context context, String title, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        if (!message.equals("")) {
            progressDialog.setMessage(message);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static Dialog a2zdialogMessage(Context context, String heading) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.a2z_dialog);
        dialog.setCanceledOnTouchOutside(false);

        TextView tv_heading = dialog.findViewById(R.id.tv_heading);
        tv_heading.setText(heading);

        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.5f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        // paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        //Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public static Dialog dialogMessage(Context context, String heading, int type) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_success_failed);
        dialog.setCanceledOnTouchOutside(false);

        TextView tv_heading = dialog.findViewById(R.id.tv_heading);
        ImageView iv_image = dialog.findViewById(R.id.iv_image);
        tv_heading.setText(heading);


        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.5f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        // paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // dialog.getWindow().getAttributes().windowAnimations =R.style.DialogTheme2;
        return dialog;
    }


    public static void showMessageDialogtc(Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);


        builder.setTitle("I hereby understand and agree that:-\n").setMessage("1. Details submitteded by me for impanelment is true & correct and belongs to me.\n" +
                "" +
                "\n" +
                "2. I have understood the terms of BC Business and agree to comply with Bank's, Company's, Provider's and RBI's guidelines from me-to-me.\n" +
                "\n" +
                "3. I will maintain the required details for each transaction processed by me on behalf of customer.\n" +
                "\n" +
                "4. I will not misuse Company's, Provider's or Bank's systems for unlawful transacons.\n" +
                "\n" +
                "5. I will abide by the terms of agreement & service for which I am being empanelled, experiences etc is found to be improper, incorrect or not as per ICICI Bank, Provider, Company's or RBI's guidelines for impanelment.\n" +
                "\n" +
                "6. I authorize A2Z Suvidhaa, Provider & Bank to verify the details mentioned above and such other details as they may deem fit in connection with my impanelment.\n" +
                "\n" +
                "7. I confirm that I am not associated with any company providing money transfer or such BC Business Services or I am willing to resign from any such company for the purpose of onboarding with A2Z Suvidhaa.\n" +
                "\n" +
                "8. I promise not to share the customer details with others.\n" +
                "\n" +
                "9. I undertake that I will not use the Bank's services offered to me by Distributor Partner for any purpose which is illegal in the eyes of law.\n")
                .setCancelable(false)
                .setPositiveButton("Close ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
        ;
        AlertDialog alert = builder.create();
        if (!activity.isFinishing())
            alert.show();
    }

    public static Dialog searchReport(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_report_search);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog searchPaymentFundReport(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_payment_fund_search);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog searchFundTranser(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_search);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog confirmFundRequestDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_fund_request_confim);
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout relativeLayout = dialog.findViewById(R.id.ll_main_layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.gravity = Gravity.CENTER;
        relativeLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog pgStatusDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_transaction_status);
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout relativeLayout = dialog.findViewById(R.id.ll_main_layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.gravity = Gravity.CENTER;
        relativeLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog searchReport1(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_report_search1);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog complain(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_report_complain);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog deleteBene(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_bene_delete_conform);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog dele_bene_otp(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_otp_bene_delete);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog transferConformDetail(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_transfer_conform);
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout linearLayout = dialog.findViewById(R.id.ll_main_layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }


    public static Dialog transferConformDetailDMT1(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_transfer_conform_dmt1);
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout linearLayout = dialog.findViewById(R.id.ll_main_layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog aepsResponseDialog(Context context, int type) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_apes_response);
        dialog.setCanceledOnTouchOutside(false);
        if (type == 1)
            ((TextView) dialog.findViewById(R.id.title)).setText("AEPS Transaction Details");
        else
            ((TextView) dialog.findViewById(R.id.title)).setText("ADHAAR PAY Transaction Details");
        RelativeLayout linearLayout = dialog.findViewById(R.id.ll_main_layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog billRechargeConfirmation(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_bill_pay_confirm);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog rechargeConfirmation(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_rechar_conform);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog fundTransferConfirmation(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_fund_transfer_confirm);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog processing(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_processing);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog requestApprove(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_request_approve);
        dialog.setCanceledOnTouchOutside(false);
        ScrollView linearLayout = dialog.findViewById(R.id.ll_main_layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.95f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.85f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.height = heightLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }


    public static Dialog dmt1TransactionSlip(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_dmt1_transfer_slip);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.ll_main_layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.95f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.80f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height=heightLcl;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public static Dialog checkStatus(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_check_status);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.90f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }


    public static Dialog r2rTransferConfimationWithPin(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_verify_pin);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.90f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }


    public static Dialog confirmTransaction(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_transaction_confirm);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }


    public static Dialog updateTransaction(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_update_transaction);
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout linearLayout = dialog.findViewById(R.id.ll_main_layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels * 0.90f);
        int heightLcl = (int) (displayMetrics.heightPixels * 0.7f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        //paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    /*public static void volleyErrorDialog(Context context, int type){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_volley_error);
        dialog.setCanceledOnTouchOutside(false);

        LinearLayout linearLayout = dialog.findViewById(R.id.layout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthLcl = (int) (displayMetrics.widthPixels*0.85f);
        int heightLcl = (int) (displayMetrics.heightPixels*0.5f);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        paramsLcl.width = widthLcl;
        // paramsLcl.height =heightLcl ;
        paramsLcl.gravity = Gravity.CENTER;
        linearLayout .setLayoutParams(paramsLcl);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(view->{
            dialog.dismiss();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
        TextView tv_errorTypeTextView= dialog.findViewById(R.id.tv_errorType);
        if(type==0)
            tv_errorTypeTextView.setText("(e)");
        else  tv_errorTypeTextView.setText("(c)");


        dialog.setOnCancelListener(dialog1 -> {
            dialog.dismiss();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });

        dialog.show();
    }*/

    public static Dialog transactionStatus(Context context, String heading, int type) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_success_failed);
        dialog.setCancelable(false);

        TextView tv_heading = dialog.findViewById(R.id.tv_heading);
        ImageView iv_image = dialog.findViewById(R.id.iv_image);
        tv_heading.setText(heading);

        if (type == 1) {
            iv_image.setImageResource(R.drawable.icon_success);
            tv_heading.setTextColor(context.getResources().getColor(R.color.success));
        } else if (type == 2) {
            iv_image.setImageResource(R.drawable.v_cancel);
            tv_heading.setTextColor(context.getResources().getColor(R.color.failed));
            tv_heading.setText("Transaction Failed: " + heading);
        } else if (type == 3 || type == 18) {
            iv_image.setImageResource(R.drawable.icon_alarm);
            tv_heading.setTextColor(context.getResources().getColor(R.color.warning));
            tv_heading.setText("Transaction Failed: " + heading);
        } else {
            iv_image.setImageResource(R.drawable.unknown_status);
            tv_heading.setTextColor(context.getResources().getColor(R.color.text_color_3));
            tv_heading.setText("Transaction Failed: " + heading);
        }

        return dialog;
    }
}
