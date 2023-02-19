package com.datadog.pej.back;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class SpringBackApplication {


  private static final Logger log = LoggerFactory.getLogger(SpringBackApplication.class);


  public static void main(String[] args) {
    SpringApplication.run(SpringBackApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplateBuilder().build();
  }


  @Bean
  public CommandLineRunner run() {
    return args -> {
      log.info("test");
    };
  }

}
