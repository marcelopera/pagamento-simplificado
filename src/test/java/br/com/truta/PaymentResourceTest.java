package br.com.truta;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import br.com.truta.entities.Account;
import br.com.truta.entities.UserEntity;

import static io.restassured.RestAssured.given;

import java.math.BigDecimal;
import java.util.Random;

@QuarkusTest
class PaymentResourceTest {
    @Test
    @TestTransaction
    void testCreateUserEndpoint() {

        Random random = new Random();
        int code = random.nextInt(10000);

        UserEntity user = new UserEntity();
        user.cadastrationCode = String.valueOf(code);
        user.email = "testCreateUserEndpoint@test.com";
        user.fullName = "Test da Silva";
        user.password = "123";
        user.type = 1;

        given()
          .when()
          .contentType(MediaType.APPLICATION_JSON)
          .body(user)
          .post("/create-user")
          .then()
             .statusCode(201);
    }

    @Test
    @TestTransaction
    void testCreateUserEndpointRepeatedCode() {

        Random random = new Random();
        int code = random.nextInt(10000);

        UserEntity user = new UserEntity();
        user.cadastrationCode = String.valueOf(code);
        user.email = "testCreateUserEndpointRepeatedCode1@test.com";
        user.fullName = "Test da Silva";
        user.password = "123";
        user.type = 1;

        UserEntity userRepeated = new UserEntity();
        userRepeated.cadastrationCode = String.valueOf(code);
        userRepeated.email = "testCreateUserEndpointRepeatedCode2@test.com";
        userRepeated.fullName = "Test da Silva";
        userRepeated.password = "123";
        userRepeated.type = 1;

        given()
          .when()
          .contentType(MediaType.APPLICATION_JSON)
          .body(user)
          .post("/create-user")
          .then()
             .statusCode(201);
        
        given()
          .when()
          .contentType(MediaType.APPLICATION_JSON)
          .body(userRepeated)
          .post("/create-user")
          .then()
             .statusCode(409);
    }

    @Test
    @TestTransaction
    void testCreateUserEndpointRepeatedEmail() {

        Random random = new Random();
        int code = random.nextInt(10000);
        int code2 = code + 1;

        UserEntity user = new UserEntity();
        user.cadastrationCode = String.valueOf(code);
        user.email = "testCreateUserEndpointRepeatedEmail@test.com";
        user.fullName = "Test da Silva";
        user.password = "123";
        user.type = 1;

        UserEntity userRepeated = new UserEntity();
        userRepeated.cadastrationCode = String.valueOf(code2);
        userRepeated.email = "testCreateUserEndpointRepeatedEmail@test.com";
        userRepeated.fullName = "Test da Silva";
        userRepeated.password = "123";
        userRepeated.type = 1;

        given()
          .when()
          .contentType(MediaType.APPLICATION_JSON)
          .body(user)
          .post("/create-user")
          .then()
             .statusCode(201);
        
        given()
          .when()
          .contentType(MediaType.APPLICATION_JSON)
          .body(userRepeated)
          .post("/create-user")
          .then()
             .statusCode(409);
    }

    @Test
    @TestTransaction
    void testCreateAccount() {

        Random random = new Random();
        int code = random.nextInt(10000);

        UserEntity user = new UserEntity();
        user.cadastrationCode = String.valueOf(code);
        user.email = "testCreateAccount@test.com";
        user.fullName = "Test da Silva";
        user.password = "123";
        user.type = 1;

        String location = given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(user)
            .post("/create-user")
            .then()
                .statusCode(201)
                .extract()
                .header("location");

        long id = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
        
        Account acc = new Account();
        acc.balance = BigDecimal.valueOf(1000);
        acc.ownerId = id;

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(acc)
            .post("/create-account")
            .then()
                .statusCode(201);

    }

    @Test
    @TestTransaction
    void testCreateAccountUserInexistente() {
        
        Account acc = new Account();
        acc.balance = BigDecimal.valueOf(1000);
        acc.ownerId = -999l;

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(acc)
            .post("/create-account")
            .then()
                .statusCode(201);

    }

}