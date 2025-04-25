package com.example.weatherforecast.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.models.HourlyForecast;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>{
    private List<HourlyForecast> list;

    public HourlyAdapter(List<HourlyForecast> list) {
        this.list = list;
    }

    public void setData(List<HourlyForecast> newData) {
        this.list = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly, parent, false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        HourlyForecast item = list.get(position);
        holder.tvHour.setText(item.hour);
        Picasso.get().load(item.iconUrl).into(holder.imgIcon);
        holder.tvTemp.setText(item.temperature);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class HourlyViewHolder extends RecyclerView.ViewHolder {
        TextView tvHour, tvTemp;
        ImageView imgIcon;

        public HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHour = itemView.findViewById(R.id.tv_hour);
            tvTemp = itemView.findViewById(R.id.tv_temp);
            imgIcon = itemView.findViewById(R.id.img_icon);
        }
    }
}