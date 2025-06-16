package org.mtbo.rxkafka.controller;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import org.mtbo.rxkafka.domain.StockUpdate;
import org.mtbo.rxkafka.producers.StockPriceProducer;
import org.mtbo.rxkafka.repo.ClickHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class StocksApi {
  private final ClickHouseRepository repository;
  private final StockPriceProducer producer;

  @Autowired
  public StocksApi(ClickHouseRepository repository, StockPriceProducer producer) {
    this.repository = repository;
    this.producer = producer;
  }

  @PostMapping("/produce")
  public Mono<Long> produce() {
    return producer.produceStockPrices(5).count();
  }

  @GetMapping("/stock-prices-out")
  public Flux<StockUpdate> getAvgStockPrices(
      @RequestParam("from") @NotNull Instant from, @RequestParam("to") @NotNull Instant to) {
    if (from.isAfter(to)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'from' must come before 'to'");
    }

    return repository.findMinuteAvgStockUpdate(from, to);
  }
}
