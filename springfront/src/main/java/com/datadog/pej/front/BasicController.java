package com.datadog.pej.front;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BasicController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicController.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("#{environment['url'] ?: 'http://localhost:8088'}")
    private String url;

    @RequestMapping("/upstream")
    public String upstream() {
        Quote quote = restTemplate.getForObject(url + "/downstream", Quote.class);
        LOGGER.info("Downstream call successful:  " + quote.toString());
        return quote.toString()+"\n";
    }

}