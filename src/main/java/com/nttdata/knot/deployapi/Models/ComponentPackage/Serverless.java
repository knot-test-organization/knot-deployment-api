package com.nttdata.knot.deployapi.Models.ComponentPackage;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Serverless {
    @JsonProperty("createFunctionApp")
    private Boolean createFunctionApp;

    @JsonProperty("applicationStack")
    private String applicationStack;

    public Serverless() {
    }

    public Serverless(Boolean createFunctionApp, String applicationStack) {
        this.createFunctionApp = createFunctionApp;
        this.applicationStack = applicationStack;
    }
}
