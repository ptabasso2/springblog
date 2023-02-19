package com.datadog.pej.back;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;


@RestController
public class BasicController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicController.class);
    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    RestTemplate restTemplate;


    public CountDownLatch getLatch() {
        return latch;
    }

    @RequestMapping("/downstream")
    public Quote downstream() {

        String anime = restTemplate.getForObject("http://colormind.io/list/", String.class);
        LOGGER.info(anime);

        String google = restTemplate.getForObject("https://www.google.fr", String.class);
        LOGGER.info(google);


        Value value = new Value();
        value.setId((long) (5 + (Math.random() * ((10 - 5) + 1))));
        value.setQuote("Alea jacta est");
        Quote quote = new Quote();
        quote.setValue(value);
        quote.setType("success");

        getLatch().countDown();

        return quote;
    }

}