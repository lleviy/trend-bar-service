package com.example.trendbarservice.service;

import com.example.trendbarservice.dao.TrendBarDao;
import com.example.trendbarservice.model.Quote;
import com.example.trendbarservice.model.TrendBar;
import com.example.trendbarservice.model.TrendBarPeriod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TrendBarServiceAsyncImplTest {

    private static final String TEST_SYMBOL = "EURUSD";
    private TrendBarService trendBarService;
    private TrendBarDao trendBarDao;

    private final long MILLIS_IN_MINUTE = 1000 * 60;
    private final long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
    private final long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;

    @BeforeEach
    void setUp() {
        trendBarDao = Mockito.mock(TrendBarDao.class);
        trendBarService = new TrendBarServiceAsyncImpl(trendBarDao);
    }

    @Test
    void testBuildTrendBars_WhenMultipleTrendBarsAreCompleted() {
        long timestamp = System.currentTimeMillis();
        timestamp = timestamp - (timestamp % MILLIS_IN_MINUTE);

        for (int i = 0; i < 3; i++) {
            trendBarService.buildTrendBars(new Quote(TEST_SYMBOL + i, timestamp, 1.4));
            trendBarService.buildTrendBars(new Quote(TEST_SYMBOL + i, timestamp + 5000, 1.2));
            trendBarService.buildTrendBars(new Quote(TEST_SYMBOL + i, timestamp + MILLIS_IN_MINUTE, 1.5));
            trendBarService.buildTrendBars(new Quote(TEST_SYMBOL + i, timestamp + MILLIS_IN_HOUR, 1.2));
            trendBarService.buildTrendBars(new Quote(TEST_SYMBOL + i, timestamp + MILLIS_IN_DAY, 2.0));
        }

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            for (int i = 0; i < 3; i++) {
                verify(trendBarDao, times(3)).saveTrendBars(eq(TEST_SYMBOL + i), eq(TrendBarPeriod.M1), any(TrendBar.class));
                verify(trendBarDao, times(2)).saveTrendBars(eq(TEST_SYMBOL + i), eq(TrendBarPeriod.H1), any(TrendBar.class));
                verify(trendBarDao, times(1)).saveTrendBars(eq(TEST_SYMBOL + i), eq(TrendBarPeriod.D1), any(TrendBar.class));
            }
        });
    }

    @Test
    void testBuildTrendBars_WhenUpdatedMinutely() {
        long timestamp = System.currentTimeMillis();
        timestamp = timestamp - (timestamp % MILLIS_IN_MINUTE);

        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp, 1.4));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + 5000, 1.2));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + 10000, 2.1));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + 20000, 1.9));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + MILLIS_IN_MINUTE, 1.5));

        ArgumentCaptor<TrendBar> captor = ArgumentCaptor.forClass(TrendBar.class);
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(trendBarDao).saveTrendBars(eq(TEST_SYMBOL), eq(TrendBarPeriod.M1), captor.capture());

            TrendBar trendBar = captor.getValue();
            assertEquals(1.4, trendBar.getOpen());
            assertEquals(1.2, trendBar.getLow());
            assertEquals(2.1, trendBar.getHigh());
            assertEquals(1.9, trendBar.getClose());
        });
    }

    @Test
    void testBuildTrendBars_WhenUpdatedHourly() {
        long timestamp = System.currentTimeMillis();
        timestamp = timestamp - (timestamp % MILLIS_IN_HOUR);

        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp, 1.4));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + MILLIS_IN_MINUTE, 1.2));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + MILLIS_IN_MINUTE * 10, 2.1));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + MILLIS_IN_MINUTE * 40, 1.9));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + MILLIS_IN_HOUR, 1.5));

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(trendBarDao, times(4)).saveTrendBars(eq(TEST_SYMBOL), eq(TrendBarPeriod.M1), any(TrendBar.class));
            ArgumentCaptor<TrendBar> captor = ArgumentCaptor.forClass(TrendBar.class);
            verify(trendBarDao).saveTrendBars(eq(TEST_SYMBOL), eq(TrendBarPeriod.H1), captor.capture());

            TrendBar trendBar = captor.getValue();
            assertEquals(1.4, trendBar.getOpen());
            assertEquals(1.2, trendBar.getLow());
            assertEquals(2.1, trendBar.getHigh());
            assertEquals(1.9, trendBar.getClose());
        });
    }

    @Test
    void testBuildTrendBars_WhenUpdatedDaily() {
        long timestamp = System.currentTimeMillis();
        timestamp = timestamp - (timestamp % MILLIS_IN_DAY);

        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp, 1.4));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + MILLIS_IN_HOUR, 1.2));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + MILLIS_IN_HOUR * 10, 2.1));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + MILLIS_IN_HOUR * 11, 1.9));
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + MILLIS_IN_DAY, 1.5));

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(trendBarDao, times(4)).saveTrendBars(eq(TEST_SYMBOL), eq(TrendBarPeriod.M1), any(TrendBar.class));
            verify(trendBarDao, times(4)).saveTrendBars(eq(TEST_SYMBOL), eq(TrendBarPeriod.H1), any(TrendBar.class));
            ArgumentCaptor<TrendBar> captor = ArgumentCaptor.forClass(TrendBar.class);
            verify(trendBarDao).saveTrendBars(eq(TEST_SYMBOL), eq(TrendBarPeriod.D1), captor.capture());

            TrendBar trendBar = captor.getValue();
            assertEquals(1.4, trendBar.getOpen());
            assertEquals(1.2, trendBar.getLow());
            assertEquals(2.1, trendBar.getHigh());
            assertEquals(1.9, trendBar.getClose());
        });
    }

    @Test
    public void testGetTrendBars() {
        // Arrange
        String symbol = TEST_SYMBOL;
        TrendBarPeriod period = TrendBarPeriod.H1;
        long fromTimestamp = 1643723400L;
        Long toTimestamp = 1643726800L;

        // Act
        trendBarService.getTrendBars(symbol, period, fromTimestamp, toTimestamp);

        // Assert
        verify(trendBarDao).getTrendBars(symbol, period, fromTimestamp, toTimestamp);
    }

    @Test
    public void testGetTrendBars_WithoutToTimestamp() {
        // Arrange
        String symbol = TEST_SYMBOL;
        TrendBarPeriod period = TrendBarPeriod.H1;
        long fromTimestamp = 1643723400L;

        // Act
        trendBarService.getTrendBars(symbol, period, fromTimestamp);

        // Assert
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(trendBarDao).getTrendBars(eq(symbol), eq(period), eq(fromTimestamp), captor.capture());
        long now = System.currentTimeMillis();
        assertTrue(now - captor.getValue() < MILLIS_IN_MINUTE);
    }
}
