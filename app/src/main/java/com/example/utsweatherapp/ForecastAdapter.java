package com.example.utsweatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<MainActivity.ForecastItem> forecastList;

    public ForecastAdapter(List<MainActivity.ForecastItem> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        MainActivity.ForecastItem item = forecastList.get(position);

        holder.tvDay.setText(item.getDay());
        holder.tvMaxTemp.setText(Math.round(item.getMaxTemp()) + item.getTempUnit());
        holder.tvMinTemp.setText(Math.round(item.getMinTemp()) + item.getTempUnit());
        holder.tvCondition.setText(item.getCondition());

        Picasso.get().load(item.getIconUrl()).into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvMaxTemp, tvMinTemp, tvCondition;
        ImageView ivIcon;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvMaxTemp = itemView.findViewById(R.id.tvMaxTemp);
            tvMinTemp = itemView.findViewById(R.id.tvMinTemp);
            tvCondition = itemView.findViewById(R.id.tvCondition);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}