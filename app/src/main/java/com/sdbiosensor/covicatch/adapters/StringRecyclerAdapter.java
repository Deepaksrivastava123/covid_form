package com.sdbiosensor.covicatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sdbiosensor.covicatch.R;

import java.util.ArrayList;

public class StringRecyclerAdapter extends RecyclerView.Adapter<StringRecyclerAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> list;
    private OnItemClickListener listener;

    public StringRecyclerAdapter(Context context, ArrayList<String> list, OnItemClickListener listener) {
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
        final String item = list.get(position);
        holder.text_item.setText(item);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String item, int positon);
    }

}
