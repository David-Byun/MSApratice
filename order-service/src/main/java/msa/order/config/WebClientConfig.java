package msa.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    //webflux pom.xml 필요
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
