package com.kodilla.tripfrontvaadin.pages.user;

import com.kodilla.tripfrontvaadin.AdminConfig;
import com.kodilla.tripfrontvaadin.base.BasePage;
import com.kodilla.tripfrontvaadin.domain.Trip;
import com.kodilla.tripfrontvaadin.exception.NotAuthorizedException;
import com.kodilla.tripfrontvaadin.service.TripService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Route("userPage")
public class UserPage extends BasePage {

    public UserPage() {
        super();
        prepareView();
    }

    private void prepareView() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        Button changePasswordBtn = new Button("Change Password", e -> changePassword());
        horizontalLayout.add(changePasswordBtn);
        Button deleteUserBtn = new Button("Delete User", e -> deleteUser());
        horizontalLayout.add(deleteUserBtn);
        createdTrip.setColumns("from", "to", "distance", "date", "temperature");
        createdTrip.addComponentColumn(this::createEditButton);
        createdTrip.addComponentColumn(this::createDeleteButton);
        joinedTrip.setColumns("from", "to", "distance", "date", "temperature");
        joinedTrip.addComponentColumn(this::createDetachButton);
        Label createdLbl = new Label("Created by me:");
        Label joinedLbl = new Label("Joined:");
        add(createdLbl, createdTrip);
        add(joinedLbl, joinedTrip);
        refresh();
    }

    private void refresh(){
        try {
            createdTrip.setItems(tripService.getTripCreatedByUser());
            joinedTrip.setItems(tripService.getTripUserJoined());
        }catch (NotAuthorizedException e) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            AdminConfig.isAuthorised = false;
        }
    }

    private void deleteUser() {
        //todo
    }

    private void changePassword() {
        //Todo
    }

    private Button createEditButton(Trip trip){
        return new Button("Edit");
        //TODO
    }

    private void deleteTrip(Trip trip) {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/trip/" + trip.getId())
                .build().encode().toUri();
        try {
            restTemplate.exchange(uri, HttpMethod.DELETE, cookieService.getEntityWithLogin(), String.class);
            refresh();
        }catch (HttpClientErrorException.Forbidden e){
            AdminConfig.isAuthorised = false;
            getUI().ifPresent(ui -> ui.navigate("login"));
        }
    }

    private Button createDeleteButton(Trip trip) {
        return new Button("Delete", e -> deleteTrip(trip));
    }

    private Button createDetachButton(Trip trip){
        return new Button("Detach");
        //TODO
    }

    private TripService tripService = new TripService();
    private Grid<Trip> createdTrip = new Grid<>(Trip.class);
    private Grid<Trip> joinedTrip = new Grid<>(Trip.class);
}
