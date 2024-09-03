package com.example.trendbarservice.model;

public class TrendBar {
    private double open;
    private double high;
    private double low;
    private double close;
    private final TrendBarPeriod period;
    private final long periodStartTimestamp;

    public TrendBar(long periodStartTimestamp, TrendBarPeriod period) {
        this.periodStartTimestamp = periodStartTimestamp;
        this.period = period;
        open = 0;
        high = 0;
        low = Double.MAX_VALUE;
        close = 0;
    }

    public void update(double price) {
        open = open == 0 ? price : open;
        high = Math.max(high, price);
        low = Math.min(low, price);
        close = price;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public TrendBarPeriod getPeriod() {
        return period;
    }

    public long getPeriodStartTimestamp() {
        return periodStartTimestamp;
    }
}
