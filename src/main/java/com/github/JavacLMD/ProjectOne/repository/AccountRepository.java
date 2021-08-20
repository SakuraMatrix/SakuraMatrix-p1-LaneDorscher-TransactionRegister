package com.github.JavacLMD.ProjectOne.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.update.Update;

import com.github.JavacLMD.ProjectOne.domain.Account;
import com.github.JavacLMD.ProjectOne.domain.TransactionLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Random;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

@Repository
public class AccountRepository {
    private static final Logger log = LoggerFactory.getLogger(AccountRepository.class);

    private final CqlSession session;

    public AccountRepository(CqlSession session) {
        this.session = session;
    }

    /**
     * Returns all accounts registered in the database
     * @return flux of all Accounts in the table
     */
    public Flux<Account> getAllAccounts() {
        Select select = QueryBuilder.selectFrom("bank", "accounts").all();

        log.info("Getting all accounts...");
        return Flux.from(session.executeReactive(select.build()))
                .map(AccountRepository::getAccountFromRow);

    }

    /**
     * Returns single account registered with the id
     * @param id Account ID
     * @return mono of the account with the id
     */
    public Mono<Account> getAccountById(int id) {
        Select select = QueryBuilder.selectFrom("bank", "accounts")
                .columns("account_id", "name", "birthday", "balance", "join_date")
                .whereColumn("account_id").isEqualTo(literal(id));

        log.info("Getting account by id...");
        return Mono.from(session.executeReactive(select.build()))
                .map(AccountRepository::getAccountFromRow);
    }

    /**
     * Adds the account to the database
     * @param account Account to add to the database
     * @return newly created account as a Mono
     */
    public Mono<Account> create(Account account) {
        log.info("Account creation in progress...");
        Insert insert = QueryBuilder.insertInto("bank", "accounts")
                    .value("account_id", literal(account.getId()))
                    .value("name", literal(account.getName()))
                    .value("birthday", literal(account.getBirthday()))
                    .value("join_date", literal(account.getJoinDate()))
                    .value("balance", literal(account.getBalance()));

            Flux.just(insert.build())
                    .flatMap(session::executeReactive)
                    .subscribe();
        return Mono.just(account);
    }

    /**
     * Updates existing account with values from a partially constructed account
     * @param partialAccount Account object holding the new values
     * @return true if no errors are thrown (does not mean it updated)
     */
    public Mono<Boolean> updateAccount(Account partialAccount) {
        boolean flag;
        try {
            getAccountById(partialAccount.getId()).subscribe(account -> {
                log.info("Account update in progress...");

                boolean changedBalance = false;
                String name = partialAccount.getName();
                LocalDate birthday = partialAccount.getBirthday();
                double balance = partialAccount.getBalance();

                if (name != null && !name.isEmpty()) {
                    account.setName(name);
                }
                if (birthday != null && birthday.isBefore(LocalDate.now())) {
                    account.setBirthday(birthday);
                }
                if (balance != -999999) {
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
            log.info("Account update successful!");
            flag = true;
        } catch (RuntimeException e) {
            log.error("Account update failed!" + e.getMessage());
            flag = false;
        }

        return Mono.just(flag);
    }

    /**
     * Deletes the account from the database by associated id
     * @return true if no errors are thrown (does not mean it deleted)
     */
    public Mono<Boolean> deleteAccountById(int id) {
        boolean flag;
        try {
            log.info("Account deletion in progress...");
            Delete delete = QueryBuilder.deleteFrom("bank", "accounts")
                    .whereColumn("account_id").isEqualTo(literal(id));
            Flux.just(delete.build())
                    .flatMap(session::executeReactive)
                    .subscribe();

            deleteLogsFromAccount(id).subscribe();
            log.info("Account deletion successful!");
            flag = true;
        } catch (RuntimeException e) {
            flag = false;
            log.error("Failed to delete account! | " + e.getMessage());
        }
        return Mono.just(flag);
    }

    /**
     * Get all the transaction logs from the database
     */
    public Flux<TransactionLog> getAllTransactionLogs() {
        log.info("Getting all accounts...");
        Select select = QueryBuilder.selectFrom("bank", "transaction_logs")
                .all();

        return Flux.from(session.executeReactive(select.build()))
                .map(AccountRepository::getLogFromRow);
    }

    /**
     * Get all the transaction logs associated with the account id from the database
     */
    public Flux<TransactionLog> getLogsByAccountId(int accountId) {
        log.info("Getting account by id: " + accountId);
        Select select = QueryBuilder.selectFrom("bank", "transaction_logs")
                .all()
                .whereColumn("account_id").isEqualTo(literal(accountId));

        return Flux.from(session.executeReactive(select.build()))
                .map(AccountRepository::getLogFromRow);
    }

    /**
     * Add a transaction log to the account and update the account's balance
     * @return true if no errors where thrown (does not mean it was added)
     */
    public Mono<Boolean> addLogToAccount(int accountId, TransactionLog partialLog) {
        boolean flag;
        log.info("Attempting to add log to table...");
        try {
            //update account value
            getAccountById(accountId).subscribe(account -> {

                double newBalance = account.getBalance() + partialLog.getAmount();
                account.setBalance(newBalance);

                log.info("Attempting to update account balance...");
                Update updateAccount = QueryBuilder.update("bank", "accounts")
                        .setColumn("balance", literal(account.getBalance()))
                        .whereColumn("account_id").isEqualTo(literal(accountId));
                log.info("Updated account balance to reflect changes.");

                TransactionLog transactionLog = TransactionLog.from(accountId, new Random().nextInt(), partialLog.getTransactionDate(),
                        partialLog.getCode(), partialLog.getDescription(), partialLog.getAmount(), account.getBalance());

                Flux.just(updateAccount.build())
                        .flatMap(session::executeReactive)
                        .subscribe();

                log.info("Attempting to add transaction log to database...");
                //add to database
                Insert insert = QueryBuilder.insertInto("bank", "transaction_logs")
                        .value("account_id", literal(transactionLog.getAccountId()))
                        .value("log_id", literal(transactionLog.getLogId()))
                        .value("transaction_date", literal(transactionLog.getTransactionDate()))
                        .value("code", literal(transactionLog.getCode()))
                        .value("description", literal(transactionLog.getDescription()))
                        .value("amount", literal(transactionLog.getAmount()))
                        .value("isCredit", literal(transactionLog.isCredit()))
                        .value("balanceAtDate", literal(transactionLog.getBalanceAtDate()));

                Flux.just(insert.build())
                        .flatMap(session::executeReactive)
                        .subscribe();
                log.info("Added transaction log to database...");
            });
            flag = true;
        } catch (RuntimeException e) {
            log.error("Adding transaction log failed! | " + e.getMessage());
            flag = false;
        }
        return Mono.just(flag);
    }

    /**
     * Delete all transaction logs from account with associated id
     * @return true if no errors were thrown (does not mean it was deleted)
     */
    public Mono<Boolean> deleteLogsFromAccount(int accountId) {
        boolean flag;

        log.info("Attempting deletion of logs...");
        try {
            Delete delete = QueryBuilder.deleteFrom("bank", "transaction_logs")
                    .whereColumn("account_id").isEqualTo(literal(accountId));

            Flux.from(session.executeReactive(delete.build()))
                    .subscribe();
            flag = true;
            log.info("Deletion successful!");
        } catch (RuntimeException e) {
            log.error("Deletion failed! | " + e.getMessage());
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
