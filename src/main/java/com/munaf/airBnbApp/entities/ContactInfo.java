package com.munaf.airBnbApp.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ContactInfo {

    private String email;

    private String phoneNumber;

    private String completeAddress;

    private String location;

}
