package com.vijayjangid.aadharkyc.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.model.Member;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Member> memberList;
    private Context context;

    private boolean canClick = true;
    private boolean isLoadingAdded = false;

    public MemberAdapter(Context context) {
        this.context = context;
        memberList = new ArrayList<>();
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_member, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Member member = memberList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;


                viewHolder.tv_name.setText(String.valueOf(member.getName()));
                viewHolder.tv_balance.setText(String.valueOf(member.getBalance()));
                viewHolder.tv_email.setText(String.valueOf(member.getEmail()));
                viewHolder.tv_id.setText(member.getPrefix() + " : " + member.getId());
                viewHolder.tv_shopName.setText(String.valueOf(member.getShopName()));
                viewHolder.tv_status.setText(String.valueOf(member.getStatus()));
                viewHolder.tv_mobile.setText(String.valueOf(member.getMobile()));
                viewHolder.tv_parent.setText(member.getParentDetails());

                viewHolder.tv_itemCount1.setText((position + 1) + "");
                viewHolder.tv_itemCount2.setText((position + 1) + "");

                if (member.getStatus_id().equalsIgnoreCase("0")) {

                    viewHolder.btn_action.setText("Activate");
                    viewHolder.btn_action.setAlpha(0.3f);
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.failed));
                } else {
                    viewHolder.btn_action.setText("Deactivate");
                    viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.success));
                }


                viewHolder.ll_main_layout.setOnClickListener(view -> {
                    viewHideShow(viewHolder, member);
                });
                if (memberList.get(viewHolder.getAdapterPosition()).isHide()) {
                    viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
                    viewHolder.ll_hideable.setVisibility(View.GONE);
                } else {
                    viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
                    viewHolder.ll_hideable.setVisibility(View.VISIBLE);
                }


                viewHolder.btn_action.setOnClickListener(view -> {

                    if (SharedPref.getInstance(context).getRollId() == 4) {
                        if (memberList.get(viewHolder.getAdapterPosition()).getStatus_id().equalsIgnoreCase("0"))
                            MakeToast.show(context, "Action not allowed\ncontact to admin");
                        else if (canClick)
                            showConfirm(viewHolder);

                    } else if (SharedPref.getInstance(context).getRollId() == 1)
                        if (canClick)
                            showConfirm(viewHolder);
                });


                break;
            case LOADING:
