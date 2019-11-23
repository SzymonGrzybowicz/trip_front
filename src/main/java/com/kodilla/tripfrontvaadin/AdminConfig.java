package com.kodilla.tripfrontvaadin;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AdminConfig {

    private String apiAddress = "http://localhost:8081";
}
