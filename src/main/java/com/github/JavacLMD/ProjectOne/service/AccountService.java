package com.github.JavacLMD.ProjectOne.service;

import com.github.JavacLMD.ProjectOne.domain.Account;
import com.github.JavacLMD.ProjectOne.domain.TransactionLog;
import com.github.JavacLMD.ProjectOne.repository.AccountRepository;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountService {

    final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    //GET "/accounts"
    public Flux<Account> getAllAccounts() {
       return accountRepository.getAllAccounts();
    }

    //POST "/accounts"
    public Mono<Boolean> create(Account account) {
        return accountRepository.create(account);
    }

    //PUT "/accounts"
    public Mono<Boolean> updateAccount(Account partialAccount) {
        return accountRepository.updateAccount(partialAccount);
    }

    //GET "/accounts/{accountId}"
    public Mono<Account> getAccountById(int id) {
        return accountRepository.getAccountById(id);
    }

    //DELETE "/accounts/{accountId}"
    public Mono<Boolean> deleteAccountById(int id) {
        return accountRepository.deleteAccountById(id);
    }

    //GET "/accounts/logs"
    public Flux<TransactionLog> getAllTransactionLogs() {
        return accountRepository.getAllTransactionLogs();
    }


    //GET "/accounts/{accountId}/logs"
    public Flux<TransactionLog> getLogsByAccountId(int accountId) {
        return accountRepository.getLogsByAccountId(accountId);
    }

    //POST "/accounts/{accountId}/logs"
    public Mono<Boolean> addLogToAccount(int accountId, TransactionLog partialLog) {
        return accountRepository.addLogToAccount(accountId, partialLog);
    }

    //DELETE "/accounts/{accountId}/logs/"
    public Mono<Boolean> deleteLogsFromAccount(int accountId) {
        return accountRepository.deleteLogsFromAccount(accountId);
    }


}
