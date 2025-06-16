package org.mtbo.rxkafka.processor;

import java.util.function.Function;
import org.mtbo.rxkafka.domain.StockUpdate;
import org.mtbo.rxkafka.repo.ClickHouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class StockPriceProcessor {
  private static final String USD = "USD";
  private static final String EUR = "EUR";
  private static final Logger log = LoggerFactory.getLogger(StockPriceProcessor.class);

  @Bean
  public Function<Flux<Message<StockUpdate>>, Flux<Message<StockUpdate>>> processStockPrices(
      ClickHouseRepository repository, CurrencyRate currencyRate) {
    return stockPrices ->
        stockPrices
            .flatMapSequential(
                message -> {
                  StockUpdate stockUpdate = message.getPayload();
                  return repository
                      .saveStockUpdate(stockUpdate)
                      .flatMap(
                          success ->
                              Boolean.TRUE.equals(success) ? Mono.just(stockUpdate) : Mono.empty())
                      .flatMap(stock -> currencyRate.convertRate(USD, EUR, stock.price()))
                      .map(newPrice -> convertPrice(stockUpdate, newPrice))
                      .map(
                          priceInEuro -> {
                            return MessageBuilder.withPayload(priceInEuro)
                                .setHeader(KafkaHeaders.KEY, priceInEuro.symbol())
                                .copyHeaders(message.getHeaders())
                                .build();
                          })
                      .doOnError(
                          throwable -> {
                            log.error("StockPrice process error", throwable);
                          });
                })
            .doOnError(
                throwable -> {
                  log.error("StockPrice process top error", throwable);
                });
  }

  private StockUpdate convertPrice(StockUpdate stockUpdate, double newPrice) {

    return new StockUpdate(stockUpdate.symbol(), newPrice, EUR, stockUpdate.timestamp());
  }
}
