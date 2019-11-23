package com.kodilla.tripfrontvaadin.pages.myEvents;

import com.kodilla.tripfrontvaadin.base.BasePage;
import com.kodilla.tripfrontvaadin.domain.Event;
import com.kodilla.tripfrontvaadin.exception.NotAuthorizedException;
import com.kodilla.tripfrontvaadin.service.EventService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Route("myEvents")
public class MyEventsPage extends BasePage {

    public MyEventsPage() {
        super();
        prepareView();
    }

    private void prepareView() {
        createdEvent.setColumns("where", "date", "temperature", "price");
        createdEvent.addComponentColumn(this::createDeleteButton);
        joinedEvent.setColumns("where", "date", "temperature", "price");
        Label createdLbl = new Label("Created by me:");
        Label joinedLbl = new Label("Joined:");
        add(createdLbl, createdEvent);
        add(joinedLbl, joinedEvent);
        refresh();
    }

    private void refresh() {
        try {
            createdEvent.setItems(eventService.getEventsCreatedByUser());
            joinedEvent.setItems(eventService.getEventUserJoined());
        } catch (NotAuthorizedException e) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            cookieService.removeCookie();
        }
    }

    private void deleteEvent(Event event) {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/event/" + event.getId())
                .build().encode().toUri();
        try {
            restTemplate.exchange(uri, HttpMethod.DELETE, cookieService.getEntityWithLogin(), String.class);
            refresh();
        } catch (HttpClientErrorException.Forbidden e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
        }
    }

    private Button createDeleteButton(Event event) {
        return new Button("Delete", e -> deleteEvent(event));
    }

    private EventService eventService = new EventService();
    private Grid<Event> createdEvent = new Grid<>(Event.class);
    private Grid<Event> joinedEvent = new Grid<>(Event.class);
}
