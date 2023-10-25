package com.nttdata.knot.deployapi.Models.Deployment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Istio {
    
    private Boolean enabled;

    public Istio(Boolean enabled) {
        this.enabled = enabled;
    }
}

