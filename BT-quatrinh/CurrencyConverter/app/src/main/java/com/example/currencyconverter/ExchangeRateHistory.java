package com.example.currencyconverter;

import java.util.Date;

public class ExchangeRateHistory {
    private Date date;
    private double rate;

    public ExchangeRateHistory(Date date, double rate) {
        this.date = date;
        this.rate = rate;
    }

    public Date getDate() {
        return date;
    }

    public double getRate() {
        return rate;
    }
}