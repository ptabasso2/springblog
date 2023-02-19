package com.datadog.pej.front;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote {

    private String type;
    private Values values;

    public Quote() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Values getValue() {
        return values;
    }

    public void setValue(Values values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "type='" + type + '\'' +
                ", values=" + values +
                '}';
    }
}
