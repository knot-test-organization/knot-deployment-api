package com.nttdata.knot.deployapi.Services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.nttdata.knot.deployapi.Models.Deployment.*;
import com.nttdata.knot.deployapi.Models.Enviroments.Envs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.nttdata.knot.deployapi.Interfaces.IDeployService;
import com.nttdata.knot.deployapi.Interfaces.IGithubService;
import com.nttdata.knot.deployapi.Models.ComponentPackage.Component;
import com.nttdata.knot.deployapi.Models.ComponentPackage.Env;
import com.nttdata.knot.deployapi.Models.GithubPackage.GithubFileRequest.Committer;
import com.nttdata.knot.deployapi.Models.GithubPackage.GithubFileRequest.CreateGithubFileRequest;
import com.nttdata.knot.deployapi.Models.GithubPackage.GithubFileRequest.DeleteGithubFileRequest;
import com.nttdata.knot.deployapi.Models.GithubPackage.GithubFileResponse.GetGithubFileResponse;

import reactor.core.publisher.Mono;

@Service
public class DeployService implements IDeployService {

        private String repoName = "knot-onboarding-resources";
        private IGithubService githubService;
        private final Logger logger = LoggerFactory.getLogger(DeployService.class);

        public DeployService(IGithubService githubService) {
                this.githubService = githubService;
        }

        @Override
        public Mono<Deploy> createDeployAsync(String org, String area, String product, Component component) throws JsonProcessingException {

                // Initiate the Deploy object attributes
                App app = null;
                int replicaCount = component.isHa() ? 1 : 2;
                int minReplicas = component.isHa() ? component.getMinReplicas() : 0;
                int maxReplicas = component.isHa() ? component.getMaxReplicas() : 0;
                int targetAverageUtilization = component.isHa() ? component.getTargetAverageUtilization() : 0;
                String targetMemoryAverageUtilization = null;
                if (component.isHa() && component.getTargetMemoryAverageUtilization() != null) {
                        targetMemoryAverageUtilization = component.getTargetMemoryAverageUtilization() + "Mi";
                }

                if (component.getBbdd() != null && component.getBbdd().isEnabled()) {
                        Base64.Encoder encoder = Base64.getEncoder();
                        Function<String, String> encode = s -> s == null ? null
                                        : encoder.encodeToString(s.getBytes(StandardCharsets.UTF_8));

                        String dbType = encode.apply(component.getBbdd().getType());
                        String dbTier = encode.apply(component.getBbdd().getTier());
                        String dbVersion = encode.apply(component.getBbdd().getVersion());
                        String dbName = encode.apply(component.getBbdd().getName());
                        String dbUser = encode.apply(component.getBbdd().getAdmin());
                        String dbPassword = encode.apply(component.getBbdd().getAdminPass());

                        Data data = new Data();
                        data.setType(dbType);
                        data.setTier(dbTier);
                        data.setDbVersion(dbVersion);
                        data.setName(dbName);
                        data.setUser(dbUser);
                        data.setPassword(dbPassword);
                        // set the App class
                        app = new App(component.getBbdd().isEnabled(), data);
                }

                // Initialize list of Environments
                List<Envs> environments = component.getEnvironments().stream()
                                .map(environment -> new Envs(environment.isEnabled(), environment.getEnvPath(),
                                                environment.getNameSpace(), environment.getVersion()))
                                .collect(Collectors.toList());

                // populate the Deploy Object
                Deploy deploy = new Deploy(component.getId(),
                                new Image(component.getId() + ".azurecr.io/" + component.getId(),
                                                environments.get(0).getVersion(),
                                                environments.get(0).getEnvPath(),
                                                environments.get(0).getNameSpace()),
                                replicaCount,
                                new Autoscaling(component.isHa(),
                                                minReplicas, maxReplicas, targetAverageUtilization,
                                                targetMemoryAverageUtilization),
                                new Istio(component.isEdge()),
                                new Secret(app),
                                component.getTechnology());

                // prepare the verticals values commit
                var values_deploy = prepareValueForCommit(component, deploy);

                // push the values of each enviroment to the repository
                for (Env environment : component.getEnvironments()) {
                        if (environment.isEnabled()) {
                                this.githubService.createGithubFileAsync(values_deploy, repoName,
                                "products/" + org + "/" + area + "/" + product + "/" + component.getId() + "/deployment/" + environment.getEnvPath() + "/values.yaml")
                                                .block();
                        }
                }
                return Mono.just(deploy);
        }

        @Override
        public Mono<DeleteGithubFileRequest> deleteDeployAsync(String org, String area, String product, String ComponentName, String enviroment) {

                // get the file to delete
                var valuesFile = this.githubService.getGithubFileAsync(repoName,
                                "products/" + org + "/" + area + "/" + product + "/" + ComponentName + "/deployment/" + enviroment + "/values.yaml")
                                .block();

                // set the commit
                Committer committer = new Committer();
                committer.setEmail("41898282+github-actions[bot]@users.noreply.github.com");
                committer.setName("github-actions[bot]");

                DeleteGithubFileRequest deleteGithubFileRequest = new DeleteGithubFileRequest();
                deleteGithubFileRequest
                                .setMessage("Removing Deployment vertical into a Component, with name " + ComponentName);
                deleteGithubFileRequest.setCommitter(committer);
                deleteGithubFileRequest.setSha(valuesFile.getSha());

                // delete the file
                this.githubService.deleteGithubFileAsync(deleteGithubFileRequest,
                                repoName,
                                "products/" + org + "/" + area + "/" + product + "/" + ComponentName + "/deployment/" + enviroment + "/values.yaml")
                                .block();

                return Mono.just(deleteGithubFileRequest);
        }

