package com.github.JavacLMD.ProjectOne;

import com.datastax.oss.driver.api.core.CqlSession;
import com.github.JavacLMD.ProjectOne.domain.Account;
import com.github.JavacLMD.ProjectOne.domain.TransactionLog;
import com.github.JavacLMD.ProjectOne.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.server.HttpServer;

@Configuration
@ComponentScan
public class AppConfig {

    @Autowired
    AccountService accountService;

    @Bean
    public CqlSession getSession() {
        CqlSession session = CqlSession.builder().build();
        return session;
    }

    @Bean
    public HttpServer getServer() {
        return HttpServer.create()
                .port(8080)
                .route(routes -> routes
                        //get all accounts (WORKS)
                        .get("/accounts", ((request, response) ->
                                response.send(accountService.getAllAccounts()
                                        .map(App::toByteBuf)
                                        .log("http-server")
                                )))
                        //create a new account (WORKS)
                        .post("/accounts", ((request, response) ->
                                response.send(request.receive()
                                        .asString()
                                        .map(Account::from)
                                        .flatMap(accountService::create)
                                        .map(App::toByteBuf)
                                        .log("http-server")
                                )))
                        //update account (WORKS)
                        .put("/accounts", ((request, response) ->
                                response.send(request.receive()
                                        .asString()
                                        .map(Account::from)
                                        .flatMap(accountService::updateAccount)
                                        .map(App::toByteBuf)
                                        .log("http-server")
                                )))
                        //get account by id (WORKS)
                        .get("/accounts/{accountId}", ((request, response) ->
                                response.send(accountService.getAccountById(
                                        Integer.parseInt(request.param("accountId")))
                                        .map(App::toByteBuf)
                                        .log("http-server") //get account by id
                                )))

                        //delete account by id  (WORKS)
                        .delete("/accounts/{accountId}", ((request, response) ->
                                response.send(accountService.deleteAccountById(
                                        Integer.parseInt(request.param("accountId")))
                                        .map(App::toByteBuf)
                                        .log("http-server")
                                )))

                        //get all logs from every account (WORKS)
                        .get("/logs", ((request, response) ->
                                response.send(accountService.getAllTransactionLogs()
                                        .map(App::toByteBuf)
                                        .log("http-server")
                                )))

                        //get all logs from account (WORKS)
                        .get("/logs/accountId={accountId}", ((request, response) ->
                                response.send(accountService.getLogsByAccountId(
                                        Integer.parseInt(request.param("accountId")))
                                        .map(App::toByteBuf)
                                        .log("http-server")
                                )))
                        //add log to account (WORKS)
                        .post("/logs/accountId={accountId}", ((request, response) ->
                                response.send(request.receive()
                                        .asString()
                                        .map(TransactionLog::from)
                                        .flatMap(x -> accountService.addLogToAccount(
                                                Integer.parseInt(request.param("accountId")), x))
                                        .map(App::toByteBuf)
                                        .log("http-server")
                                )))
                        //delete all transaction logs from account (WORKS)
                        .delete("/logs/accountId={accountId}", ((request, response) ->
                                response.send(accountService.deleteLogsFromAccount(Integer.parseInt(request.param("accountId")))
                                        .map(App::toByteBuf)
                                        .log("http-server")
                                )))

                        //test stuff
                        .get("/error", ((request, response) ->
                                response.status(404).addHeader("Message", "This wasn't suppose to happen!")))

                );
    }


}
