package com.example.currencyconverter;

public class ConversionHistory {
    private String date;
    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double result;
    private double rate;

    public ConversionHistory(String date, String fromCurrency, String toCurrency,
                             double amount, double result, double rate) {
        this.date = date;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.result = result;
        this.rate = rate;
    }

    // Getter methods
    public String getDate() { return date; }
    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public double getAmount() { return amount; }
    public double getResult() { return result; }
    public double getRate() { return rate; }
}