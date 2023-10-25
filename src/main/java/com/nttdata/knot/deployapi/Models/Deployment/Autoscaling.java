package com.nttdata.knot.deployapi.Models.Deployment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Autoscaling {

    private Boolean enabled;
    private int minReplicas;
    private int maxReplicas;
    private int targetAverageUtilization;
    private String targetMemoryAverageUtilization;

    public Autoscaling(Boolean enabled, int minReplicas, int maxReplicas, int targetAverageUtilization, String targetMemoryAverageUtilization) {
        this.enabled = enabled;
        this.minReplicas = minReplicas;
        this.maxReplicas = maxReplicas;
        this.targetAverageUtilization = targetAverageUtilization;
        this.targetMemoryAverageUtilization = targetMemoryAverageUtilization;
    }

}