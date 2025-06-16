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

