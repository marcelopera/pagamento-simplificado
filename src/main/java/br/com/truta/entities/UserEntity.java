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
@Table(name = "USERS", uniqueConstraints = @UniqueConstraint(columnNames = { "cadastration_code", "email" }))
public class UserEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    public long id;

    @Column(name = "full_name")
    public String fullName;

    @Column(name = "type_code")
    public int type;

    @Column(name = "cadastration_code")
    public String cadastrationCode;

    @Column(name = "email")
    public String email;

    @Column(name = "password")
    public String password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCadastrationCode() {
        return cadastrationCode;
    }

    public void setCadastrationCode(String cadastrationCode) {
        this.cadastrationCode = cadastrationCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
