package com.github.JavacLMD.ProjectOne.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class Account {

//    CREATE TYPE IF NOT EXISTS bank.transaction_logs (
//            transaction_date DATE,
//            code TEXT,
//            description TEXT,
//            amount DOUBLE,
//            isCredit BOOLEAN,
//            balanceAtDate DOUBLE
//    );
//
//    CREATE TABLE IF NOT EXISTS bank.accounts (
//            account_id INT,
//            name TEXT,
//            birthday DATE,
//            balance DOUBLE,
//            register MAP<int, transaction_logs>,
//            join_date DATE,
//                    PRIMARY KEY (account_id);
//     );

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new ParanamerModule());

    private int id = -1;
    private String name = "";
    private LocalDate birthday = LocalDate.now();
    private double balance = 0;
    private LocalDate joinDate = LocalDate.now();

    public Account() {
        //register = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", balance=" + balance +
                ", joinDate=" + joinDate +
                '}';
    }

    private Account(int id, String name, LocalDate birthday, double balance, LocalDate joinDate) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.balance = balance;
        this.joinDate = joinDate;
    }

    public static Account from(int id, String name, LocalDate birthday, double balance, LocalDate joinDate) {
        return new Account(id,name,birthday,balance, joinDate);
    }

    public static Account from(int id, String name, LocalDate birthday, double balance) {
        return from(id, name, birthday, balance, LocalDate.now());
    }

    public static Account from(String str) {
        Account account = null;
        try {
            account = OBJECT_MAPPER.readValue(str, Account.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account;
    }

    public void logTransaction(TransactionLog log) {
        balance += log.getAmount();
        log.setBalanceAtDate(balance);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }
}
