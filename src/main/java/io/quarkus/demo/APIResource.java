package io.quarkus.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api")
public class APIResource {

    @Inject @ConfigProperty(name = "greeting.message")
    Optional<String> message;

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return message.orElse("Bonjour");
    }

    @GET
    @Path("/products")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getProducts() {
        return Arrays.asList(new Product("01","Watch","A cool smart watch",200.0),
            new Product("02","Phone","A cool smart phone",300.0),
            new Product("03","TV","A cool smart TV",1000.0),
            new Product("04","Pet","A cool smart Pet",400.0));
    }
}