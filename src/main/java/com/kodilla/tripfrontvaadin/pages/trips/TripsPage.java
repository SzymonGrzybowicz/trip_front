package com.kodilla.tripfrontvaadin.pages.trips;

import com.kodilla.tripfrontvaadin.AdminConfig;
import com.kodilla.tripfrontvaadin.base.BasePage;
import com.kodilla.tripfrontvaadin.domain.Trip;
import com.kodilla.tripfrontvaadin.exception.NotAuthorizedException;
import com.kodilla.tripfrontvaadin.service.CookieService;
import com.kodilla.tripfrontvaadin.service.TripService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@Route("trips")
public class TripsPage extends BasePage {

    public TripsPage(TripService tripService) {
        super();
        this.tripService = tripService;
        prepareView();
    }

    private void prepareView() {
        Button createTripBtn = new Button("Create Trip", e -> getUI().ifPresent(ui -> ui.navigate("createTrip")));
        add(createTripBtn);
        grid.setColumns("from", "to", "distance", "date", "temperature");
        grid.addComponentColumn(this::buildJoinButton);
        add(grid);
        setSizeFull();
        refresh();
    }

    private void refresh() {
        try {
            grid.setItems(tripService.getTrips());
        } catch (NotAuthorizedException e) {
            AdminConfig.isAuthorised = false;
        }
    }


    private Button buildJoinButton(Trip trip) {
        Button button = new Button("Join");
        button.addClickListener(e -> joinUserToTrip(trip));
        return button;
    }

    private void joinUserToTrip(Trip trip) {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/user/join/" + trip.getId())
                .build().encode().toUri();

        try {
            RestTemplate restTemplate = new RestTemplate();
            CookieService cookieService = new CookieService();
            restTemplate.exchange(uri, HttpMethod.PUT, cookieService.getEntityWithLogin(), String.class);
            Notification.show("Joined.", 5000, Notification.Position.MIDDLE);
        } catch (HttpClientErrorException.Unauthorized e){
            AdminConfig.isAuthorised = false;
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (HttpClientErrorException.BadRequest e) {
            Notification.show("Ooops, something is wrong.", 5000, Notification.Position.MIDDLE);
        }
    }

    private AdminConfig adminConfig = new AdminConfig();
    private Grid<Trip> grid = new Grid<>(Trip.class);
    private TripService tripService;
}
