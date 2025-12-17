package br.com.truta.resources;

import java.net.URI;
import java.util.List;

import org.jboss.logging.Logger;

import br.com.truta.entities.Account;
import br.com.truta.entities.UserEntity;
import br.com.truta.models.TransferRequest;
import br.com.truta.services.TransferService;
import io.smallrye.common.annotation.RunOnVirtualThread;
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
public class PaymentResource {

    @Inject
    Logger logger;

    @Inject
    TransferService transferService;

    @POST
    @Path("/transfer")
    @RunOnVirtualThread
    @Transactional
    public Response transfer(TransferRequest req) {
        transferService.validateRequest(req);        
        return transferService.makeTransfer(req);
    }

    @POST
    @Path("/create-user")
    @RunOnVirtualThread
    @Transactional
    public Response createUser(UserEntity user) {

        if (UserEntity.<UserEntity>find("email = ?1 OR cadastrationCode = ?2", user.email, user.cadastrationCode).firstResult() != null) {
            return Response.status(Response.Status.CONFLICT).entity("Usuario ja cadastrado").build();
        }
        try {
            user.persist();
            URI uri = new URI("/user/" + user.id);
            return Response.created(uri).build();
        } catch (Exception e) {
            logger.error("Falha ao criar usuario", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/create-account")
    @RunOnVirtualThread
    @Transactional
    public Response createAccount(Account acc) {
        try {
            acc.persist();
            URI uri = new URI("/account/" + acc.id);
            return Response.created(uri).build();
        } catch (Exception e) {
            logger.error("Falha ao criar conta", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/users")
    @RunOnVirtualThread
    public List<UserEntity> getUsers() {
        return UserEntity.listAll();
    }

    @GET
    @Path("/user/{id}")
    @RunOnVirtualThread
    public UserEntity getUser(@PathParam("id") long userId) {
        return UserEntity.findById(userId);
    }

    @GET
    @Path("/account/{id}")
    @RunOnVirtualThread
    public Account getAccount(@PathParam("id") long accId) {
        return Account.findById(accId);
    }

    @GET
    @Path("/balance/{id}")
    @RunOnVirtualThread
    public Account getBalance(@PathParam("id") long ownerId) {
        return Account.find("ownerId", ownerId).firstResult();
    }

}
