package com.example.trendbarservice.provider;

import com.example.trendbarservice.model.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomQuoteProviderTest {

    private QuoteProvider quoteProvider;

    @BeforeEach
    void setUp() {
        quoteProvider = new RandomQuoteProvider();
    }

    @Test
    void getQuote() {
        Quote quote = quoteProvider.getQuote();

        assertEquals(6, quote.symbol().length());
        assertTrue(quote.price() > 0);
        assertTrue(quote.timestamp() > 0);
    }
}
