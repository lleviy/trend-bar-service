package com.example.trendbarservice.dao;

import com.example.trendbarservice.model.TrendBar;
import com.example.trendbarservice.model.TrendBarPeriod;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class TrendBarDaoImpl implements TrendBarDao {
    private final ConcurrentMap<String, ConcurrentMap<TrendBarPeriod, List<TrendBar>>> trendBars;

    public TrendBarDaoImpl() {
        this.trendBars = new ConcurrentHashMap<>();
    }

    public void saveTrendBars(String symbol, TrendBarPeriod period, TrendBar trendBar) {
        ConcurrentMap<TrendBarPeriod, List<TrendBar>> symbolTrendBars = trendBars.computeIfAbsent(symbol, k -> new ConcurrentHashMap<>());
        symbolTrendBars.computeIfAbsent(period, k -> new CopyOnWriteArrayList<>()).add(trendBar);
    }

    public List<TrendBar> getTrendBars(String symbol, TrendBarPeriod period, long fromTimestamp, Long toTimestamp) {
        ConcurrentMap<TrendBarPeriod, List<TrendBar>> symbolTrendBars = trendBars.get(symbol);
        if (symbolTrendBars == null) {
            return Collections.emptyList();
        }
        List<TrendBar> trendBars = symbolTrendBars.get(period);
        if (trendBars == null) {
            return Collections.emptyList();
        }
        return trendBars.stream()
                .filter(tb -> tb.getPeriodStartTimestamp() >= fromTimestamp && (toTimestamp == null || tb.getPeriodStartTimestamp() <= toTimestamp))
                .collect(Collectors.toList());
    }
}
