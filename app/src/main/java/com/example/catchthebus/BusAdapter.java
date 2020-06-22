package com.example.catchthebus;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {
    private ArrayList<Bus> buses;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener{
        void onItemClick(int position) throws InterruptedException, IOException, JSONException;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        clickListener = listener;
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder{
        public TextView linePublicNumber;
        public TextView destCode50;
        public TextView expDeptTimeAsString;

        public BusViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            linePublicNumber = itemView.findViewById(R.id.line_number);
            destCode50 = itemView.findViewById(R.id.destCode);
            expDeptTimeAsString = itemView.findViewById(R.id.exp_dept_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            try {
                                listener.onItemClick(position);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    public BusAdapter(ArrayList<Bus> buses){
        this.buses = buses;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item,parent,false);
        BusViewHolder evh = new BusViewHolder(v, clickListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        Bus currentBus = buses.get(position);

        holder.linePublicNumber.setText(currentBus.getLinePublicNumber());
        holder.destCode50.setText(currentBus.get_destinationCode50());
        holder.expDeptTimeAsString.setText(currentBus.getExpectedDepartureTimeAsTimeString());


    }

    @Override
    public int getItemCount() {
        return buses.size();
    }

}
