package com.vijayjangid.aadharkyc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.model.BankDetail;

import java.util.ArrayList;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BankDetail> bankDetailList;
    private OnAccountListAdapterClickListener listener;

    public AccountListAdapter(Context context, ArrayList<BankDetail> beneficiaryList) {
        this.context = context;
        this.bankDetailList = beneficiaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_acc_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        BankDetail bankDetail = bankDetailList.get(i);
        viewHolder.tv_name.setText(bankDetail.getBeneName());
        viewHolder.tv_bank_number.setText(bankDetail.getAccountNumber());
        viewHolder.tv_ifsce_code.setText(bankDetail.getIfsc());
        viewHolder.tv_mobile.setText(bankDetail.getMobile());
        viewHolder.tv_rem.setText(bankDetail.getBalance());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.clickToMobile(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bankDetailList.size();
    }

    public void setupListener(OnAccountListAdapterClickListener listener) {
        this.listener = listener;
    }

    public interface OnAccountListAdapterClickListener {
        void clickToMobile(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_rem;
        TextView tv_mobile;
        TextView tv_bank_number;
        TextView tv_ifsce_code;
        Button btn_delete;
        Button btn_verify;
        Button btn_transfer;
        ImageView iv_veri;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_bank_number = itemView.findViewById(R.id.tv_bank_number);
            tv_ifsce_code = itemView.findViewById(R.id.tv_ifse_code);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_rem = itemView.findViewById(R.id.tv_rem_bal);

        }
    }


}
