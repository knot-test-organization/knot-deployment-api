package com.nttdata.knot.deployapi.Models.Deployment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class App {

    private Boolean enabled;
    private Data data;

    public App(Boolean enabled, Data data) {
        this.enabled = enabled;
        this.data = data;
    }

    public App() {
    }
}

