package com.kodilla.tripfrontvaadin.service;

import com.kodilla.tripfrontvaadin.AdminConfig;
import com.kodilla.tripfrontvaadin.domain.Event;
import com.kodilla.tripfrontvaadin.domain.Localization;
import com.kodilla.tripfrontvaadin.exception.NotAuthorizedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventService {

    public List<Event> getEvents() throws NotAuthorizedException {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/event")
                .build().encode().toUri();

        return sendRequest(uri);
    }

    public List<Event> getEventsCreatedByUser() throws NotAuthorizedException {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/event/createdByUser")
                .build().encode().toUri();
        return sendRequest(uri);
    }

    public List<Event> getEventUserJoined() throws NotAuthorizedException {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/event/userJoined")
                .build().encode().toUri();
        return sendRequest(uri);
    }

    public List<Event> getEventsInRadiusOfLocation(Localization selectedLocalization, Long value) throws NotAuthorizedException {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/event/" +
                selectedLocalization.getGoogleId() + "/" + value).build().encode().toUri();
        return sendRequest(uri);
    }

    private List<Event> sendRequest(URI uri) throws NotAuthorizedException {
        HttpEntity entity = cookieService.getEntityWithLogin();

        try{
            ResponseEntity<Event[]> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, Event[].class);
            Event[] response = responseEntity.getBody();
            if (response != null) {
                for (int i = 0; i < response.length; i++) {
                    response[i].setWhere(response[i].getLocalization().getMainDescription() + ", " +
                            response[i].getLocalization().getSecondaryDescription());
                }
                return Arrays.asList(response);
            }
            return new ArrayList<>();
        } catch (HttpClientErrorException.Forbidden e) {
            throw new NotAuthorizedException();
        }
    }

    private RestTemplate restTemplate = new RestTemplate();
    private AdminConfig adminConfig = new AdminConfig();
    private CookieService cookieService = new CookieService();
}
