package com.kodilla.tripfrontvaadin.pages.login;

import com.kodilla.tripfrontvaadin.AdminConfig;
import com.kodilla.tripfrontvaadin.base.BasePage;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import java.net.URI;

@Route(value = "login")
public class LoginPage extends BasePage {

    public LoginPage() {
        super();
        prepareView();
    }

    private void prepareView() {
        Label loginLbl = new Label("Log in.");
        add(loginLbl);
        Button confirmBtn = new Button("Confirm", e -> confirmLogin());
        emailTF.setErrorMessage("This is not a valid Email!");
        add(emailTF);
        passwordTF.setMinLength(8);
        passwordTF.setErrorMessage("Min length: 8.");
        add(passwordTF);
        add(confirmBtn);

    }

    private void confirmLogin() {
        if (emailTF.isInvalid() || passwordTF.isInvalid()) {
            Notification.show("Bad credentials", 5000, Notification.Position.MIDDLE);
            return;
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/login")
                .queryParam("username", emailTF.getValue())
                .queryParam("password", passwordTF.getValue())
                .build().encode().toUri();
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, null, String.class);
            String key = responseEntity.getHeaders().get("Set-Cookie").get(0);
            String splited = key.split(";")[0].substring(11);
            Cookie cookie = new Cookie("Auth_key", splited);
            cookie.setMaxAge(-1);
            VaadinService.getCurrentResponse().addCookie(cookie);
            getUI().ifPresent(ui -> ui.navigate(""));
        } catch (HttpClientErrorException.Unauthorized e) {
            cookieService.removeCookie();
            Notification.show("Bad credentials", 5000, Notification.Position.MIDDLE);
        }
    }

    private EmailField emailTF = new EmailField("Email");
    private PasswordField passwordTF = new PasswordField("Password");
    private RestTemplate restTemplate = new RestTemplate();
    private AdminConfig adminConfig = new AdminConfig();
}
