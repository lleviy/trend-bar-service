package com.example.trendbarservice.service;

import com.example.trendbarservice.dao.TrendBarDao;
import com.example.trendbarservice.model.Quote;
import com.example.trendbarservice.model.TrendBar;
import com.example.trendbarservice.model.TrendBarPeriod;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TrendBarServiceImpl implements TrendBarService {
    private final ConcurrentMap<String, ConcurrentMap<TrendBarPeriod, TrendBar>> trendBars;
    private final TrendBarDao trendBarDao;

    private final long MILLIS_IN_MINUTE = 1000 * 60;
    private final long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
    private final long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;

    public TrendBarServiceImpl(TrendBarDao storage) {
        this.trendBarDao = storage;
        this.trendBars = new ConcurrentHashMap<>();
    }

    public void buildTrendBars(Quote quote) {
        String symbol = quote.symbol();
        long timestamp = quote.timestamp();

        for (TrendBarPeriod period : TrendBarPeriod.values()) {
            long periodStartTimestamp = getPeriodStartTimestamp(timestamp, period);

            ConcurrentMap<TrendBarPeriod, TrendBar> symbolTrendBars = trendBars.computeIfAbsent(symbol, k -> new ConcurrentHashMap<>());
            TrendBar currentTrendBar = symbolTrendBars.computeIfAbsent(period, k -> new TrendBar(periodStartTimestamp, period));

            if (isTreadBarCompleted(currentTrendBar, timestamp)) {
                trendBarDao.saveTrendBars(symbol, period, currentTrendBar);
                symbolTrendBars.remove(period);
                currentTrendBar = symbolTrendBars.computeIfAbsent(period, k -> new TrendBar(periodStartTimestamp, period));
            }

            currentTrendBar.update(quote.price());
        }
    }

    public List<TrendBar> getTrendBars(String symbol, TrendBarPeriod period, long fromTimestamp, Long toTimestamp) {
        return trendBarDao.getTrendBars(symbol, period, fromTimestamp, toTimestamp);
    }

    public List<TrendBar> getTrendBars(String symbol, TrendBarPeriod period, long fromTimestamp) {
        return trendBarDao.getTrendBars(symbol, period, fromTimestamp, System.currentTimeMillis());
    }

    private boolean isTreadBarCompleted(TrendBar currentTrendBar, long timestamp) {
        long periodEndTimestamp = currentTrendBar.getPeriodStartTimestamp() + getPeriodDurationMillis(currentTrendBar.getPeriod());
        return periodEndTimestamp <= timestamp;
    }

    private long getPeriodStartTimestamp(long timestamp, TrendBarPeriod period) {
        return switch (period) {
            case M1 -> timestamp - (timestamp % MILLIS_IN_MINUTE);
            case H1 -> timestamp - (timestamp % MILLIS_IN_HOUR);
            case D1 -> timestamp - (timestamp % MILLIS_IN_DAY);
        };
    }

    private long getPeriodDurationMillis(TrendBarPeriod period) {
        return switch (period) {
            case M1 -> MILLIS_IN_MINUTE;
            case H1 -> MILLIS_IN_HOUR;
            case D1 -> MILLIS_IN_DAY;
        };
    }
}
