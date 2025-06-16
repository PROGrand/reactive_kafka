/*
 * Copyright (c) 2025. Vladimir E. Koltunov, mtbo.org
 * Please see the AUTHORS file for details.
 * All rights reserved. Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package org.mtbo.rxkafka.consumer;

import jakarta.annotation.PostConstruct;
import java.util.List;
import org.apache.kafka.clients.admin.NewTopic;
import org.mtbo.rxkafka.domain.StockUpdate;
import org.mtbo.rxkafka.domain.TopicConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.util.annotation.NonNull;

@Component
public class StockPriceConsumer {
  private final ReactiveKafkaConsumerTemplate<String, StockUpdate> kafkaConsumerTemplate;

  public StockPriceConsumer(
      @NonNull KafkaProperties properties,
      @Qualifier(TopicConfig.STOCK_PRICES_OUT) NewTopic topic) {
    var receiverOptions =
        ReceiverOptions.<String, StockUpdate>create(properties.buildConsumerProperties())
            .subscription(List.of(topic.name()));
    this.kafkaConsumerTemplate = new ReactiveKafkaConsumerTemplate<>(receiverOptions);
  }

  @PostConstruct
  public void consume() {
    kafkaConsumerTemplate
        .receiveAutoAck()
        .doOnNext(
            consumerRecord -> {
              // simulate processing
              System.out.printf(
                  "received key=%1$s, value=%2$s from topic=%3$s, offset=%4$s, partition=%5$s%n",
                  consumerRecord.key(),
                  consumerRecord.value(),
                  consumerRecord.topic(),
                  consumerRecord.offset(),
                  consumerRecord.partition());
            })
        .doOnError(e -> System.err.printf("Consumer error: %1$s%n", e.toString()))
        .doOnComplete(() -> System.out.println("Consumed all messages"))
        .subscribe();
  }
}
