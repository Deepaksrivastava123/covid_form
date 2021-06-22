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
import com.sdbiosensor.covicatch.network.models.GetHistoryResponseModel;

import java.util.ArrayList;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<GetHistoryResponseModel.DataModel> list;
    private OnItemClickListener listener;

    public HistoryRecyclerAdapter(Context context, ArrayList<GetHistoryResponseModel.DataModel> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button button;
        public TextView item;

        public MyViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            button = view.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(list.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()) .inflate(R.layout.item_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final GetHistoryResponseModel.DataModel item = list.get(position);
        holder.item.setText("Date: " + item.getCreatedDate() + "\n" + "Kit Serial Number: " + item.getKitSerialNumber() + "\n" + "Result: " + item.getResultStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(GetHistoryResponseModel.DataModel item, int position);
    }

}
