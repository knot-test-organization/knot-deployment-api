package com.nttdata.knot.deployapi.Models.ComponentPackage;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BBDD {
    
    private boolean enabled;
    private String name;
    private String type;
    private String admin;
    private String adminPass;
    private String version;
    private String tier;
    
}