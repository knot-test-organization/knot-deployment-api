package com.nttdata.knot.deployapi.Models.Deployment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Image {

    private String repository;
    private String tag;
    private String envPath;
    private String nameSpace;

    public Image(String repository, String tag, String envPath, String nameSpace) {
        this.repository = repository;
        this.tag = tag;
        this.envPath = envPath;
        this.nameSpace = nameSpace;
    }
}

