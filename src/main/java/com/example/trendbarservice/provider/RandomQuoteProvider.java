package com.example.trendbarservice.provider;

import com.example.trendbarservice.model.Quote;

import java.util.Random;

public class RandomQuoteProvider implements QuoteProvider {
    private final Random random;

    public RandomQuoteProvider() {
        this.random = new Random();
    }

    @Override
    public Quote getQuote() {
        StringBuilder symbolBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            char c = (char) ('A' + random.nextInt(26));
            symbolBuilder.append(c);
        }
        String symbol = symbolBuilder.toString();
        long timestamp = System.currentTimeMillis() - random.nextInt(100000);
        double price = random.nextDouble() * 100;
        return new Quote(symbol, timestamp, price);
    }
}
