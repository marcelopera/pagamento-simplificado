package br.com.truta.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "servico-notificacao")
public interface NotificationClient {
    
    @POST
    Uni<Response> sendNotification();

}
