package br.com.truta.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "servico-autorizativo")
public interface ExternalAutorizationClient {
    
    @GET
    Uni<Response> getAuthorization();

}
