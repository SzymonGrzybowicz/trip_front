package com.kodilla.tripfrontvaadin.pages.createEvent;

import com.kodilla.tripfrontvaadin.base.BasePage;
import com.kodilla.tripfrontvaadin.components.GoogleSearchBox;
import com.kodilla.tripfrontvaadin.domain.Event;
import com.kodilla.tripfrontvaadin.domain.Localization;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Route("createEvent")
public class CreateEventPage extends BasePage {

    public CreateEventPage() {
        super();
        prepareView();
    }

    private void prepareView() {
        Button confirm = new Button("Confirm", e-> confirmCreations());
        Button clear = new Button("Clear", e -> clear());
        date.setMin(LocalDate.now().plusDays(1));
        add(whereSearchBox, priceTA, date);
        priceTA.setErrorMessage("That is not a number!");
        add(time, clear, confirm);
    }

    private void  confirmCreations(){
        if (!whereSearchBox.isEnable() && !priceTA.isInvalid() && !date.isEmpty() && !time.isEmpty()){
            URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/event")
                    .build().encode().toUri();
            HttpEntity entity = cookieService.getEntityWithLogin();
            HttpEntity entity1 = new HttpEntity(buildEvent(), entity.getHeaders());
            try {
                restTemplate.exchange(uri, HttpMethod.POST, entity1, String.class);
                getUI().ifPresent(ui -> ui.navigate("events"));
            }catch (HttpClientErrorException.Forbidden e){
                cookieService.removeCookie();
                getUI().ifPresent(ui -> ui.navigate("login"));
            }
        }
    }

    private Event buildEvent(){
        Localization localization = whereSearchBox.getSelectedLocalization();
        localization.setNumberInTrip(-1);
        LocalDateTime dateTime = date.getValue().atTime(time.getValue());
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        return new Event(localization, date, priceTA.getValue());
    }

    private void clear() {
        whereSearchBox.clear();
        priceTA.clear();
        date.clear();
        time.clear();
    }


    private GoogleSearchBox whereSearchBox = new GoogleSearchBox("Where:");
    private DatePicker date = new DatePicker("Date");
    private TimePicker time = new TimePicker("Time");
    private NumberField priceTA = new NumberField("Price");
}
