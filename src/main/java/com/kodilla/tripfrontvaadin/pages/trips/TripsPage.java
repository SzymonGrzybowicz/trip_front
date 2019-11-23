package com.kodilla.tripfrontvaadin.pages.trips;

import com.kodilla.tripfrontvaadin.base.BasePage;
import com.kodilla.tripfrontvaadin.components.GoogleSearchBox;
import com.kodilla.tripfrontvaadin.domain.Trip;
import com.kodilla.tripfrontvaadin.exception.NotAuthorizedException;
import com.kodilla.tripfrontvaadin.service.CookieService;
import com.kodilla.tripfrontvaadin.service.TripService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;


@Route("trips")
public class TripsPage extends BasePage {

    public TripsPage() {
        super();
        prepareView();
    }

    private void prepareView() {
        Button createTripBtn = new Button("Create Trip", e -> getUI().ifPresent(ui -> ui.navigate("createTrip")));
        add(createTripBtn);
        distance.setItems(Arrays.asList(10l, 20l, 50l, 100l));
        Button search = new Button("Search", e -> onSearch());
        Button clear = new Button("Clear", e -> {
            localization.clear();
            refresh();
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(localization, distance, search, clear);
        add(horizontalLayout);
        grid.setColumns("from", "to", "distance", "date", "temperature");
        grid.addComponentColumn(this::buildJoinButton);
        add(grid);
        setSizeFull();
        refresh();
    }

    private void onSearch() {
        try {
            grid.setItems(tripService.getTripsInRadiusOfLocation(localization.getSelectedLocalization(), distance.getValue()));
        } catch (NotAuthorizedException e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
        }
    }

    private void refresh() {
        try {
            grid.setItems(tripService.getTrips());
        } catch (NotAuthorizedException e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
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
        } catch (HttpClientErrorException.Unauthorized e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (HttpClientErrorException.BadRequest e) {
            Notification.show("Ooops, something is wrong.", 5000, Notification.Position.MIDDLE);
        }
    }

    private Grid<Trip> grid = new Grid<>(Trip.class);
    private TripService tripService = new TripService();
    private GoogleSearchBox localization = new GoogleSearchBox("Localization:");
    private ComboBox<Long> distance = new ComboBox<>();
}
