package ru.practicum.ewm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.StatsClient;

@Configuration
public class StatsClientConfig {

    @Bean
    public StatsClient statsClient() {
        return new StatsClient("http://stats-server:9090");
    }
}
