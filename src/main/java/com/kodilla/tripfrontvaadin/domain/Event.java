package com.kodilla.tripfrontvaadin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Event {


    public Event(Localization localization, Date date, double price) {
        this.localization = localization;
        this.date = date;
    }

    @JsonProperty("id")
    private Long id;

    @JsonProperty("localization")
    private Localization localization;

    @Setter
    private String where;

    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    @JsonProperty("temperature")
    private Integer temperature;

    @JsonProperty("price")
    private double price;
}

