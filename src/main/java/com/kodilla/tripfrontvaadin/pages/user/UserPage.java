package com.kodilla.tripfrontvaadin.pages.user;

import com.kodilla.tripfrontvaadin.base.BasePage;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
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
        Button changePasswordBtn = new Button("Change Password", e -> showChangePasswordForm());
        add(changePasswordBtn);
        currentPasswordTA.setVisible(false);
        currentPasswordTA.setMinLength(8);
        newPasswordTA.setErrorMessage("Min length: 8");
        newPasswordTA.setVisible(false);
        newPasswordTA.setMinLength(8);
        newPasswordTA.setErrorMessage("Min length: 8");
        confirmPasswordChangeBtn.setVisible(false);
        add(currentPasswordTA);
        add(newPasswordTA);
        add(confirmPasswordChangeBtn);
        Button deleteUserBtn = new Button("Delete User", e -> deleteUser());
        add(deleteUserBtn);
        Button myTrips = new Button("My Trips", e -> getUI().ifPresent(ui -> ui.navigate("myTrips")));
        add(myTrips);
        Button myEvents = new Button("My Events", e -> getUI().ifPresent(ui -> ui.navigate("myEvents")));
        add(myEvents);
    }

    private void deleteUser() {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/user")
                .build().encode().toUri();
        try {
            restTemplate.exchange(uri, HttpMethod.DELETE, cookieService.getEntityWithLogin(), String.class);
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate(""));
        } catch (HttpClientErrorException.Forbidden e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (HttpClientErrorException.BadRequest e) {
            Notification.show("Ooops! Something Wrong. Try Again", 5000, Notification.Position.MIDDLE);
        }
    }

    private void showChangePasswordForm() {
        currentPasswordTA.setVisible(true);
        newPasswordTA.setVisible(true);
        confirmPasswordChangeBtn.setVisible(true);
    }

    private void changePassword() {
        if (currentPasswordTA.isInvalid() || newPasswordTA.isInvalid()) return;
        String currentPassword = currentPasswordTA.getValue();
        String newPassword = newPasswordTA.getValue();
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() +
                "/user/" + newPassword +
                "/" + currentPassword
        )
                .build().encode().toUri();
        try {
            restTemplate.exchange(uri, HttpMethod.PUT, cookieService.getEntityWithLogin(), String.class);
            currentPasswordTA.setVisible(false);
            newPasswordTA.setVisible(false);
            confirmPasswordChangeBtn.setVisible(false);
            Notification.show("OK!", 5000, Notification.Position.MIDDLE);
        } catch (HttpClientErrorException.Forbidden e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (HttpClientErrorException.BadRequest e) {
            Notification.show("Current password not match! Try again.", 5000, Notification.Position.MIDDLE);
        }
    }

    private PasswordField currentPasswordTA = new PasswordField("Current Password");
    private PasswordField newPasswordTA = new PasswordField("New Password");
    private Button confirmPasswordChangeBtn = new Button("Confirm", e -> changePassword());
}
