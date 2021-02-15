package com.vijayjangid.aadharkyc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.listener.ProviderCallbackListener;
import com.vijayjangid.aadharkyc.model.Provider;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Provider> providerList;
    private ProviderCallbackListener listener;

    public ProviderAdapter(Context context, ArrayList<Provider> providerList) {
        this.context = context;
        this.providerList = providerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_provider, viewGroup,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Provider provider = providerList.get(i);

        viewHolder.tv_providerName.setText(provider.getProviderName());

        Picasso.get()
                .load(provider.getProviderImage())
                .resize(50, 50)
                .centerCrop()
                .placeholder(R.drawable.icon_no_image)
                .error(R.drawable.icon_no_image)
                .into(viewHolder.cv_providerImage);


        viewHolder.main_layout.setOnClickListener(view ->
                listener.onProviderCallback(providerList.get(viewHolder.getAdapterPosition())));

    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public void setupProviderCallback(ProviderCallbackListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView cv_providerImage;
        TextView tv_providerName;
        LinearLayout main_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_providerImage = itemView.findViewById(R.id.cv_providerImage);
            tv_providerName = itemView.findViewById(R.id.tv_providerName);
            main_layout = itemView.findViewById(R.id.ll_main_layout);

        }
    }
}
