package br.com.truta.services;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import br.com.truta.clients.NotificationClient;
import br.com.truta.entities.Account;
import br.com.truta.entities.UserEntity;
import br.com.truta.exceptions.TransferException;
import br.com.truta.models.TransferRequest;
import br.com.truta.models.TransferResponse;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
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

    private Queue<TransferRequest> notificationQueue = new ConcurrentLinkedQueue<>();

    public Uni<TransferRequest> validateRequest(TransferRequest req) {

        return UserEntity.<UserEntity>findById(req.payer()).flatMap(payer -> {
            if (payer.type==2) {
                return Uni.createFrom().failure(new TransferException("Falha negocial. Loja não pode fazer transferencia", "001"));
            }
            return Uni.createFrom().item(req);
        });

    }

    public void addNotificationToQueue(TransferRequest req) {
        notificationQueue.add(req);
    }

    public Uni<TransferResponse> makeTransfer(TransferRequest req) {
        return Account.<Account>find("ownerId", req.payer()).firstResult()
                .flatMap(payer -> {
                    
                    if (payer == null) {
                        return Uni.createFrom().failure(new TransferException("Conta de origem não encontrada", "004"));
                    }
                    if (payer.getBalance().compareTo(req.value()) < 0) {
                        return Uni.createFrom().failure(new TransferException("Saldo insuficiente", "009"));
                    }

                    return Account.<Account>find("ownerId", req.payee()).firstResult()
                            .map(payee -> {
                                if (payee == null) {
                                    throw new TransferException("Conta de destino não encontrada", "005");
                                }

                                payer.setBalance(payer.getBalance().subtract(req.value()));
                                payee.setBalance(payee.getBalance().add(req.value()));

                                return new TransferResponse("Transferencia Bem-sucedida");
                            });
                });
    }

    @Scheduled(every = "5s")
    public void sendNotification() {
        logger.info("Verificando status da fila");
        if (!notificationQueue.isEmpty()) {

            logger.info("iniciando processamento da fila de transferencias");

            notificationQueue.forEach(item -> 
                notificationClient.sendNotification().invoke(resp -> logger.info(resp.getStatus())).subscribe().with(s -> logger.info(s))
            );

            notificationQueue.clear();
        }
    }

}
