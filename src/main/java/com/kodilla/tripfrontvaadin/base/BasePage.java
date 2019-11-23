package com.kodilla.tripfrontvaadin.base;

import com.kodilla.tripfrontvaadin.AdminConfig;
import com.kodilla.tripfrontvaadin.pages.main.MainPage;
import com.kodilla.tripfrontvaadin.pages.login.*;
import com.kodilla.tripfrontvaadin.pages.registration.*;
import com.kodilla.tripfrontvaadin.service.CookieService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.springframework.web.client.RestTemplate;

public abstract class BasePage extends VerticalLayout implements BeforeEnterObserver {

    public BasePage() {
        MainBar mainBar = new MainBar();
        add(mainBar);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (this instanceof MainPage || this instanceof LoginPage || this instanceof RegistrationPage) {
            return;
        }
        if (!cookieService.isCookieAdded()) {
            event.forwardTo("login");
        }
    }

    protected AdminConfig adminConfig = new AdminConfig();
    protected RestTemplate restTemplate = new RestTemplate();
    protected CookieService cookieService = new CookieService();
}
