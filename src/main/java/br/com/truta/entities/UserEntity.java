package br.com.truta.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity(name = "USERS")
@Table(name = "USERS",uniqueConstraints = @UniqueConstraint(columnNames = {"cadastration_code", "email"}))
public class UserEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    public long id;

    @Column(name = "full_name")
    public String fullName;

    @Column(name = "cadastration_code")
    public String cadastrationCode;

    @Column(name = "email")
    public String email;

    @Column(name = "password")
    public String password;

}
