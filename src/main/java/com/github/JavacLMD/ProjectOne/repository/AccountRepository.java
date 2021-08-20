package com.github.JavacLMD.ProjectOne.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.update.Update;

import com.github.JavacLMD.ProjectOne.domain.Account;
import com.github.JavacLMD.ProjectOne.domain.TransactionLog;

import jnr.constants.platform.Local;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

@Repository
public class AccountRepository {

    private CqlSession session;

    public AccountRepository(CqlSession session) {
        this.session = session;
    }

    //region Account Table
    public Flux<Account> getAllAccounts() {
        Select select = QueryBuilder.selectFrom("bank", "accounts").all();

        return Flux.from(session.executeReactive(select.build()))
                .map(AccountRepository::getAccountFromRow);

    }

    public Mono<Account> getAccountById(int id) {
        Select select = QueryBuilder.selectFrom("bank", "accounts")
                .columns("account_id", "name", "birthday", "balance", "join_date")
                .whereColumn("account_id").isEqualTo(literal(id));

        return Mono.from(session.executeReactive(select.build()))
                .map(AccountRepository::getAccountFromRow);
    }

    public Mono<Boolean> create(Account account) {
        boolean flag = false;
        try {
            Insert insert = QueryBuilder.insertInto("bank", "accounts")
                    .value("account_id", literal(account.getId()))
                    .value("name", literal(account.getName()))
                    .value("birthday", literal(account.getBirthday()))
                    .value("join_date", literal(account.getJoinDate()))
                    .value("balance", literal(account.getBalance()));

            Flux.just(insert.build())
                    .flatMap(session::executeReactive)
                    .subscribe();
            flag = true;
        } catch (RuntimeException e) {
            flag = false;
        }
        return Mono.just(flag);
    }

    public Mono<Boolean> updateAccount(Account partialAccount) {
        boolean flag = false;
        try {
            getAccountById(partialAccount.getId()).subscribe(account -> {

                boolean changedBalance = false;
                String name = partialAccount.getName();
                LocalDate birthday = partialAccount.getBirthday();
                Double balance = partialAccount.getBalance();

                if (name != null && !name.isEmpty()) {
                    account.setName(name);
                }
                if (birthday != null && birthday.isBefore(LocalDate.now())) {
                    account.setBirthday(birthday);
                }
                if (balance != null && balance != 0.00) {
                    account.setBalance(balance);
                    changedBalance = true;
                }

                Update update = QueryBuilder.update("bank", "accounts")
                        .setColumn("name", literal(account.getName()))
                        .setColumn("birthday", literal(account.getBirthday()))
                        .setColumn("balance", literal(account.getBalance()))
                        .whereColumn("account_id").isEqualTo(literal(account.getId()));

                Flux.just(update.build())
                        .flatMap(session::executeReactive)
                        .subscribe();

                if (changedBalance) {
                    TransactionLog log = TransactionLog.from(account.getId(),
                            LocalDate.now()
                            , "ATM",
                            "Unidentified Balance Change",
                            account.getBalance(),
                            account.getBalance());

                    Insert insert = QueryBuilder.insertInto("bank", "transaction_logs")
                            .value("account_id", literal(log.getAccountId()))
                            .value("log_id", literal(log.getLogId()))
                            .value("transaction_date", literal(log.getTransactionDate()))
                            .value("code", literal(log.getCode()))
                            .value("description", literal(log.getDescription()))
                            .value("amount", literal(log.getAmount()))
                            .value("isCredit", literal(log.isCredit()))
                            .value("balanceAtDate", literal(log.getBalanceAtDate()));

                    Flux.just(insert.build())
                            .flatMap(session::executeReactive)
                            .subscribe();
                }
            });
            flag = true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            flag = false;
        }


        return Mono.just(flag);
    }

    public Mono<Boolean> deleteAccountById(int id) {
        boolean flag = false;
        try {
            Delete delete = QueryBuilder.deleteFrom("bank", "accounts")
                    .whereColumn("account_id").isEqualTo(literal(id));
            Flux.just(delete.build())
                    .flatMap(session::executeReactive)
                    .subscribe();

            deleteLogsFromAccount(id).subscribe();

            flag = true;
        } catch (RuntimeException e) {
            flag = false;
        }
        return Mono.just(flag);
    }

    //endregion

    public Flux<TransactionLog> getAllTransactionLogs() {
        Select select = QueryBuilder.selectFrom("bank", "transaction_logs")
                .all();

        return Flux.from(session.executeReactive(select.build()))
                .map(AccountRepository::getLogFromRow);
    }

    public Flux<TransactionLog> getLogsByAccountId(int accountId) {
        Select select = QueryBuilder.selectFrom("bank", "transaction_logs")
                .all()
                .whereColumn("account_id").isEqualTo(literal(accountId));

        return Flux.from(session.executeReactive(select.build()))
                .map(AccountRepository::getLogFromRow);
    }

    public Mono<Boolean> addLogToAccount(int accountId, TransactionLog partialLog) {
        boolean flag = false;
        try {
            //update account value
            getAccountById(accountId).subscribe(account -> {

                double newBalance = account.getBalance() + partialLog.getAmount();
                account.setBalance(newBalance);

                Update updateAccount = QueryBuilder.update("bank", "accounts")
                        .setColumn("balance", literal(account.getBalance()))
                        .whereColumn("account_id").isEqualTo(literal(accountId));

                TransactionLog log = TransactionLog.from(accountId, new Random().nextInt(), partialLog.getTransactionDate(),
                        partialLog.getCode(), partialLog.getDescription(), partialLog.getAmount(), account.getBalance());

                Flux.just(updateAccount.build())
                        .flatMap(session::executeReactive)
                        .subscribe();

                //add to database
                Insert insert = QueryBuilder.insertInto("bank", "transaction_logs")
                        .value("account_id", literal(log.getAccountId()))
                        .value("log_id", literal(log.getLogId()))
                        .value("transaction_date", literal(log.getTransactionDate()))
                        .value("code", literal(log.getCode()))
                        .value("description", literal(log.getDescription()))
                        .value("amount", literal(log.getAmount()))
                        .value("isCredit", literal(log.isCredit()))
                        .value("balanceAtDate", literal(log.getBalanceAtDate()));

                Flux.just(insert.build())
                        .flatMap(session::executeReactive)
                        .subscribe();
            });
            flag = true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            flag = false;
        }
        return Mono.just(flag);
    }

    public Mono<Boolean> deleteLogsFromAccount(int accountId) {
        boolean flag = false;
        try {
            Delete delete = QueryBuilder.deleteFrom("bank", "transaction_logs")
                    .whereColumn("account_id").isEqualTo(literal(accountId));

            Flux.from(session.executeReactive(delete.build()))
                    .subscribe();
            flag = true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            flag = false;
        }
        return Mono.just(flag);
    }

    private static Account getAccountFromRow(Row row) {
        return Account.from(
                row.getInt("account_id"),
                row.getString("name"),
                row.getLocalDate("birthday"),
                row.getDouble("balance"),
                row.getLocalDate("join_date")
        );
    }

    private static TransactionLog getLogFromRow(Row row) {
        return TransactionLog.from(
                row.getInt("account_id"),
                row.getInt("log_id"),
                row.getLocalDate("transaction_date"),
                row.getString("code"),
                row.getString("description"),
                row.getDouble("amount"),
                row.getBoolean("isCredit"),
                row.getDouble("balanceAtDate")
        );
    }


}
