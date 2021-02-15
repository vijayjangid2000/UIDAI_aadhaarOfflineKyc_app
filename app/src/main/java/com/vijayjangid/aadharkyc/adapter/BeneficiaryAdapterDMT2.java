package com.vijayjangid.aadharkyc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.listener.OnBeneficiaryAdapterClickListener;
import com.vijayjangid.aadharkyc.model.Beneficiary;

import java.util.ArrayList;

public class BeneficiaryAdapterDMT2 extends RecyclerView.Adapter<BeneficiaryAdapterDMT2.ViewHolder> {

    private Context context;
    private ArrayList<Beneficiary> beneficiaryList;
    private OnBeneficiaryAdapterClickListener listener;

    public BeneficiaryAdapterDMT2(Context context, ArrayList<Beneficiary> beneficiaryList) {
        this.context = context;
        this.beneficiaryList = beneficiaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.list_beneficiary_dmt2, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Beneficiary beneficiary = beneficiaryList.get(i);
        viewHolder.tv_bank_name.setText(beneficiary.getBank());
        viewHolder.tv_bank_number.setText(beneficiary.getAccount());
        viewHolder.tv_name.setText(beneficiary.getName());
        viewHolder.tv_ifsce_code.setText(beneficiary.getIfsc());

        //listeners
        viewHolder.btn_transfer.setOnClickListener(view -> {
            listener.transfer(i);
        });
        viewHolder.btn_delete.setOnClickListener(view -> {
            listener.delete(i);
        });

    }

    @Override
    public int getItemCount() {
        return beneficiaryList.size();
    }

    public void setupListener(OnBeneficiaryAdapterClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_bank_name;
        TextView tv_bank_number;
        TextView tv_ifsce_code;
        Button btn_delete;
        Button btn_transfer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_bank_number = itemView.findViewById(R.id.tv_bank_number);
            tv_ifsce_code = itemView.findViewById(R.id.tv_ifse_code);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            btn_transfer = itemView.findViewById(R.id.btn_transfer);
        }
    }


}
