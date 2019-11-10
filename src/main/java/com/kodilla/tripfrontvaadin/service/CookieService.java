package com.kodilla.tripfrontvaadin.service;

import com.vaadin.flow.server.VaadinService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
public class CookieService {

    public HttpEntity getEntityWithLogin() {
        HttpHeaders headers = new HttpHeaders();
        String key = "";
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        if (cookies == null) {
            return new HttpEntity(null);
        }
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals("Auth_key")) {
                if (cookies[i].getValue().length() == 0) {
                    return new HttpEntity(null);
                }
                key = cookies[i].getValue();
            }
        }
        headers.add("Content-Type", "application/json");
        headers.add("Cookie", "JSESSIONID=" + key);
        return new HttpEntity(headers);
    }

    public boolean isCookieAdded() {
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("Auth_key")) {
                    if (cookies[i].getValue().length() == 0) {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void removeCookie() {
        VaadinService.getCurrentResponse().addCookie(new Cookie("Auth_key", ""));
    }
}
