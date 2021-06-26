package com.sdbiosensor.covicatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.network.models.CreatePatientRequestModel;

import java.util.ArrayList;

public class ExistingUsersDialogAdapter extends RecyclerView.Adapter<ExistingUsersDialogAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<CreatePatientRequestModel> list;
    private OnItemClickListener listener;

    public ExistingUsersDialogAdapter(Context context, ArrayList<CreatePatientRequestModel> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_item;

        public MyViewHolder(View view) {
            super(view);
            text_item = view.findViewById(R.id.item);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(list.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()) .inflate(R.layout.item_string_recycler, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CreatePatientRequestModel item = list.get(position);
        holder.text_item.setText(item.getFirstName() + " " + item.getLastName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CreatePatientRequestModel item, int position);
    }

}
