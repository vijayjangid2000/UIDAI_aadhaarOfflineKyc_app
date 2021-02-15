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
import com.vijayjangid.aadharkyc.model.Report;

import java.util.ArrayList;

public class RemTransAdapter extends RecyclerView.Adapter<RemTransAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Report> reports;
    private OnBeneficiaryAdapterClickListener listener;

    public RemTransAdapter(Context context, ArrayList<Report> beneficiaryList) {
        this.context = context;
        this.reports = beneficiaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_rem_trans, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Report report = reports.get(i);
        viewHolder.tv_bank_name.setText(report.getBankName());
        viewHolder.tv_bank_number.setText(report.getBeneAccount());
        viewHolder.tv_name.setText(report.getName());
        viewHolder.tv_ifsce_code.setText(report.getIfscCode());

        viewHolder.btn_status.setText(report.getStatus());


    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void setupListener(OnBeneficiaryAdapterClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_bank_name;
        TextView tv_bank_number;
        TextView tv_ifsce_code;
        Button btn_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_bank_number = itemView.findViewById(R.id.tv_bank_number);
            tv_ifsce_code = itemView.findViewById(R.id.tv_ifse_code);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);

            btn_status = itemView.findViewById(R.id.btn_status);
        }
    }


}
