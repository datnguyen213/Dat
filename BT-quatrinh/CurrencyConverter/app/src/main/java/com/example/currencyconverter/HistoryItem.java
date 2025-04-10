package com.example.currencyconverter;

public class HistoryItem {
    private String dateTime;
    private String fromAmount;
    private String toAmount;
    private double exchangeRate;

    public HistoryItem(String dateTime, String fromAmount, String toAmount, double exchangeRate) {
        this.dateTime = dateTime;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
        this.exchangeRate = exchangeRate;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getFromAmount() {
        return fromAmount;
    }

    public String getToAmount() {
        return toAmount;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }
}