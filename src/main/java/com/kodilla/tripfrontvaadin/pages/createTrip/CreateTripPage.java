package com.kodilla.tripfrontvaadin.pages.createTrip;

import com.kodilla.tripfrontvaadin.AdminConfig;
import com.kodilla.tripfrontvaadin.base.BasePage;
import com.kodilla.tripfrontvaadin.components.GoogleSearchBox;
import com.kodilla.tripfrontvaadin.domain.Localization;
import com.kodilla.tripfrontvaadin.domain.Trip;
import com.kodilla.tripfrontvaadin.service.CookieService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Route("createTrip")
public class CreateTripPage extends BasePage {

    public CreateTripPage() {
        super();
        prepareView();
    }

    private void prepareView() {
        distance.setEnabled(false);
        fromSearchBox.setEnableChangeListener(e -> {
            if (isCheckDistanceAvailable()) {
                distance.setValue(getDistance());
            }
        });
        toSearchBox.setEnableChangeListener(e -> {
            if (isCheckDistanceAvailable()) {
                distance.setValue(getDistance());
            }
        });
        Button confirm = new Button("Confirm", e-> confirmCreations());
        Button clear = new Button("Clear", e -> clear());
        date.setMin(LocalDate.now().plusDays(1));
        add(fromSearchBox, toSearchBox, distance, date);
        add(time, clear, confirm);
    }

    private void  confirmCreations(){
        if (isCheckDistanceAvailable() && !distance.isEmpty() && !date.isEmpty() && !time.isEmpty()){
            URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/trip")
                    .build().encode().toUri();
            HttpEntity entity = cookieService.getEntityWithLogin();
            HttpEntity entity1 = new HttpEntity(buildTrip(), entity.getHeaders());
            try {
                restTemplate.exchange(uri, HttpMethod.POST, entity1, String.class);
                getUI().ifPresent(ui -> ui.navigate("trips"));
            }catch (HttpClientErrorException.Forbidden e){
                cookieService.removeCookie();
            }
        }
    }

    private Trip buildTrip(){
        List<Localization> localizations = new ArrayList<>();
        Localization fromLocalization = fromSearchBox.getSelectedLocalization();
        fromLocalization.setNumberInTrip(0);
        localizations.add(fromLocalization);
        Localization toLocalization = toSearchBox.getSelectedLocalization();
        toLocalization.setNumberInTrip(1);
        localizations.add(toLocalization);
        LocalDateTime dateTime = date.getValue().atTime(time.getValue());
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        String distanceString = distance.getValue().split(" km")[0];
        double distanceDouble = Double.parseDouble(distanceString);
        return new Trip(localizations, date, distanceDouble);
    }

    private String getDistance() {
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() +
                "/google/distance/" +
                fromSearchBox.getSelectedLocalization().getGoogleId() +
                "/" +
                toSearchBox.getSelectedLocalization().getGoogleId())
                .build().encode().toUri();
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, cookieService.getEntityWithLogin(), String.class);
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            cookieService.removeCookie();
            getUI().ifPresent(ui -> ui.navigate("login"));
        }
        return "";
    }

    private void clear() {
        fromSearchBox.clear();
        toSearchBox.clear();
        distance.clear();
        date.clear();
        time.clear();
    }

    private boolean isCheckDistanceAvailable(){
        if (!fromSearchBox.isEnable() && !toSearchBox.isEnable()) {
            return true;
        }
        return false;
    }


    private TextField distance = new TextField("Distance:");
    private GoogleSearchBox fromSearchBox = new GoogleSearchBox("From:");
    private GoogleSearchBox toSearchBox = new GoogleSearchBox("To:");
    private DatePicker date = new DatePicker("Date");
    private TimePicker time = new TimePicker("Time");
}
