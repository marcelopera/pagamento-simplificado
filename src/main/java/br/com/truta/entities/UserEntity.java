package br.com.truta.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "USERS")
public class UserEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    public long id;

    @Column(name = "full_name")
    public String fullName;

    @Column(name = "type_code")
    public int type;

    @Column(name = "cadastration_code", unique = true)
    public String cadastrationCode;

    @Column(name = "email", unique = true)
    public String email;

    @Column(name = "password")
    public String password;

}
