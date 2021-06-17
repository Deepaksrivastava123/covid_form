package com.sdbiosensor.covicatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.sdbiosensor.covicatch.R;

import java.util.ArrayList;

public class MultiRecyclerAdapter extends RecyclerView.Adapter<MultiRecyclerAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> list;
    private ArrayList<String> preSelectedItems;
    private OnItemClickListener listener;

    public MultiRecyclerAdapter(Context context, ArrayList<String> list, ArrayList<String> preSelectedItems, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.preSelectedItems = preSelectedItems;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox text_item;

        public MyViewHolder(View view) {
            super(view);
            text_item = view.findViewById(R.id.item);
            text_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    listener.onItemCheckChange(list.get(getAdapterPosition()), getAdapterPosition(), b);
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()) .inflate(R.layout.item_multi_recycler, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final String item = list.get(position);
        holder.text_item.setText(item);
        if(preSelectedItems.contains(item)){
            holder.text_item.setChecked(true);
        } else {
            holder.text_item.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemCheckChange(String item, int position, boolean isSelected);
    }

}
