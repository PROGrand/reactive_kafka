/*
 * Copyright (c) 2025. Vladimir E. Koltunov, mtbo.org
 * Please see the AUTHORS file for details.
 * All rights reserved. Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package org.mtbo.rxkafka.processor;

import reactor.core.publisher.Mono;

public interface CurrencyRate {
  Mono<Double> convertRate(String from, String to, double amount);
}
