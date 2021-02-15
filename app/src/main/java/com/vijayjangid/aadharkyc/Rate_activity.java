package com.vijayjangid.aadharkyc;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

public class Rate_activity extends AppCompatActivity implements View.OnClickListener {

    RatingBar ratingBar;
    TextView tv_proceed, tv_communicate, tv_giveFeedback, tv_rateOnPlayStore, tv_skipRating;
    AlertDialog dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Rate Our App");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow_ios);


        tv_proceed = findViewById(R.id.tv_selectOne);
        tv_communicate = findViewById(R.id.tv_communicate);
        tv_giveFeedback = findViewById(R.id.tv_giveFeedback);
        tv_rateOnPlayStore = findViewById(R.id.tv_rateOnPlayStore);
        tv_skipRating = findViewById(R.id.tv_skipRating);
        ratingBar = findViewById(R.id.ratingBar);

        tv_proceed.setOnClickListener(this);
        tv_communicate.setOnClickListener(this);
        tv_giveFeedback.setOnClickListener(this);
        tv_rateOnPlayStore.setOnClickListener(this);
        tv_skipRating.setOnClickListener(this);
        ratingBar.setOnClickListener(this);
        ratingBar.setStepSize(1);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingSelected((int) rating);
                tv_proceed.setVisibility(View.GONE);
            }
        });

        tv_skipRating.setVisibility(View.GONE);
        tv_rateOnPlayStore.setVisibility(View.GONE);
        tv_giveFeedback.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_rateOnPlayStore:
                inAppRating();
                break;

            case R.id.tv_giveFeedback:
                Toast.makeText(this, "This will Open Feedback " +
                        "Activity, FAQ etc", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tv_skipRating:
                onBackPressed();
                break;
        }
    }

    void ratingSelected(int stars) {

        switch (stars) {
            case 1:
            case 2:
            case 3:
                tv_communicate.setText("Sorry to hear that!\nMind giving us a feedback");
                rateOrFeedback(true);
                break;
            case 4:
            case 5:
                tv_communicate.setText("Yay! Mind Giving us a\n" + stars + " Star Rating on Google Play Store");
                rateOrFeedback(false);
                break;
        }
    }

    void rateOrFeedback(boolean feedback) {

        tv_proceed.setVisibility(View.GONE);
        tv_skipRating.setVisibility(View.VISIBLE);

        if (feedback) {
            tv_giveFeedback.setVisibility(View.VISIBLE);
            tv_rateOnPlayStore.setVisibility(View.GONE);
        } else {
            tv_rateOnPlayStore.setVisibility(View.VISIBLE);
            tv_giveFeedback.setVisibility(View.GONE);
        }

    }

    void inAppRating() {

        showProgressBar(true, "Please wait, opening play store");

        final ReviewManager manager = ReviewManagerFactory.create(Rate_activity.this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        final String message = "App Not found on Google PlayStore";

        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(Rate_activity.this, reviewInfo);
                    flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Rate_activity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    // There was some problem, continue regardless of the result.
                    Toast.makeText(Rate_activity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.layout_progressbar_rating, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        AlertDialog.Builder alertBldr_loading = new AlertDialog.Builder(this)
                .setCancelable(false);
        dialogView = alertBldr_loading.create();
        dialogView.setView(view);
        Window window = dialogView.getWindow();
        if (window != null) window.setBackgroundDrawableResource(R.color.Transparent);
        dialogView.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dialogView != null) dialogView.cancel();
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}