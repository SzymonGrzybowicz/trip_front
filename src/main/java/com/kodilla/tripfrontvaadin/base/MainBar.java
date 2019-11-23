package com.kodilla.tripfrontvaadin.base;

import com.kodilla.tripfrontvaadin.AdminConfig;
import com.kodilla.tripfrontvaadin.service.CookieService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.web.client.RestTemplate;

public class MainBar extends HorizontalLayout {

    public MainBar() {
        prepareView();
    }

    private void prepareView() {
        setWidth("100%");
        setSpacing(true);
        Button loginBtn = new Button("Log in", e -> getUI().ifPresent(ui -> ui.navigate("login")));
        Button registerBtn = new Button("Create User", e -> getUI().ifPresent(ui -> ui.navigate("registration")));
        Button tripsBtn = new Button("Trips", e -> getUI().ifPresent(ui -> ui.navigate("trips")));
        Button eventsBtn = new Button("Events", e -> getUI().ifPresent(ui -> ui.navigate("events")));
        Button userBtn = new Button("My Page", e -> getUI().ifPresent(ui -> ui.navigate("userPage")));
        Button logoutBtn = new Button("Logout", e -> logout());
        add(tripsBtn, eventsBtn, loginBtn, registerBtn, logoutBtn, userBtn);
        loginBtn.getStyle().set("margin-left", "auto");
        logoutBtn.getStyle().set("margin-left", "auto");
        setVerticalComponentAlignment(Alignment.END, loginBtn);
        setVerticalComponentAlignment(Alignment.END, registerBtn);
        setVerticalComponentAlignment(Alignment.END, userBtn);
        setVerticalComponentAlignment(Alignment.END, logoutBtn);
        if (cookieService.isCookieAdded()) {
            loginBtn.setVisible(false);
            registerBtn.setVisible(false);
        } else {
            tripsBtn.setVisible(false);
            eventsBtn.setVisible(false);
            logoutBtn.setVisible(false);
            userBtn.setVisible(false);
        }
    }

    private void logout() {
        restTemplate.getForObject(adminConfig.getApiAddress() + "/logout", String.class);
        cookieService.removeCookie();
        getUI().ifPresent(ui -> ui.navigate(""));
    }

    private AdminConfig adminConfig = new AdminConfig();
    private RestTemplate restTemplate = new RestTemplate();
    private CookieService cookieService = new CookieService();
}
