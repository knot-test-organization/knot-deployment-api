package com.nttdata.knot.deployapi.Controllers;


import com.nttdata.knot.deployapi.Models.Deployment.Deploy;
import com.nttdata.knot.deployapi.Models.GithubPackage.GithubFileRequest.DeleteGithubFileRequest;

import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nttdata.knot.deployapi.Interfaces.IDeployService;
import com.nttdata.knot.deployapi.Models.ComponentPackage.Component;

@RestController
@RequestMapping("/deploy")
public class DeployController {

    private IDeployService deployService;
    private static final Logger logger = LoggerFactory.getLogger(Deploy.class);


    @Autowired
    public DeployController(IDeployService deployService) {
        this.deployService = deployService;
       
    }

    @PostMapping("/{org}/{area}/{product}")
    public  ResponseEntity<Mono<Deploy>> create(@PathVariable String org, @PathVariable String area, @PathVariable String product, @RequestBody Component component) throws JsonProcessingException {
         var deploy = deployService.createDeployAsync(org, area, product, component);
        return ResponseEntity.ok(deploy);
    }
    
    
    @DeleteMapping("/{org}/{area}/{product}/{name}/{enviroment}")
    public  ResponseEntity<Mono<DeleteGithubFileRequest>> delete(@PathVariable String org, @PathVariable String area, @PathVariable String product, @PathVariable String name, @PathVariable String enviroment) throws JsonProcessingException {
          
        var delete = deployService.deleteDeployAsync(org, area, product, name, enviroment);
        logger.info("The component {} is being deleted", name);

        return ResponseEntity.ok(delete);
    }

    @PutMapping("/{org}/{area}/{product}")
    public  ResponseEntity<Mono<Deploy>> update (@PathVariable String org, @PathVariable String area, @PathVariable String product, @RequestBody Component component) throws JsonProcessingException {
          
        var deploy = deployService.updateDeployAsync(org, area, product, component);
        logger.info("The component {} is being Updated", component.getName());

        return ResponseEntity.ok(deploy);
    }


}

