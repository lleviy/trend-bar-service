package com.example.trendbarservice.dao;

import com.example.trendbarservice.model.TrendBar;
import com.example.trendbarservice.model.TrendBarPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrendBarDaoImplTest {
    private static final String TEST_SYMBOL = "EURUSD";
    private final long MILLIS_IN_MINUTE = 1000 * 60;
    private TrendBarDao trendBarDao;

    @BeforeEach
    public void setUp() {
        trendBarDao = new TrendBarDaoImpl();
    }

    @Test
    public void testSaveTrendBars() {
        long now = System.currentTimeMillis();
        TrendBarPeriod period = TrendBarPeriod.M1;
        TrendBar trendBar = new TrendBar(now, period);
        trendBar.update(456.34);

        trendBarDao.saveTrendBars(TEST_SYMBOL, period, trendBar);

        long fromTimestamp = now - MILLIS_IN_MINUTE;
        long toTimestamp = now + MILLIS_IN_MINUTE;

        var trendBars = trendBarDao.getTrendBars(TEST_SYMBOL, period, fromTimestamp, toTimestamp);
        assertEquals(1, trendBars.size());
        assertEquals(trendBar, trendBars.get(0));
    }

    @Test
    public void testGetTrendBars_NoData() {
        TrendBarDaoImpl trendBarDao = new TrendBarDaoImpl();
        TrendBarPeriod period = TrendBarPeriod.M1;
        long fromTimestamp = 1000L;
        Long toTimestamp = 2000L;

        List<TrendBar> trendBars = trendBarDao.getTrendBars(TEST_SYMBOL, period, fromTimestamp, toTimestamp);
        assertTrue(trendBars.isEmpty());
    }

    @Test
    public void testGetTrendBars_WithSpecifiedPeriod() {
        long currentTimestamp = System.currentTimeMillis();
        TrendBarPeriod period = TrendBarPeriod.M1;
        for (int i = 0; i < 10; i++) {
            currentTimestamp += MILLIS_IN_MINUTE;
            TrendBar trendBar = new TrendBar(currentTimestamp, period);
            trendBar.update(i);
            trendBarDao.saveTrendBars(TEST_SYMBOL, period, trendBar);
        }

        List<TrendBar> trendBars = trendBarDao.getTrendBars(TEST_SYMBOL, period, currentTimestamp - MILLIS_IN_MINUTE * 2, currentTimestamp);
        assertEquals(3, trendBars.size());
        List<TrendBar> allTrendBars = trendBarDao.getTrendBars(TEST_SYMBOL, period, currentTimestamp - MILLIS_IN_MINUTE * 10, currentTimestamp);
        assertEquals(10, allTrendBars.size());
    }

}
