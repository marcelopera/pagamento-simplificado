package br.com.truta.resources;

import br.com.truta.models.TransferRequest;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/")
public class Payment {
    
    @POST
    @Path("/transfer")
    @Transactional
    public Uni<Response> transfer (TransferRequest req) {
        return null;
    }


}
