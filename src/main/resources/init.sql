/*
 * Copyright (c) 2025. Vladimir E. Koltunov, mtbo.org
 * Please see the AUTHORS file for details.
 * All rights reserved. Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

create database if not exists stockdb;
create table if not exists stockdb.updates
(
    symbol    String,
    price     Double,
    currency  String,
    timestamp DateTime
)
    engine = SummingMergeTree(symbol)
        order by (price, currency, timestamp);

