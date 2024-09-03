package com.example.trendbarservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrendBarTest {

    @Test
    public void testUpdate() {
        TrendBar trendBar = new TrendBar(1000, TrendBarPeriod.M1);

        final double openPrice = 10.5;
        trendBar.update(openPrice);
        assertEquals(openPrice, trendBar.getOpen(), 0.01);
        assertEquals(openPrice, trendBar.getHigh(), 0.01);
        assertEquals(openPrice, trendBar.getLow(), 0.01);
        assertEquals(openPrice, trendBar.getClose(), 0.01);

        final double highPrice = 20;
        trendBar.update(highPrice);
        assertEquals(openPrice, trendBar.getOpen(), 0.01);
        assertEquals(highPrice, trendBar.getHigh(), 0.01);
        assertEquals(openPrice, trendBar.getLow(), 0.01);
        assertEquals(highPrice, trendBar.getClose(), 0.01);

        final double lowPrice = 5;
        trendBar.update(5);
        assertEquals(openPrice, trendBar.getOpen(), 0.01);
        assertEquals(highPrice, trendBar.getHigh(), 0.01);
        assertEquals(lowPrice, trendBar.getLow(), 0.01);
        assertEquals(lowPrice, trendBar.getClose(), 0.01);

        final double closePrice = 6;
        trendBar.update(closePrice);
        assertEquals(openPrice, trendBar.getOpen(), 0.01);
        assertEquals(highPrice, trendBar.getHigh(), 0.01);
        assertEquals(lowPrice, trendBar.getLow(), 0.01);
        assertEquals(closePrice, trendBar.getClose(), 0.01);
    }
}
