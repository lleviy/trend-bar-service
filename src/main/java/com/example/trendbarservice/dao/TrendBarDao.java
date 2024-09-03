package com.example.trendbarservice.dao;

import com.example.trendbarservice.model.TrendBar;
import com.example.trendbarservice.model.TrendBarPeriod;

import java.util.List;

public interface TrendBarDao {
    void saveTrendBars(String symbol, TrendBarPeriod period, TrendBar trendBar);
    List<TrendBar> getTrendBars(String symbol, TrendBarPeriod period, long fromTimestamp, Long toTimestamp);
}
