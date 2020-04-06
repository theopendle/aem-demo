package com.theopendle.core.models.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Country {

    @JsonProperty("countryName")
    private String name;

    private String code;
}