        @Override
        public Mono<Deploy> updateDeployAsync(String org, String area, String product, Component component) throws JsonProcessingException {
                // Initiate the Deploy object attributes
                App app = null;
                int replicaCount = component.isHa() ? 1 : 2;
                int minReplicas = component.isHa() ? component.getMinReplicas() : 0;
                int maxReplicas = component.isHa() ? component.getMaxReplicas() : 0;
                int targetAverageUtilization = component.isHa() ? component.getTargetAverageUtilization() : 0;
                String targetMemoryAverageUtilization = null;
                if (component.isHa() && component.getTargetMemoryAverageUtilization() != null) {
                        targetMemoryAverageUtilization = component.getTargetMemoryAverageUtilization() + "Mi";
                }

                if (component.getBbdd() != null && component.getBbdd().isEnabled()) {
                        Base64.Encoder encoder = Base64.getEncoder();
                        Function<String, String> encode = s -> encoder
                                        .encodeToString(s.getBytes(StandardCharsets.UTF_8));

                        String dbType = encode.apply(component.getBbdd().getType());
                        String dbTier = encode.apply(component.getBbdd().getTier());
                        String dbVersion = encode.apply(component.getBbdd().getVersion());
                        String dbName = encode.apply(component.getBbdd().getName());
                        String dbUser = encode.apply(component.getBbdd().getAdmin());
                        String dbPassword = encode.apply(component.getBbdd().getAdminPass());
                        Data data = new Data();
                        data.setType(dbType);
                        data.setTier(dbTier);
                        data.setDbVersion(dbVersion);
                        data.setName(dbName);
                        data.setUser(dbUser);
                        data.setPassword(dbPassword);
                        // set the App class
                        app = new App(component.getBbdd().isEnabled(), data);
                }

                

                // initiate Deploy object
                Deploy deploy = null;
                for (Env environment : component.getEnvironments()) {
                        if (environment.isEnabled()) {
                                GetGithubFileResponse valuesFile = null;
                                // populate the Deploy Object
                                deploy = new Deploy(component.getId(),
                                                new Image(component.getId() + ".azurecr.io/" + component.getId(),
                                                                environment.getVersion(),
                                                                environment.getEnvPath(),
                                                                environment.getNameSpace()),
                                                replicaCount,
                                                new Autoscaling(component.isHa(),
                                                                minReplicas, maxReplicas, targetAverageUtilization,
                                                                targetMemoryAverageUtilization),
                                                new Istio(component.isEdge()),
                                                new Secret(app),
                                                component.getTechnology());

                                try {
                                        // get the file to update
                                        valuesFile = this.githubService.getGithubFileAsync(repoName,
                                                        "products/" + org + "/" + area + "/" + product + "/" + component.getId() + "/deployment/" + environment.getEnvPath() + "/values.yaml")
                                                        .block();

                                } catch (WebClientResponseException.NotFound ex) {

                                        logger.error("File not found: " + ex.getMessage());
                                }

                                // prepare the verticals values commit ans set the SHA
                                var values_deploy = prepareValueForCommit(component, deploy);

                                if (valuesFile != null) {

                                        values_deploy.setSha(valuesFile.getSha());
                                }
                                // push the values to the repository
                                this.githubService.createGithubFileAsync(values_deploy, repoName,
                                                "products/" + org + "/" + area + "/" + product + "/" + component.getId() + "/deployment/" + environment.getEnvPath() + "/values.yaml")
                                                .block();

                        }
                }
                return Mono.just(deploy);
        }

        // serialize content of values and prepare a commit
        private CreateGithubFileRequest prepareValueForCommit(Component component, Object vertical)
                        throws JsonProcessingException {
                YAMLFactory yamlFactory = new YAMLFactory();
                yamlFactory.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);
                ObjectMapper objectMapper = new ObjectMapper(yamlFactory);

                String verticalInBase64String = Base64.getEncoder()
                                .encodeToString(objectMapper
                                                .writeValueAsString(vertical).getBytes(StandardCharsets.UTF_8));

                Committer committer = new Committer();
                committer.setEmail("41898282+github-actions[bot]@users.noreply.github.com");
                committer.setName("github-actions[bot]");

                CreateGithubFileRequest createGithubFileRequest = new CreateGithubFileRequest();
                createGithubFileRequest.setMessage("Add new Deployment vertical into a Component, with name " + component.getId());
                createGithubFileRequest.setCommitter(committer);
                createGithubFileRequest.setContent(verticalInBase64String);

                return createGithubFileRequest;

        }
}
