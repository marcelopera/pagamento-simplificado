package br.com.truta.models;

import java.math.BigDecimal;

public record TransferRequest(
    BigDecimal value,
    long payer,
    long payee
) {
    
}
