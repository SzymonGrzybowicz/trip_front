package com.kodilla.tripfrontvaadin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Trip {


    public Trip(List<Localization> localizations, Date date, double distance) {
        this.localizations = localizations;
        this.date = date;
        this.distance = distance;
    }

    @JsonProperty("id")
    private Long id;

    @Setter
    @JsonProperty("localizations")
    private List<Localization> localizations;

    @Setter
    private String from;

    @Setter
    private String to;

    @Setter
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    @Setter
    @JsonProperty("distance")
    private double distance;

    @JsonProperty("temperature")
    private Integer temperature;

}
