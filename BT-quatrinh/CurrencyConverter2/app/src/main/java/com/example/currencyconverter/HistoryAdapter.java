package com.example.currencyconverter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<ConversionHistory> historyList;

    public HistoryAdapter(List<ConversionHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ConversionHistory history = historyList.get(position);

        holder.dateTextView.setText(history.getDate());
        holder.conversionTextView.setText(String.format("%.2f %s → %.2f %s (Tỷ giá: %.4f)",
                history.getAmount(), history.getFromCurrency(),
                history.getResult(), history.getToCurrency(), history.getRate()));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView conversionTextView;

        HistoryViewHolder(View view) {
            super(view);
            dateTextView = view.findViewById(R.id.dateTextView);
            conversionTextView = view.findViewById(R.id.conversionTextView);
        }
    }
}
