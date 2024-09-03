package com.example.trendbarservice.service;

import com.example.trendbarservice.dao.TrendBarDao;
import com.example.trendbarservice.model.Quote;
import com.example.trendbarservice.model.TrendBar;
import com.example.trendbarservice.model.TrendBarPeriod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = TrendBarServiceImplIntegrationTest.class)
@ComponentScan(basePackages = "com.example.trendbarservice")
class TrendBarServiceImplIntegrationTest {

    @Autowired
    private TrendBarService trendBarService;

    @Autowired
    private TrendBarDao trendBarDao;

    private static final String TEST_SYMBOL = "EURUSD";
    private final long MILLIS_IN_MINUTE = 1000 * 60;
    private final long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
    private final long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;

    @Test
    @DirtiesContext
    void testBuildTrendBarsAndGetIt() {
        long timestamp = System.currentTimeMillis();
        timestamp = timestamp - (timestamp % MILLIS_IN_MINUTE);
        Quote quote = new Quote(TEST_SYMBOL, timestamp, 100);

        trendBarService.buildTrendBars(quote);
        trendBarService.buildTrendBars(new Quote(TEST_SYMBOL, timestamp + MILLIS_IN_MINUTE, 200));

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            List<TrendBar> trendBars = trendBarDao.getTrendBars(TEST_SYMBOL, TrendBarPeriod.M1, System.currentTimeMillis() - MILLIS_IN_DAY, System.currentTimeMillis() + MILLIS_IN_DAY);
            assertEquals(1, trendBars.size());
            TrendBar trendBar = trendBars.get(0);
            assertEquals(trendBar.getPeriod(), TrendBarPeriod.M1);
            assertEquals(trendBar.getOpen(), 100);
            assertEquals(trendBar.getClose(), 100);
        });
    }

}
