package com.nttdata.knot.deployapi.Models;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Codespace {

   
    private String username;
    private String repositoryId;
    private String branch;
    private String devContainer;
    private String region;
    private String machineType;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Codespace codespace = (Codespace) obj;
        return Objects.equals(username, codespace.username) &&
               Objects.equals(repositoryId, codespace.repositoryId) &&
               Objects.equals(devContainer, codespace.devContainer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, repositoryId, devContainer);
    }
}