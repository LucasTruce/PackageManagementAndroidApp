package com.app.packagemanagementandroidapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.packagemanagementandroidapp.R;
import com.app.packagemanagementandroidapp.model.Pack;

import java.util.ArrayList;
import java.util.List;

public class PaginationAdapter extends RecyclerView.Adapter<PaginationAdapter.MyViewHolder> {

    private List<Pack> packagesList;
    private Context context;

    public PaginationAdapter(List<Pack> packagesList, Context context) {
        this.packagesList = packagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_list, parent,false );
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Pack pack = packagesList.get(position);
        holder.mPackageNumber.setText(pack.getPackageNumber());
        holder.mPackageSenderAddress.setText(pack.getSender().getPostCode() + ", " + pack.getSender().getCity() + "\nul. " + pack.getSender().getStreet() + " " + pack.getSender().getHouseNumber() + "/" + pack.getSender().getApartmentNumber());
        holder.mPackageSenderPhone.setText(pack.getSender().getPhoneNumber());
        holder.mPackageSenderTitle.setText(pack.getSender().getName() + " " + pack.getSender().getLastName());
    }

    @Override
    public int getItemCount() {
        return packagesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mPackageNumber;
        private TextView mPackageSenderTitle;
        private TextView mPackageSenderAddress;
        private TextView mPackageSenderPhone;

        public MyViewHolder(View itemView) {
            super(itemView);

            mPackageNumber = (TextView) itemView.findViewById(R.id.package_number);
            mPackageSenderTitle = (TextView) itemView.findViewById(R.id.packageSenderTitle);
            mPackageSenderAddress = (TextView) itemView.findViewById(R.id.packageSenderAdress);
            mPackageSenderPhone = (TextView) itemView.findViewById(R.id.packageSenderPhone);
        }
    }

    public void addPackages(List<Pack> packages) {
        packagesList.addAll(packages);

        notifyDataSetChanged();

    }

}
