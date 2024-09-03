package com.example.trendbarservice.service;

import com.example.trendbarservice.dao.TrendBarDao;
import com.example.trendbarservice.model.Quote;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class TrendBarServiceAsyncImpl extends TrendBarServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(TrendBarServiceAsyncImpl.class);

    private final ExecutorService executorService;
    private final BlockingQueue<Quote> quoteQueue;

    public TrendBarServiceAsyncImpl(TrendBarDao trendBarDao) {
        super(trendBarDao);
        this.executorService = Executors.newSingleThreadExecutor();
        this.quoteQueue = new LinkedBlockingQueue<>();
        startQuoteProcessing();
    }

    @Override
    public void buildTrendBars(Quote quote) {
        quoteQueue.add(quote);
    }

    private void startQuoteProcessing() {
        executorService.submit(() -> {
            while (true) {
                try {
                    Quote quote = quoteQueue.take();
                    super.buildTrendBars(quote);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Quote processing interrupted, {}", e.getMessage(), e);
                }
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
