package com.nttdata.knot.deployapi.Models.Enviroments;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Envs {

    private boolean enabled;
    private String envPath;
    private String nameSpace;
    private String version;

    public Envs(boolean enabled, String envPath, String nameSpace, String version) {
        this.enabled = enabled;
        this.envPath = envPath;
        this.nameSpace = nameSpace;
        this.version = version;
    }
}
