package org.mtbo.rxkafka.domain;

public record StockUpdate(
    String symbol, double price, String currency, java.time.Instant timestamp) {}
