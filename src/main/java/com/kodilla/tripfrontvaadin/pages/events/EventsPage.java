package com.kodilla.tripfrontvaadin.pages.events;

import com.kodilla.tripfrontvaadin.base.BasePage;
import com.kodilla.tripfrontvaadin.components.GoogleSearchBox;
import com.kodilla.tripfrontvaadin.domain.Event;
import com.kodilla.tripfrontvaadin.exception.NotAuthorizedException;
import com.kodilla.tripfrontvaadin.service.EventService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;

import java.util.Arrays;

@Route("events")
public class EventsPage extends BasePage {

    public EventsPage() {
        super();
        prepareView();
    }

    private void prepareView() {
        Button createTripBtn = new Button("Create Event", e -> getUI().ifPresent(ui -> ui.navigate("createEvent")));
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
        grid.setColumns("where", "date", "temperature", "price");
        grid.addComponentColumn(this::buildBuyButton);
        add(grid);
        setSizeFull();
        refresh();
    }

    private void refresh() {
        try {
            grid.setItems(eventService.getEvents());
        } catch (NotAuthorizedException e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
        }
    }

    private void onSearch() {
        try {
            grid.setItems(eventService.getEventsInRadiusOfLocation(localization.getSelectedLocalization(), distance.getValue()));
        } catch (NotAuthorizedException e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
        }
    }

    private Button buildBuyButton(Event event) {
        Button button = new Button("Buy Ticket");
        button.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("buyTicket/" + event.getId())));
        return button;
    }

    private Grid<Event> grid = new Grid<>(Event.class);
    private EventService eventService = new EventService();
    private GoogleSearchBox localization = new GoogleSearchBox("Localization:");
    private ComboBox<Long> distance = new ComboBox<>();
}
