package com.github.JavacLMD.ProjectOne;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Assert;

@SpringJUnitConfig(classes = AppConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppTest {

    @Autowired
    ApplicationContext context;
    WebTestClient rest;

    @BeforeAll
    public void setup() {
        this.rest = WebTestClient.bindToApplicationContext(this.context).configureClient().build();
    }

    @Test
    public void postAccount() {
        rest.post()
                .uri("/accounts")
                .body("'{\"id\":5,\"name\":\"Joe Morgan\",\"birthday\":[1998,1,22],\"balance\":2000.0}'", String.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    public void getAllAccounts() {
        rest.get()
                .uri("/accounts")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void getSingleAccount() {
        rest.get()
                .uri("/accounts/5")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void updateSingleAccount() {
        WebTestClient.ResponseSpec successful = rest.put()
                .uri("/accounts")
                .body("'{\"id\":5,\"name\":\"Joe Morgan\",\"birthday\":[1998,4,22], \"balance\":1000}'", String.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    public void getAllLogs() {
        rest.get()
                .uri("/logs")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void getLogsFromAccount() {
        rest.get()
                .uri("/logs/accountId=5")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void addLogToAccount() {
        rest.post()
                .uri("logs/accountId=5")
                .body("{\"accountId\":5,\"logId\":1,\"transactionDate\":[2021,8,19],\"code\":\"AP\",\"description\":\"Faux register\",\"amount\":50}", String.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    public void deleteLogsFromAccount() {
        rest.delete()
                .uri("/logs/accountId=5")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void deleteAccount() {
        rest.delete()
                .uri("/accounts/5")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void error() {
        rest.delete()
                .uri("/error")
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }



}