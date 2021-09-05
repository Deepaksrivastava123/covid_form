package com.dotvik.covify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.dotvik.covify.R;
import com.dotvik.covify.network.models.CreatePatientRequestModel;

import java.util.ArrayList;

public class ExistingUsersRecyclerAdapter extends RecyclerView.Adapter<ExistingUsersRecyclerAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<CreatePatientRequestModel> list;
    private OnItemClickListener listener;

    public ExistingUsersRecyclerAdapter(Context context, ArrayList<CreatePatientRequestModel> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button button;

        public MyViewHolder(View view) {
            super(view);
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
        View itemView = LayoutInflater.from(parent.getContext()) .inflate(R.layout.item_existing_user, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CreatePatientRequestModel item = list.get(position);
        holder.button.setText(item.getFirstName() + " " + item.getLastName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CreatePatientRequestModel item, int position);
    }

}
