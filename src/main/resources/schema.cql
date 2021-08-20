DROP KEYSPACE IF EXISTS bank;

CREATE KEYSPACE IF NOT EXISTS bank
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS bank.transaction_logs (
    account_id INT,
    log_id INT,
    transaction_date DATE,
    code TEXT,
    description TEXT,
    amount DOUBLE,
    isCredit BOOLEAN,
    balanceAtDate DOUBLE,
    PRIMARY KEY (account_id, transaction_date, log_id)
);

CREATE TABLE IF NOT EXISTS bank.accounts (
    account_id INT,
    name TEXT,
    birthday DATE,
    join_date DATE,
    balance DOUBLE,
    PRIMARY KEY (account_id)
);

INSERT INTO bank.accounts (account_id, name, birthday, join_date, balance)
VALUES (1, 'Lane Dorscher', '1998-01-22', '2021-08-19', 500.00);

INSERT INTO bank.transaction_logs (account_id, log_id, transaction_date, code, description, amount, isCredit, balanceAtDate)
VALUES (1, 2, '2021-08-19', 'AD', 'Test Entry', 500.00, true, 500.00);

INSERT INTO bank.transaction_logs (account_id, log_id, transaction_date, code, description, amount, isCredit, balanceAtDate)
VALUES (1, 2, '2021-08-19', '000', 'Test Entry', 0, false, 500.00);