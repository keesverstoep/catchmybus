package com.example.catchthebus;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PassAdapter extends RecyclerView.Adapter<PassAdapter.PassViewHolder> {
    private ArrayList<Pass> passes;

    public static class PassViewHolder extends RecyclerView.ViewHolder{
        public TextView timingPointNameTextView;
        public TextView tripStopStatusTextView;
        public TextView expDeptTimeTextView;

        public PassViewHolder(@NonNull View itemView){
            super(itemView);
            timingPointNameTextView = itemView.findViewById(R.id.timing_point_name);
            tripStopStatusTextView = itemView.findViewById(R.id.trip_stop_status);
            expDeptTimeTextView = itemView.findViewById(R.id.exp_pass_dept_time);
        }

    }

    public PassAdapter(ArrayList<Pass> passes){
        this.passes = passes;
    }

    @NonNull
    @Override
    public PassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pass_item, parent, false);
        PassViewHolder evh = new PassViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull PassViewHolder holder, int position) {
        Pass currentItem = passes.get(position);
        holder.timingPointNameTextView.setText(currentItem.getTimingPointName());
        holder.tripStopStatusTextView.setText(currentItem.getTripStopStatus());
        holder.expDeptTimeTextView.setText(currentItem.getExpectedDepartureTimeAsTimeString());

        if(currentItem.getYourStop().equals("your Stop")){
            holder.itemView.setBackgroundColor(Color.GREEN);
        }else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return passes.size();
    }
}
