/*
 * Copyright (c) 2025. Vladimir E. Koltunov, mtbo.org
 * Please see the AUTHORS file for details.
 * All rights reserved. Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

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
