package com.theopendle.core.models.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Continent {

    @JsonProperty("continentName")
    private String name;

    private List<Country> countries;
}