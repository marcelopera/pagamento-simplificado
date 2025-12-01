package br.com.truta.entities;

import java.math.BigDecimal;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "ACCOUNTS")
public class Account extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    long id;

    @Column(name = "owner_id")
    long ownerId;

    @Column(name = "balance")
    BigDecimal balance;

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    
}
