package com.kodilla.tripfrontvaadin.pages.myTrips;

import com.kodilla.tripfrontvaadin.base.BasePage;
import com.kodilla.tripfrontvaadin.components.GoogleSearchBox;
import com.kodilla.tripfrontvaadin.domain.Localization;
import com.kodilla.tripfrontvaadin.domain.Trip;
import com.kodilla.tripfrontvaadin.exception.NotAuthorizedException;
import com.kodilla.tripfrontvaadin.service.TripService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Route("myTrips")
public class MyTripsPage extends BasePage {

    public MyTripsPage() {
        super();
        prepareView();
    }

    private void prepareView() {
        createdTrip.setColumns("from", "to", "distance", "date", "temperature");
        createdTrip.addComponentColumn(this::createEditButton);
        createdTrip.addComponentColumn(this::createDeleteButton);
        joinedTrip.setColumns("from", "to", "distance", "date", "temperature");
        joinedTrip.addComponentColumn(this::createDetachButton);
        Label createdLbl = new Label("Created by me:");
        Label joinedLbl = new Label("Joined:");
        newDate.setMin(LocalDate.now().plusDays(1));
        editForm.add(currentStartLocation, newStartLocation, currentDestinationLocation);
        editForm.add(newDestinationLocation, currentDate, newDate, newTime);
        editForm.setVisible(false);
        add(createdLbl, editForm, createdTrip);
        add(joinedLbl, joinedTrip);
        refresh();
    }

    private void refresh() {
        try {
            createdTrip.setItems(tripService.getTripCreatedByUser());
            joinedTrip.setItems(tripService.getTripUserJoined());
        } catch (NotAuthorizedException e) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            cookieService.removeCookie();
        }
    }

    private void onConfirmEdit(Trip trip) {
        List<Localization> localizations = new ArrayList<>();
        if (!newStartLocation.isEnable()) {
            Localization localization = newStartLocation.getSelectedLocalization();
            localization.setNumberInTrip(0);
            localizations.add(localization);
        } else {
            trip.getLocalizations().forEach(
                    l -> {
                        if (l.getNumberInTrip() == 0) {
                            localizations.add(l);
                        }
                    }
            );
        }
        if (!newDestinationLocation.isEnable()) {
            Localization localization = newDestinationLocation.getSelectedLocalization();
            localization.setNumberInTrip(0);
            localizations.add(localization);
        } else {
            trip.getLocalizations().forEach(
                    l -> {
                        if (l.getNumberInTrip() == 1) {
                            localizations.add(l);
                        }
                    }
            );
        }
        trip.setLocalizations(localizations);
        if (!newDate.isEmpty() && newTime.isEmpty()) {
            LocalDateTime dateTime = newDate.getValue().atTime(newTime.getValue());
            Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
            trip.setDate(date);
        }

        HttpEntity entity = cookieService.getEntityWithLogin();
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/trip")
                .build().encode().toUri();
        HttpEntity entitywithBody = new HttpEntity(trip, entity.getHeaders());

        try {
            restTemplate.exchange(uri, HttpMethod.PUT, entitywithBody, Trip[].class);
            Notification.show("OK!", 5000, Notification.Position.MIDDLE);
            editForm.setVisible(false);
        } catch (HttpClientErrorException.Forbidden e) {
            getUI().ifPresent( ui -> ui.navigate("login"));
        } catch (HttpClientErrorException.BadRequest e) {
            Notification.show("Ooops! Something Wrong!", 5000, Notification.Position.MIDDLE);
        }
        refresh();
    }

    private void showEditForm(Trip trip) {
        currentStartLocation.setText(trip.getFrom());
        currentDestinationLocation.setText(trip.getTo());
        currentDate.setText(trip.getDate().toString());
        Button confirmEdit = new Button("Confirm", e -> onConfirmEdit(trip));
        editForm.add(confirmEdit);
        editForm.setVisible(true);
    }

    private Button createEditButton(Trip trip) {
        return new Button("Edit", e -> showEditForm(trip));
    }

    private void deleteTrip(Trip trip) {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/trip/" + trip.getId())
                .build().encode().toUri();
        try {
            restTemplate.exchange(uri, HttpMethod.DELETE, cookieService.getEntityWithLogin(), String.class);
            refresh();
        } catch (HttpClientErrorException.Forbidden e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
        }
    }

    private Button createDeleteButton(Trip trip) {
        return new Button("Delete", e -> deleteTrip(trip));
    }

    private Button createDetachButton(Trip trip) {
        return new Button("Detach", e -> detachTrip(trip));
    }

    private void detachTrip(Trip trip) {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/user/detach/" + trip.getId())
                .build().encode().toUri();
        try {
            restTemplate.exchange(uri, HttpMethod.PUT, cookieService.getEntityWithLogin(), String.class);
            refresh();
        } catch (HttpClientErrorException.Forbidden e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (HttpClientErrorException.BadRequest e) {
            Notification.show("Ooops! Something is wrong! Try Again", 5000, Notification.Position.MIDDLE);
        }
    }

    private TripService tripService = new TripService();
    private Grid<Trip> createdTrip = new Grid<>(Trip.class);
    private Grid<Trip> joinedTrip = new Grid<>(Trip.class);
    private HorizontalLayout editForm = new HorizontalLayout();
    private Label currentStartLocation = new Label("Current From:");
    private GoogleSearchBox newStartLocation = new GoogleSearchBox("New from:");
    private Label currentDestinationLocation = new Label("Current to:");
    private GoogleSearchBox newDestinationLocation = new GoogleSearchBox("New to:");
    private Label currentDate = new Label("Current date:");
    private DatePicker newDate = new DatePicker("New date:");
    private TimePicker newTime = new TimePicker("New Time");
}
