package com.theopendle.core.contact;

import org.osgi.service.component.annotations.Component;

@Component(service = ContactService.class, immediate = true)
public class ContactServiceImpl implements ContactService {

    @Override
    public String postContactMessage(final String message) {
        return "Thank you for saying: " + message;
    }
}