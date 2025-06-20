/*
 * Copyright (c) 2025. Vladimir E. Koltunov, mtbo.org
 * Please see the AUTHORS file for details.
 * All rights reserved. Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package org.mtbo.rxkafka.producers;

import java.time.Instant;
import java.util.Random;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.mtbo.rxkafka.domain.StockUpdate;
import org.mtbo.rxkafka.domain.TopicConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderResult;

@Component
public class StockPriceProducer {
  public static final String[] STOCKS = {"AAPL", "GOOG", "MSFT", "AMZN", "TSLA"};
  private static final String CURRENCY = "USD";

  private final ReactiveKafkaProducerTemplate<String, StockUpdate> kafkaProducer;
  private final NewTopic topic;
  private final Random random = new Random();

  public StockPriceProducer(
      KafkaProperties properties, @Qualifier(TopicConfig.STOCK_PRICES_IN) NewTopic topic) {
    this.kafkaProducer =
        new ReactiveKafkaProducerTemplate<>(
            SenderOptions.create(properties.buildProducerProperties()));
    this.topic = topic;
  }

  public Flux<SenderResult<Void>> produceStockPrices(int count) {
    return Flux.range(0, count)
        .map(
            i -> {
              String stock = STOCKS[random.nextInt(STOCKS.length)];
              double price = 100 + (200 * random.nextDouble());
              return MessageBuilder.withPayload(
                      new StockUpdate(stock, price, CURRENCY, Instant.now()))
                  .setHeader(MessageHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                  .build();
            })
        .flatMap(
            stock -> {
              var newRecord = new ProducerRecord<>(topic.name(), "STOCKS", stock.getPayload());

              stock
                  .getHeaders()
                  .forEach(
                      (key, value) -> newRecord.headers().add(key, value.toString().getBytes()));

              return kafkaProducer.send(newRecord);
            });
  }
}
