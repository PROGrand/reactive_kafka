package org.mtbo.rxkafka.processor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class MarketConfig {
  @Bean
  CurrencyRate currencyRate() {
    return (from, to, amount) -> Mono.just(amount * 1.1);
  }
}
