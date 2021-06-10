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
import org.json.JSONObject;

public class DistrictRecyclerAdapter extends RecyclerView.Adapter<DistrictRecyclerAdapter.MyViewHolder> {
    private Context context;
    private JSONArray list;
    private OnItemClickListener listener;

    public DistrictRecyclerAdapter(Context context, JSONArray list, OnItemClickListener listener) {
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
                        listener.onItemClick(list.getJSONObject(getAdapterPosition()), getAdapterPosition());
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
        JSONObject item;
        try {
            item = list.getJSONObject(position);
            holder.text_item.setText(item.getString("District"));
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.length();
    }

    public interface OnItemClickListener {
        void onItemClick(JSONObject item, int positon);
    }

}
