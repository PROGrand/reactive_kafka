/*
 * Copyright (c) 2025. Vladimir E. Koltunov, mtbo.org
 * Please see the AUTHORS file for details.
 * All rights reserved. Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package org.mtbo.rxkafka;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;

@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class, R2dbcDataAutoConfiguration.class})
@EnableWebFlux
public class RxkafkaApplication {

  public static void main(String[] args) throws InterruptedException {

    CountDownLatch countDownLatch = new CountDownLatch(2);
    final var counter = Flux.interval(Duration.ofMillis(100)).take(10);

    final var s1 =
        counter
            .doOnComplete(countDownLatch::countDown)
            .subscribe(aLong -> System.out.println("1 counter: " + aLong));

    Thread.sleep(500);
    final var s2 =
        counter
            .doOnComplete(countDownLatch::countDown)
            .subscribe(aLong -> System.out.println("2 counter: " + aLong));

    countDownLatch.await();

    s1.dispose();
    s2.dispose();

    SpringApplication.run(RxkafkaApplication.class, args);
  }
}
