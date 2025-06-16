package org.mtbo.rxkafka.repo;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.mtbo.rxkafka.domain.StockUpdate;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ClickHouseRepository {

  final ConnectionFactory connectionFactory;

  public ClickHouseRepository(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  public Mono<Boolean> saveStockUpdate(StockUpdate stockUpdate) {
    //    return Mono.just(true);
    return Mono.from(connectionFactory.create())
        .flatMapMany(conn -> insert(stockUpdate, conn))
        .then(Mono.just(true));
  }

  private Publisher<? extends Result> insert(StockUpdate stockUpdate, Connection conn) {
    return conn.createStatement(
            "insert into stockdb.updates values (:symbol, :price, :currency, :timestamp)")
        .bind("symbol", stockUpdate.symbol())
        .bind("price", stockUpdate.price())
        .bind("currency", stockUpdate.currency())
        .bind("timestamp", toLocalDateTime(stockUpdate.timestamp()))
        .execute();
  }

  LocalDateTime toLocalDateTime(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
  }

  public Flux<StockUpdate> findMinuteAvgStockUpdate(Instant from, Instant to) {
    return Mono.from(connectionFactory.create())
        .flatMapMany(
            conn ->
                conn.createStatement(
                        "select * from stockdb.updates where timestamp > :from and timestamp < :to")
                    .bind("from", toLocalDateTime(from))
                    .bind("to", toLocalDateTime(to))
                    .execute())
        .flatMap(
            result ->
                result.map(
                    (row, rowMetadata) ->
                        new StockUpdate(
                            row.get("symbol", String.class),
                            Objects.requireNonNull(row.get("price", Double.class)),
                            row.get("currency", String.class),
                            row.get("timestamp", Instant.class))));
  }
}
