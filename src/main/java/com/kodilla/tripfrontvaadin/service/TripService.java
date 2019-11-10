package com.kodilla.tripfrontvaadin.service;

import com.kodilla.tripfrontvaadin.AdminConfig;
import com.kodilla.tripfrontvaadin.domain.Trip;
import com.kodilla.tripfrontvaadin.exception.NotAuthorizedException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Component
public class TripService {

    public List<Trip> getTrips() throws NotAuthorizedException {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/trip")
                .build().encode().toUri();

        return sendRequest(uri);
    }

    public List<Trip> getTripCreatedByUser() throws NotAuthorizedException {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/trip/createdByUser")
                .build().encode().toUri();
        return sendRequest(uri);
    }

    public List<Trip> getTripUserJoined() throws NotAuthorizedException {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/trip/userJoined")
                .build().encode().toUri();
        return sendRequest(uri);
    }

    private List<Trip> sendRequest(URI uri) throws NotAuthorizedException {
        HttpEntity entity = cookieService.getEntityWithLogin();

        try{
            ResponseEntity<Trip[]> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity,Trip[].class);
            Trip[] response = responseEntity.getBody();
            for (int i = 0; i < response.length; i++) {
                response[i].setFrom(response[i].getLocalizations().get(0).getMainDescription() + ", " + response[i].getLocalizations().get(0).getSecondaryDescription());
                response[i].setTo(response[i].getLocalizations().get(response[i].getLocalizations().size() - 1).getMainDescription()
                        + ", " +response[i].getLocalizations().get(response[i].getLocalizations().size() - 1).getMainDescription());
            }
            return Arrays.asList(response);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new NotAuthorizedException();
        }
    }

    private RestTemplate restTemplate = new RestTemplate();
    private AdminConfig adminConfig = new AdminConfig();
    private CookieService cookieService = new CookieService();
}
