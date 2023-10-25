package com.nttdata.knot.deployapi.Models.Deployment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Deploy {
    private String name;
    private Image image;
    private int replicaCount;
    private Autoscaling autoscaling;
    private Istio istio;
    private Secret secret;
    private String technology;

    public Deploy(String name, Image image, int replicaCount, Autoscaling autoscaling, Istio istio, Secret secret, String technology) {
        this.name = name;
        this.image = image;
        this.replicaCount = replicaCount;
        this.autoscaling = autoscaling;
        this.istio = istio;
        this.secret = secret;
        this.technology = technology;
    }

    public Deploy() {

    }
}
