package com.github.JavacLMD.ProjectOne.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;

public class TransactionLog  {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new ParanamerModule());

    private int accountId = -1;
    private int logId = -1;
    private LocalDate transactionDate = LocalDate.now();
    private String code = "";
    private String description = "";
    private double amount = 0;
    private boolean isCredit = false;
    private double balanceAtDate;


    private TransactionLog(int accountId, int logId, LocalDate transactionDate, String code, String description, double amount, boolean isCredit, double balanceAtDate) {
        this.accountId = accountId;
        this.logId = logId;
        this.transactionDate = transactionDate;
        this.code = code;
        this.description = description;
        this.amount = amount;
        this.isCredit = isCredit;
        this.balanceAtDate = balanceAtDate;
    }

    public TransactionLog() {}

    public static TransactionLog from(TransactionLog log) {
        return from(
                log.getAccountId(),
                log.getLogId(),
                log.getTransactionDate(),
                log.getCode(),
                log.getDescription(),
                log.getAmount(),
                log.getBalanceAtDate());
    }

    public static TransactionLog from(int accountId, LocalDate transactionDate, String code, String description, double amount, double balanceAtDate) {
        return from(accountId, new Random().nextInt(), transactionDate, code, description, amount, balanceAtDate);
    }

    public static TransactionLog from(int accountId, int logId, LocalDate transactionDate, String code, String description, double amount, boolean isCredit, double balanceAtDate) {
        return new TransactionLog(accountId, logId, transactionDate, code, description, amount, isCredit, balanceAtDate);
    }

    public static TransactionLog from(int accountId, int logId, LocalDate transactionDate, String code, String description, double amount, double balanceAtDate) {
        return new TransactionLog(accountId, logId, transactionDate, code, description, amount, amount > 0, balanceAtDate);
    }

    public static TransactionLog from(String str) {
        TransactionLog log = null;
        try {
            log = OBJECT_MAPPER.readValue(str, TransactionLog.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return log;
    }


    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isCredit() {
        return isCredit;
    }

    public void setCredit(boolean credit) {
        isCredit = credit;
    }

    public double getBalanceAtDate() {
        return balanceAtDate;
    }

    public void setBalanceAtDate(double balanceAtDate) {
        this.balanceAtDate = balanceAtDate;
    }
}