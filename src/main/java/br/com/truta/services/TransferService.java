package br.com.truta.services;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import br.com.truta.clients.ExternalAutorizationClient;
import br.com.truta.clients.NotificationClient;
import br.com.truta.entities.Account;
import br.com.truta.entities.UserEntity;
import br.com.truta.exceptions.TransferException;
import br.com.truta.models.AuthResponse;
import br.com.truta.models.TransferRequest;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class TransferService {

    @Inject
    Logger logger;

    @RestClient
    @Inject
    NotificationClient notificationClient;

    @RestClient
    @Inject
    ExternalAutorizationClient externalAutorizationClient;

    private Queue<TransferRequest> notificationQueue = new ConcurrentLinkedQueue<>();

    public void validateRequest(TransferRequest req) {
        UserEntity payer = UserEntity.<UserEntity>findById(req.payer());
        if (payer.type == 2) {
            throw new TransferException("Falha negocial. Loja não pode fazer transferencia", "001");
        }
    }

    public void addNotificationToQueue(TransferRequest req) {
        notificationQueue.add(req);
    }

    public Response makeTransfer(TransferRequest req) {
        Account payer = Account.find("ownerId", req.payer()).firstResult();
        Account payee = Account.find("ownerId", req.payee()).firstResult();

        if (payer == null) {
            throw new TransferException("Conta de origem não encontrada", "004");
        }
        if (payee == null) {
            throw new TransferException("Conta de destino não encontrada", "005");
        }

        if (payer.getBalance().compareTo(req.value()) < 0) {
            throw new TransferException("Saldo insuficiente", "009");
        }

        payer.setBalance(payer.getBalance().subtract(req.value()));
        payee.setBalance(payee.getBalance().add(req.value()));

        try {
            // AuthResponse authExternalResponse = externalAutorizationClient.getAuthorization().readEntity(AuthResponse.class);
            // if (authExternalResponse.data().get("authorization")) {
            //     addNotificationToQueue(req);
            //     return Response.status(Response.Status.ACCEPTED).entity("Transferencia Bem-sucedida").build();
            // }
            return Response.status(Response.Status.ACCEPTED).entity("Transferencia Bem-sucedida").build();
            // throw new TransferException("Transação não autorizada", "002");
        } catch (Exception e) {
            throw new TransferException("Falha ao validar autorização", "003");
        }
    }

    @Scheduled(every = "5s")
    public void sendNotification() {
        while (!notificationQueue.isEmpty()) {
            TransferRequest req = notificationQueue.peek();
            logger.info("Tentando enviar notificacao para requisicao: " + req);
            try {
                notificationClient.sendNotification();
                notificationQueue.poll();
            } catch (Exception e) {
                logger.error("Falha ao enviar notificação" + e);
            }
        }
    }

}
