package com.example.trendbarservice.service;

import com.example.trendbarservice.model.Quote;
import com.example.trendbarservice.model.TrendBar;
import com.example.trendbarservice.model.TrendBarPeriod;

import java.util.List;

public interface TrendBarService {
    void buildTrendBars(Quote quote);
    List<TrendBar> getTrendBars(String symbol, TrendBarPeriod period, long fromTimestamp, Long toTimestamp);
    List<TrendBar> getTrendBars(String symbol, TrendBarPeriod period, long fromTimestamp);
}
