package com.nttdata.knot.deployapi;

import javax.net.ssl.SSLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
@OpenAPIDefinition(
     servers = {
        @Server(url = "http://knot.westeurope.cloudapp.azure.com/deploy-api", description = "Production Knot Deploy API server")
     },
     info = @Info(title = "Knot Deploy API")
 )

public class DeployApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeployApiApplication.class, args);
    }

    @Bean
    public HttpClient httpClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        return HttpClient.create().secure(t -> t.sslContext(sslContext));
    }

    /*@Bean
    public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
    .allowedOrigins("*")
    .allowedMethods("*")
    .allowedHeaders("*");
    }};
    */
  }
