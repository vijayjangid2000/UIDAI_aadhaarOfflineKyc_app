package com.vijayjangid.aadharkyc;

import android.app.AlertDialog;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cooltechworks.views.ScratchImageView;

import java.util.ArrayList;

public class RewardFragment extends Fragment {

    View root;

    GridView gridView_rewards;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_reward_frament, container, false);

        gridView_rewards = root.findViewById(R.id.gridView_rewards);
        gridView_rewards.setNumColumns(2);

        /* Creating the item arrayList here*/

        Drawable drawableTrophy = getContext().getDrawable(R.drawable.reward_trophy);
        Drawable drawableOla = getContext().getDrawable(R.drawable.ola);
        Drawable drawableUber = getContext().getDrawable(R.drawable.uber);

        ArrayList<RewardTypeObjects> arrayList = new ArrayList<>();
        arrayList.add(new RewardTypeObjects(drawableTrophy, "You unlocked a scratch card", "Scratch Now",
                true, false, false));

        arrayList.add(new RewardTypeObjects(drawableOla, "You have won free 10 Ola cab rides, Use this code", "3E4RDW3",
                false, true, false));

        arrayList.add(new RewardTypeObjects(drawableUber, "You have won free 5 Uber cab rides, Use this code", "544RDW0",
                false, true, false));

        arrayList.add(new RewardTypeObjects(drawableTrophy, "You won", "₹ 2000",
                false, false, true));


        CustomAdapter customAdapter = new CustomAdapter(arrayList);

        gridView_rewards.setAdapter(customAdapter);

        // Inflate the layout for this fragment
        return root;
    }

    public class CustomAdapter implements ListAdapter {

        ArrayList<RewardTypeObjects> objectList;

        public CustomAdapter(ArrayList<RewardTypeObjects> objectList) {
            this.objectList = objectList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int pos = position;

            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(R.layout.listview_reward, null);

                ImageView iv_rewardImage = convertView.findViewById(R.id.board_imageview);
                TextView tv_details = convertView.findViewById(R.id.tv_detailReward);
                TextView tv_title = convertView.findViewById(R.id.tv_titleReward);

                View.OnClickListener a = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertView();
                    }
                };

                if (objectList.get(position).isScratchAble) {
                    tv_details.setOnClickListener(a);
                    tv_title.setOnClickListener(a);
                    iv_rewardImage.setOnClickListener(a);
                }

                if (objectList.get(position).isAlreadyScratched) {
                    iv_rewardImage.setAlpha(0.6f);
                }

                if (objectList.get(position).isCode) {
                    tv_title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Drawable drawableUber = getContext().getDrawable(R.drawable.uber);
                    tv_title.setCompoundDrawables(null, null, drawableUber, null);
                }
                tv_details.setText(objectList.get(position).getDetails());
                tv_title.setText(objectList.get(position).getCode());
                iv_rewardImage.setImageDrawable(objectList.get(position).getImage());

            }
            return convertView;
        }

        void showAlertView() {

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
            final AlertDialog alertDialog = alertBuilder.create();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_reward_scratching, null);
            alertDialog.setView(dialogView);

            final LinearLayout linearLayout = dialogView.findViewById(R.id.ll_RewardScratch);
            final ImageView ivb_closeAlert = dialogView.findViewById(R.id.iv_close_alert);
            final TextView tv_scratchAmount = dialogView.findViewById(R.id.tv_amount_alert);
            final ScratchImageView scratchImageView = dialogView.findViewById(R.id.iv_scratch_alert);
            ivb_closeAlert.setVisibility(View.GONE);
            tv_scratchAmount.setVisibility(View.GONE);

            final Animation animation = new AlphaAnimation(1, 0.4f); //to change visibility from visible to invisible
            animation.setDuration(1000); //1 second duration for each animation cycle
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
            animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.

            ivb_closeAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivb_closeAlert.setAlpha(0.3f);
                    alertDialog.cancel();
                }
            });

            scratchImageView.setRevealListener(new ScratchImageView.IRevealListener() {
                @Override
                public void onRevealed(ScratchImageView tv) {
                    ivb_closeAlert.setVisibility(View.VISIBLE);
                    tv_scratchAmount.setVisibility(View.VISIBLE);
                    linearLayout.setBackgroundColor(Color.TRANSPARENT);
                    scratchImageView.startAnimation(animation);
                    linearLayout.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }

                @Override
                public void onRevealPercentChangedListener(ScratchImageView siv, float percent) {
                    if (percent > 0.35) siv.reveal();
                }
            });

            alertDialog.show();

        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return objectList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return objectList.size();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    public class RewardTypeObjects {

        private Drawable image;
        private String details;
        private String code;
        private boolean isScratchAble;
        private boolean isCode;
        private boolean isAlreadyScratched;

        public RewardTypeObjects(Drawable image, String details,
                                 String code, boolean isScratchAble,
                                 boolean isCode, boolean isAlreadyScratched) {
            this.image = image;
            this.details = details;
            this.code = code;
            this.isScratchAble = isScratchAble;
            this.isCode = isCode;
            this.isAlreadyScratched = isAlreadyScratched;
        }

        public Drawable getImage() {
            return image;
        }

        public String getDetails() {
            return details;
        }

        public String getCode() {
            return code;
        }

        public boolean isScratchAble() {
            return isScratchAble;
        }

        public boolean isCode() {
            return isCode;
        }

        public boolean isAlreadyScratched() {
            return isAlreadyScratched;
        }
    }

}


