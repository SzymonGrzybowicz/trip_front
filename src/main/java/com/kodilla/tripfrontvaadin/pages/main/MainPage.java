package com.kodilla.tripfrontvaadin.pages.main;

import com.kodilla.tripfrontvaadin.base.BasePage;
import com.kodilla.tripfrontvaadin.service.CookieService;
import com.vaadin.flow.router.Route;

@Route("")
public class MainPage extends BasePage {

    public MainPage(CookieService cookieService)
    {
        super();
        createPage();
    }

    private void createPage() {
        this.add("Some nice text about My application ;) \n " +
                "sometimes after login reload page is necessary"); //todo
    }
}
