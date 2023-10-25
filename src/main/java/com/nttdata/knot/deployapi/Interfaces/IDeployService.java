package com.nttdata.knot.deployapi.Interfaces;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.nttdata.knot.deployapi.Models.ComponentPackage.Component;

import com.nttdata.knot.deployapi.Models.Deployment.Deploy;
import com.nttdata.knot.deployapi.Models.GithubPackage.GithubFileRequest.DeleteGithubFileRequest;

import reactor.core.publisher.Mono;

public interface IDeployService {
    

    Mono<Deploy> createDeployAsync(String org, String area, String product, Component component) throws JsonProcessingException;

    Mono<DeleteGithubFileRequest> deleteDeployAsync(String org, String area, String product, String name, String enviroment);

    Mono<Deploy> updateDeployAsync(String org, String area, String product, Component component) throws JsonProcessingException ;


    
}
