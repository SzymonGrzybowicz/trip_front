package com.kodilla.tripfrontvaadin.pages.registration;

import com.kodilla.tripfrontvaadin.AdminConfig;
import com.kodilla.tripfrontvaadin.base.BasePage;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Route("registration")
public class RegistrationPage extends BasePage {

    public RegistrationPage() {
        super();
        prepareView();
    }

    private void prepareView() {
        Button confirmBtn = new Button("Confirm", e -> confirmRegistration());
        passwordField.setMinLength(8);
        passwordField.setErrorMessage("Min lenght: 8");
        emailTF.setErrorMessage("This is not a Email!");
        this.add(emailTF);
        this.add(passwordField);
        this.add(confirmBtn);
    }

    private void confirmRegistration() {
        if (emailTF.isInvalid() || passwordField.isInvalid()) {
            return;
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/user")
                .build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject personJsonObject = new JSONObject();

        try {
            personJsonObject.put("username", emailTF.getValue());
            personJsonObject.put("password", passwordField.getValue());
            headers.add("Content-Type", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(personJsonObject.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                getUI().ifPresent(ui -> ui.navigate(""));
            }
        } catch (JSONException e) {
            Notification.show("Ooops! Try again!");
        } catch (HttpClientErrorException.Conflict e) {
            Notification.show("That User Already Exist!", 5000, Notification.Position.MIDDLE);
        }
    }

    private EmailField emailTF = new EmailField("Email");
    private PasswordField passwordField = new PasswordField("Password");
    private RestTemplate restTemplate = new RestTemplate();
    private AdminConfig adminConfig = new AdminConfig();
}