//                Do nothing
                break;

        }

    }

    private void showConfirm(ViewHolder viewHolder) {
        Dialog dialog = AppDialogs.confirmTransaction(context);
        TextView tv_heading = dialog.findViewById(R.id.tv_heading);
        tv_heading.setText("Action Confirm");
        TextView tv_subHeading = dialog.findViewById(R.id.tv_subHeading);
        tv_subHeading.setText("Are you sure to In-Active");

        ImageButton btn_dismiss = dialog.findViewById(R.id.btn_dismiss);
        btn_dismiss.setOnClickListener(view1 -> dialog.dismiss());
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(view1 -> dialog.dismiss());
        if (memberList.get(viewHolder.getAdapterPosition()).getStatus_id()
                .equalsIgnoreCase("0")) {
            tv_subHeading.setText("Are you sure to Active");
            tv_subHeading.setTextColor(context.getResources().getColor(R.color.success));
        }

        Button btn_confirm = dialog.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(view1 -> {
            viewHolder.btn_action.setText("Updating...");
            canClick = false;
            updateService(viewHolder);
            dialog.dismiss();

        });


        dialog.show();
    }

    private void viewHideShow(@NonNull MemberAdapter.ViewHolder viewHolder, Member member) {
        if (member.isHide()) {
            viewHolder.ll_arrow_hideable.setVisibility(View.GONE);
            viewHolder.ll_hideable.setVisibility(View.VISIBLE);
            memberList.get(viewHolder.getAdapterPosition()).setHide(false);
        } else {
            viewHolder.ll_arrow_hideable.setVisibility(View.VISIBLE);
            viewHolder.ll_hideable.setVisibility(View.GONE);
            memberList.get(viewHolder.getAdapterPosition()).setHide(true);
        }
    }

    @Override
    public int getItemCount() {
        return memberList == null ? 0 : memberList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == memberList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(Member member) {
        memberList.add(member);
        notifyItemInserted(memberList.size() - 1);
    }

    public void addAll(List<Member> reportList) {
        for (Member member : reportList) {
            add(member);
        }
    }

    public void remove(Member member) {
        int position = memberList.indexOf(member);
        if (position > -1) {
            memberList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {

        add(new Member());
        isLoadingAdded = true;

    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = memberList.size() - 1;
        Member item = null;
        if (position > 0)
            item = getItem(position);

        if (item != null) {
            memberList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Member getItem(int position) {
        return memberList.get(position);
    }

    private void updateService(ViewHolder viewHolder) {

        String status_id = memberList.get(viewHolder.getAdapterPosition()).getStatus_id();

        String update_userId = memberList.get(viewHolder.getAdapterPosition()).getId();
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.USER_DEACTIVATE,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {
                            if (status_id.equalsIgnoreCase("1")) {
                                viewHolder.btn_action.setText("Activate");
                                viewHolder.tv_status.setText("In-active");
                                viewHolder.btn_action.setAlpha(0.3f);
                                viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.failed));
                                memberList.get(viewHolder.getAdapterPosition()).setStatus_id("0");
                            } else {
                                viewHolder.btn_action.setText("Deactivate");
                                viewHolder.tv_status.setText("Active");
                                viewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.success));
                                memberList.get(viewHolder.getAdapterPosition()).setStatus_id("1");
                            }
                            MakeToast.show(context, message);


                        } else if (status.equalsIgnoreCase("3")) {
                            if (status_id.equalsIgnoreCase("0"))
                                viewHolder.btn_action.setText("Activate");
                            else viewHolder.btn_action.setText("Deactivate");
                            MakeToast.show(context, message);
                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        } else {
                            MakeToast.show(context, message);
                        }


                    } catch (JSONException e) {
                        if (status_id.equalsIgnoreCase("0"))
                            viewHolder.btn_action.setText("Activate");
                        else viewHolder.btn_action.setText("Deactivate");
                        MakeToast.show(context, e.getMessage());

                    }

                    canClick = true;

                },
                error -> {
                    canClick = true;
                    if (status_id.equalsIgnoreCase("0"))
                        viewHolder.btn_action.setText("Activate");
                    else viewHolder.btn_action.setText("Deactivate");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(SharedPref.getInstance(context).getId()));
                param.put("token", String.valueOf(SharedPref.getInstance(context).getToken()));
                if (status_id.equalsIgnoreCase("0"))
                    param.put("statusId", "1");
                else param.put("statusId", "0");
                param.put("agentUserId", update_userId);
                return param;
            }

        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tv_name;
        private TextView tv_mobile;
        private TextView tv_balance;
        private TextView tv_status;
        private TextView tv_id;
        private TextView tv_parent;
        private TextView tv_shopName;
        private TextView tv_email;
        private TextView tv_itemCount1;
        private TextView tv_itemCount2;
        private Button btn_action;

        private LinearLayout ll_hideable;
        private LinearLayout ll_main_layout;
        private LinearLayout ll_arrow_hideable;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_balance = itemView.findViewById(R.id.tv_balance);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_shopName = itemView.findViewById(R.id.tv_shopName);
            tv_parent = itemView.findViewById(R.id.tv_parent);
            tv_email = itemView.findViewById(R.id.tv_email);
            btn_action = itemView.findViewById(R.id.btn_action);

            tv_itemCount1 = itemView.findViewById(R.id.tv_itemCount);
            tv_itemCount2 = itemView.findViewById(R.id.tv_itemCount2);

            ll_hideable = itemView.findViewById(R.id.ll_hideable);
            ll_main_layout = itemView.findViewById(R.id.ll_main_layout);
            ll_arrow_hideable = itemView.findViewById(R.id.ll_arrow_hideable);


        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

}
