package com.kodilla.tripfrontvaadin.components;

import com.kodilla.tripfrontvaadin.AdminConfig;
import com.kodilla.tripfrontvaadin.domain.Localization;
import com.kodilla.tripfrontvaadin.service.CookieService;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class GoogleSearchBox extends HorizontalLayout {

    public GoogleSearchBox(String label) {
        textField = new TextField(label);
        prepareView();
    }

    public void setEnableChangeListener(EnableChangeListener listener){
        enableChangeListener = listener;
    }

    public boolean isEnable(){
        return textField.isEnabled();
    }


    private void prepareView() {
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.addValueChangeListener(event -> {
            if (event.getValue().length() > 2) {
                getSuggestions(event.getValue());
                propositions.setItems(getSuggestions(event.getValue()));
            }
        });
        propositions.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                setSelectedItem(event.getValue());
                textField.setValue(event.getValue());
                textField.setEnabled(false);
                if (enableChangeListener != null) {
                    enableChangeListener.onEnableChange(false);
                }
            }
            propositions.setVisible(false);
        });
        add(textField);
        add(propositions);
    }

    private void setSelectedItem(String name) {
        for (int i = 0; i < localizations.length; i++) {
            if ((localizations[i].getMainDescription() + ", " + localizations[i].getSecondaryDescription()).equals(name)) {
                selectedLocalization = localizations[i];
                break;
            }
        }
    }

    private List<String> getSuggestions(String input) {

        HttpEntity entity = cookieService.getEntityWithLogin();
        URI uri = UriComponentsBuilder.fromHttpUrl(adminConfig.getApiAddress() + "/google/suggestion/" + input)
                .build().encode().toUri();

        localizations = restTemplate.exchange(uri, HttpMethod.GET, entity, Localization[].class).getBody();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < localizations.length; i++) {
            result.add(localizations[i].getMainDescription() + ", " + localizations[i].getSecondaryDescription());
        }
        return result;
    }


    public void clear() {
        selectedLocalization = null;
        localizations = null;
        textField.setEnabled(true);
        textField.clear();
        propositions.clear();
    }

    @Getter
    private Localization selectedLocalization;
    private EnableChangeListener enableChangeListener;
    private Localization[] localizations;
    private RestTemplate restTemplate = new RestTemplate();
    private AdminConfig adminConfig = new AdminConfig();
    private TextField textField;
    private ListBox<String> propositions = new ListBox<>();
    private CookieService cookieService = new CookieService();
}

