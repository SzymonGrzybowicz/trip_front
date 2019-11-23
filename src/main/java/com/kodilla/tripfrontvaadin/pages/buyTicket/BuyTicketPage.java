package com.kodilla.tripfrontvaadin.pages.buyTicket;

import com.kodilla.tripfrontvaadin.base.BasePage;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Route("buyTicket")
public class BuyTicketPage extends BasePage implements HasUrlParameter<Long> {

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.eventID = parameter;
    }

    public BuyTicketPage() {
        super();
        prepareView();
    }

    private void prepareView() {
        Label label =  new Label("To buy ticket send a SMS to number XXX-XXX-XXX saying \"This is good App\".");
        Label label1 = new Label("For testing app \"1234\" code is correct");
        Button submit = new Button("Confirm", e -> buyTicket());
        add(label, label1);
        add(numberField, submit);
    }

    private void buyTicket() {
        if (numberField.isInvalid()) {
            return;
        }
        HttpEntity entity = cookieService.getEntityWithLogin();
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() +
                "user/buyTicket/" + eventID +
                "/" + numberField.getValue().intValue())
                .build().encode().toUri();

        try{
            restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
            Notification.show("OK!", 5000, Notification.Position.MIDDLE);
            getUI().ifPresent( ui -> ui.navigate("myEvents"));
        } catch (HttpClientErrorException.Forbidden e) {
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (HttpClientErrorException.BadRequest e) {
            Notification.show("Sms Code is Wrong!", 5000, Notification.Position.MIDDLE);
        }
    }


    private NumberField numberField = new NumberField("SMS code");
    private Long eventID;
}
