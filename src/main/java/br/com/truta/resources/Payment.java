package br.com.truta.resources;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;

import br.com.truta.clients.ExternalAutorizationClient;
import br.com.truta.clients.NotificationClient;
import br.com.truta.entities.Account;
import br.com.truta.entities.UserEntity;
import br.com.truta.exceptions.TransferException;
import br.com.truta.models.AuthResponse;
import br.com.truta.models.TransferRequest;
import br.com.truta.services.TransferService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/")
public class Payment {

    @Inject
    Logger logger;

    @Inject
    TransferService transferService;

    @RestClient
    @Inject
    ExternalAutorizationClient externalAutorizationClient;

    @RestClient
    @Inject
    NotificationClient notificationClient;

    @POST
    @Path("/transfer")
    public Uni<Response> transfer(TransferRequest req) {
        return Panache.withTransaction(() -> 

                transferService.validateRequest(req)
                .flatMap(item -> {
                    return transferService.makeTransfer(req);
                })

                .flatMap(item -> externalAutorizationClient.getAuthorization().map(r -> {
                    if (r.getStatus() == 200) {

                        AuthResponse authResponse = r.readEntity(AuthResponse.class);

                        if (authResponse.data().get("authorization")) {
                            return Response.accepted(item).build();
                        }

                    }
                    throw new TransferException("Transação não autorizada", "002");
                }))
                .invoke(x -> transferService.addNotificationToQueue(req)));
    }

    @POST
    @Path("/create-user")
    public Uni<Response> createUser(UserEntity user) {
        return Panache.withTransaction(() -> user.<UserEntity>persist().map(item -> {
            return Response.status(Response.Status.CREATED).entity(user).build();
        }));
    }

    @POST
    @Path("/create-account")
    public Uni<Response> createAccount(Account acc) {
        return Panache.withTransaction(() -> acc.<Account>persist().map(item -> {
            return Response.status(Response.Status.CREATED).entity(acc).build();
        }));
    }

    @GET
    @Path("/users")
    public Uni<List<UserEntity>> getUsers() {
        return UserEntity.listAll();
    }

    @GET
    @Path("/balance/{id}")
    public Uni<Account> getBalance(@PathParam("id") long ownerId) {
        return Account.find("ownerId", ownerId).firstResult();
    }

}
