package com.datadog.pej.front;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Values {

    private Long id;
    private String quote;

    public Values() {
    }

    public Long getId() {
        return this.id;
    }

    public String getQuote() {
        return this.quote;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    @Override
    public String toString() {
        return "Values{" +
                "id=" + id +
                ", quote='" + quote + '\'' +
                '}';
    }
}