# Requirements
The task consists of a wallet server and a wallet client.  The wallet server will keep track of a users monetary
 balance in the system.  There should be separate balance for different currencies.  
 The client will emulate users depositing and withdrawing funds.

## Database
Must include the database schema that is needed for the server application.

## Technologies
### The following technologies must be used
- Java 8+ 
- Spring 5.x
- gRPC
- MySQL 5.x
- Gradle
- JUnit
### Extra points for using these technologies:
- Docker
- Hibernate
- Spring Boot 2.x

# Wallet Server
The wallet server must expose the interface described below via gRPC.
## Interfaces
### Deposit
Deposit funds to the users wallet.
#### Input
- User id
- Amount
- Currency (allowed values are EUR, USD, GBP)
#### Output
- No output needed
- Errors
- Unknown currency
 
### Withdraw
Withdraw funds from the user wallet.
#### Input
- User id
- Amount
- Currency (allowed values are EUR, USD, GBP)
#### Output
- No output needed
#### Errors
- Unknown currency, insufficient funds
 
### Balance
Get the users current balance.
#### Input
- User id
#### Output
- The balance of the users account for each currency

#### Integration Test
```
- Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
- Make a deposit of USD 100 to user with id 1.
- Check that all balances are correct
- Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
- Make a deposit of EUR 100 to user with id 1.
- Check that all balances are correct
- Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
- Make a deposit of USD 100 to user with id 1.
- Check that all balances are correct
- Make a withdrawal of USD 200 for user with id 1. Must return "ok".
- Check that all balances are correct
- Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
```
# Wallet Client
The wallet client will emulate a number of users concurrently using the wallet.  The wallet client must connect to the wallet server over gRPC. 
The client eliminating users doing rounds (a sequence of events). Whenever a round is needed it is picked at random from the following list of available rounds
### Round A
```
- Deposit 100 USD
- Withdraw 200 USD
- Deposit 100 EUR
- Get Balance
- Withdraw 100 USD
- Get Balance
- Withdraw 100 USD
```
### Round B
```
- Withdraw 100 GBP
- Deposit 300 GPB
- Withdraw 100 GBP
- Withdraw 100 GBP
- Withdraw 100 GBP
```
### Round C
```
- Get Balance
- Deposit 100 USD
- Deposit 100 USD
- Withdraw 100 USD
- Depsoit 100 USD
- Get Balance
- Withdraw 200 USD
- Get Balance
```
## The wallet client CLI parameters
```
- Users (number of concurrent users emulated)
- Concurrent_threads_per_user (number of concurrent requests a user will make)
- Rounds_per_thread (number of rounds each thread is executing)
- Make sure the client exits when all rounds has been executed.
```
# Reporting
Together with your source code please include the following:
- Instructions on how to run the client and the server
- Explanation of important choices in your solution
- Estimate on how many transactions your wallet can handle per second on your development machine (and an explanation
 on how you reached that number)

# Deadline
The project must be submitted for review at the most three weeks after the task has been assigned.

# Submission and Follow Up
Please submit the task to anastasia.sakovskaja@betpawa.com  AND valentina.frei@betpawa.com AND aleksei.makagonov@betpawa.com
