package com.kodilla.tripfrontvaadin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Localization {

    @JsonProperty(value = "google_id")
    private String googleId;

    @JsonProperty(value = "main_description")
    private String mainDescription;

    @JsonProperty(value = "secondary_description")
    private String secondaryDescription;

    @Setter
    @JsonProperty(value = "number_in_trip")
    private int numberInTrip;
}
