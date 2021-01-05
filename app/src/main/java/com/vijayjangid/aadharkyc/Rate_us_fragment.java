package com.vijayjangid.aadharkyc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

public class Rate_us_fragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ReviewManager manager = ReviewManagerFactory.create(getContext());
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
                    flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Not found on Playstore", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    // There was some problem, continue regardless of the result.
                    Toast.makeText(getContext(), "Not found on Playstore", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return inflater.inflate(R.layout.fragment_rate_us_fragment, container, false);
    }
}