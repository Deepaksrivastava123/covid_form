package com.sdbiosensor.covicatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sdbiosensor.covicatch.R;

import org.json.JSONArray;
import org.json.JSONException;

public class JsonArrayRecyclerAdapter extends RecyclerView.Adapter<JsonArrayRecyclerAdapter.MyViewHolder> {
    private Context context;
    private JSONArray list;
    private OnItemClickListener listener;

    public JsonArrayRecyclerAdapter(Context context, JSONArray list, OnItemClickListener listener) {
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
                    try {
                        listener.onItemClick(list.getString(getAdapterPosition()), getAdapterPosition());
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
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
        String item = "";
        try {
            item = list.getString(position);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        holder.text_item.setText(item);
    }

    @Override
    public int getItemCount() {
        return list.length();
    }

    public interface OnItemClickListener {
        void onItemClick(String item, int position);
    }

}
