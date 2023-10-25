package com.nttdata.knot.deployapi.Models.Deployment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Secret {
    private App app;

    public Secret(App app) {
        this.app = app;
    }
}
