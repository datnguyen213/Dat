package com.example.weatherforecast.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.weatherforecast.R;
import com.example.weatherforecast.models.DailyForecast;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.DailyViewHolder> {

    private List<DailyForecast> forecastList;

    public DailyForecastAdapter(List<DailyForecast> forecastList) {
        this.forecastList = forecastList;
    }

    public void setData(List<DailyForecast> newData) {
        this.forecastList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily, parent, false); // Đảm bảo file XML đúng tên
        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
        DailyForecast forecast = forecastList.get(position);

        holder.tvDate.setText(forecast.date); // Ví dụ: 2025-04-17
//        holder.tvDescription.setText(forecast.description);
        // Load icon từ URL
        Picasso.get()
                .load(forecast.iconUrl)
                .placeholder(R.drawable.ic_placeholder) // Icon tạm trong khi chờ
                .error(R.drawable.ic_error) // Icon lỗi nếu load fail
                .into(holder.imgIcon);
        holder.tvTempRange.setText(forecast.tempMin + " - " + forecast.tempMax);


    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public static class DailyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTempRange, tvDescription;
        ImageView imgIcon;

        public DailyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDescription = itemView.findViewById(R.id.tv_description);
            imgIcon = itemView.findViewById(R.id.img_weather_icon);
            tvTempRange = itemView.findViewById(R.id.tv_temp_range);
        }
    }
}