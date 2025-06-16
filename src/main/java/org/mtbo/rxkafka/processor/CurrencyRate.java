package org.mtbo.rxkafka.processor;

import reactor.core.publisher.Mono;

public interface CurrencyRate {
  Mono<Double> convertRate(String from, String to, double amount);
}
