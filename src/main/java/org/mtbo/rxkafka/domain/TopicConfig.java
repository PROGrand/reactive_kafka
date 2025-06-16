package org.mtbo.rxkafka.domain;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

  public static final String STOCK_PRICES_IN = "stock-prices-in";
  public static final String STOCK_PRICES_OUT = "stock-prices-out";

  @Bean(STOCK_PRICES_IN)
  public NewTopic stockPricesInTopic() {
    return new NewTopic(STOCK_PRICES_IN, 1, (short) 1);
  }

  @Bean(STOCK_PRICES_OUT)
  public NewTopic stockPricesOutTopic() {
    return new NewTopic(STOCK_PRICES_OUT, 1, (short) 1);
  }
}
