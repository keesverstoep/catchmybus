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

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private ArrayList<Stop> stops;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws InterruptedException, IOException, JSONException;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView stationNameTextView;
        public TextView stationTownTextView;
        public TextView stationDistanceTextView;

        public ListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            stationNameTextView = itemView.findViewById(R.id.station_name);
            stationTownTextView = itemView.findViewById(R.id.station_town);
            stationDistanceTextView = itemView.findViewById(R.id.distance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
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

    public ListAdapter(ArrayList<Stop> stops) {
        this.stops = stops;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_item, parent, false);
        ListViewHolder evh = new ListViewHolder(v, clickListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Stop currentStop = stops.get(position);

        holder.stationTownTextView.setText(currentStop.getTown());
        holder.stationNameTextView.setText(currentStop.getStop());
        holder.stationDistanceTextView.setText(currentStop.getDistanceString());
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }
}
