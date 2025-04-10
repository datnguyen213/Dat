package com.example.currencyconverter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private ArrayList<HistoryItem> historyList;

    public HistoryAdapter(ArrayList<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);
        holder.dateTimeTextView.setText(item.getDateTime());
        holder.fromAmountTextView.setText(item.getFromAmount());
        holder.toAmountTextView.setText(item.getToAmount());
        holder.rateTextView.setText(String.format("Tỷ giá: %.6f", item.getExchangeRate()));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTimeTextView;
        public TextView fromAmountTextView;
        public TextView toAmountTextView;
        public TextView rateTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTimeTextView = itemView.findViewById(R.id.history_date);
            fromAmountTextView = itemView.findViewById(R.id.history_from);
            toAmountTextView = itemView.findViewById(R.id.history_to);
            rateTextView = itemView.findViewById(R.id.history_rate);
        }
    }
}