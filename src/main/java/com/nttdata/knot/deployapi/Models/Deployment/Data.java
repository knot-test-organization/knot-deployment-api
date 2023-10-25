package com.nttdata.knot.deployapi.Models.Deployment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Data {

    private String type;
    private String tier;
    private String dbVersion;
    private String name;
    private String user;
    private String password;

    public Data() {
    }
}
