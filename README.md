# Transaction Register App
Log your account transaction in this app!

## Features
- Create account
- Update account
  - When balance changes, adds transaction log to represent change
- Delete account
  - Deletes all associated transaction logs
- View all accounts
- View single account by id

- View Transaction Logs
- View Transaction logs from specified account id
- Delete all transaction logs from specified account id
- Add Transaction logs to specified account
  - Automatically adjusts account balance to reflect changes

## Restful Endpoints
 - GET "/accounts": Retrieves all account information
 - POST "/accounts": Creates a new account to the database by passing json string
   - Example: '{"id":4,"name":"Lane Dorscher","birthday":[1998,1,22],"balance":500.0}'
 - PUT "/accounts": Updates existing account by passing json string
   - Example: '{"id":4,"name":"Lane","birthday":[1998,1,22],"balance":1000.0}'
 - GET "/accounts/{account_id}": Retrieves the account information from the given account id
 - DELETE "/accounts/{account_id}": deletes account with the associated id
 
 - GET "/logs": Retrieves all transactions
 - GET "/logs/accountId={accountId}": Retrieves all transactions associated with the account id
 - POST "/logs/accountId={accountId}": Creates new transaction for account from json;
   - Example: '{"accountId":1,"logId":1,"transactionDate":[2021,8,19],"code":"TEST","description":"Example","amount":0.0}'
 - DELETE "/logs/accountId={accountId}": Deletes all transactions for the account

 ## Technologies
  - Java
  - Maven
  - JUnit
  - Cassandra
  - SLF4J
  - Spring Framework