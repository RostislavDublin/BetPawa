# WALLETS client/server

## Purpose
This is a demo implementation of the "Wallets" functionality created by Rostislav Dublin<crm_guru@mail.ru> on the functional
 requirements specification of BetPawa company. See the specification saved in the ./SPEC.md.

This implementation produces two main services: Server and Client and allows to run them directly from JARs or
build Docker images, create and run Docker containers (including MySql container). 

## Demo Video 

## System requirements and expected performance
The solution is tested on the developer's laptop with the following characteristics:
- MacBook Pro; Intel Core i7 2.2 GHz; Memory: 16GB 
- CPU 1 x 4 Cores, Cache: L2-256KB, L3-6MB), Hyper-Threading

When tested on local Docker Host, the memory was constrained by 3Gb; 

The following performance was reached:
- On the "cold" (just started) Server with the L2 cache not yet populated 
(with any operation needed to lift the data from the MySql DB) the performance 
was between   


## Limitations of the current version
- The Integer data type is used even for money representation (just for speed and simplicity of prototyping and
 testing). 
  - TODO: upgrade to an appropriate data type (double, BigDecimal, etc.) ;
- No repository used for Java JAR artifacts (they simply stay right in the project local build/lib folders)
  - TODO: use any corporate/global artifacts repository (Nexus, Maven Central, etc.)
- No remote Docker Images repository used (images store right in the Docker/.../ folders)
  - TODO: use corporate/global remote Docker repository;   
- Docker images & containers production and deployment are implemented right in the Gradle module, 
without any CI/CD engine (just to show how it may be dockerized).
  - TODO: Implement production level CI/CD pipeline (with Jenkins and Ansible);
  
## Technologies used
- gRPC 1.24.0 (protobuf, stub, netty-shaded);
- Spring Boot 2.2.0 (web, shell, test, jpa, retry, actuator), lognet:grpc 3.4.3;
- ORM: Hibernate 5.4.6 (core, ehcache-jcache); 
- L2: Ehcache 3.8;
- DB: MySql, H2;  
- Test: Power Mockito 2.0.4, Junit 4.12;
- Gradle 5.6.2   

## Repository structure
The root gradle project is named "Wallet". 
It contains several common utility classes to share with subprojects.   
It has 4 subprojects:

- :Grpc - grpc-protobuf module. Implements Wallet service gRPC interface, produces Grpc.jar;
- :Server - Wallet server Spring Boot application. Depends on ":Grpc", produces Server.jar;
- :Client - Wallet test client Spring Shell application. Depends on ":Grpc", produces Client.jar;
- :Docker - Dockerization module. 
    - Builds and removes Server and Client services Docker images; 
    - Deploys and undeploys MySql, Server and Client containers on a Docker host;
    
## Build the project and get artifacts
### Build the whole project and get Server and Client services jar
```
 ./gradlew clean build
```    
This command cleans the previous build results, builds the root, Grpc, Server and Client modules, runs all unit and
 integration tests and produces finite packages:
 - ./Server/build/lib/Server-<ver>.jar
 - ./Client/build/lib/Client-<ver>.jar
 
#### To bypass tests run
```
 ./gradlew clean build -x test
```    
### (Re)Build the single module
For example, to rebuild Server module only, run
```
 ./gradlew :Server:clean :Server:build
```    
## Produce and manage Docker images
### Manage the Server module Docker image
```$bash
# Build Docker image and put to the local Docker image repository
./gradlew :Docker:buildServerImage

# Remove Docker image from the local Docker image repository
./gradlew :Docker:removeServerImage
```   
### Manage the Client module Docker image
```$bash
# Build Docker image and put to the local Docker image repository
./gradlew :Docker:buildClientImage

# Remove Docker image from the local Docker image repository
./gradlew :Docker:removeClientImage
```   

## Deploy and manage a single Docker container 
Eg. let us look how to manage the Server service Docker container.
```$bash
# Create a container
./gradlew :Docker:createServerContainer

# Start the container
./gradlew :Docker:startServerContainer

# Stop the container
./gradlew :Docker:stopServerContainer

# Remove the container
./gradlew :Docker:removeServerContainer
```   
The same set of commands is also available for the Client service

## Deploy and manage whole the solution including MySql, Server and Client containers
```$bash
# Up (deploy and run) the composition
./gradlew :Docker:composeUp

# Down (stop and undeploy) the composition
./gradlew :Docker:composeDown
```   
REM: It uses ./Docker/docker-compose.yml file for the composition definition.


## Tests and Testing
### Unit tests
The project is 100% covered by Unit tests (excluding tests for generated gRPC classes).
```
# Run all tests in all modules
gradle test

# Run all tests in a particular module
gradle :Server:test
```
### Integration tests
Also, two Server service Integration tests provided:
- WalletRepositoryIntegrationTest - simple persistence tier integration test (on H2 DB with @DataJpaTest).
```
gradle :Server:test --tests *WalletRepositoryIntegrationTest
```
- WalletServiceIntegrationTest - instantiates Spring context and substitutes H2 DB (@SpringBootTest,
@AutoConfigureTestDatabase). Here we test multiple threads performing concurrent wallet operations on the single
 wallet. The purpose of the test is to check reliability of chosen optimistic locks approach. After the series of
  parallel wallet operations we check whether the final actual  wallet balance matches the estimation.
```
gradle :Server:test --tests *WalletServiceIntegrationTest
```

## Deployment and execution
### Deployment
#### Plain Java
Both Server and Client are Spring Boot applications. You can get JARs (built as shown in the above section) 
and run them as plain Java applications, for example, in two separate Terminal windows:

##### Run Server service application:
```
java -Xms1g -Xmx1g -jar ./Server/build/libs/Server-1.0-SNAPSHOT.jar
```
On default, it connects to MySql server running at localhost:3306 using the following settings 
defined in the Server module application.properties file:    
- spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/wallet
- spring.datasource.username=root
- spring.datasource.password=123456Qw
You can customize it by editing the file, or by provisioning custom settings in the command line, eg:
```
java -jar ./Server/build/libs/Server-1.0-SNAPSHOT.jar --spring.datasource.url=jdbc:mysql://customhost:3307/wallet
```
Alternatively, you can customize a MySql server hostname by setting the following environment variable:
- MYSQL_HOST

##### Run Client service application:
```
java -jar ./Client/build/libs/Client-1.0-SNAPSHOT.jar
```
On default, it connects to the Wallet server gRPC service at localhost:6565 using the following settings 
defined in the Client module application.properties file:
- wallet.server.address=${WALLET_SERVER_ADDRESS:localhost}
- wallet.server.port=${WALLET_SERVER_PORT:6565}
You can customize it by editing the file, or by provisioning custom settings in the command line, eg:
```
java -jar ./Client/build/libs/Client-1.0-SNAPSHOT.jar --wallet.server.address=6767
```
Alternatively, you can do the same by setting the following environment variables:
- WALLET_SERVER_ADDRESS
- WALLET_SERVER_PORT

Use commands defined in the Wallet Client Shell application to configure and execute test workloads.
For more details, see "Using Wallet Client application" section below.


#### Dockerized
Use the command shared above in the "Deploy and manage whole the solution..." 
to deploy all 3 containers on the local Docker host. You'll get 3 running containers:
- wallet-server
- wallet-mysql
- wallet-client

Wallet-server container will automatically connect and create DB on the wallet-mysql.
It's gRPC service port 6565 will be reachable for the wallet-client container (as wallet-server:6565)
and also exposed on the Docker Host host machine as the localhost:6565 
(so you can use non-dockerized Wallet Client with dockerized WalletServer).  

Wallet-client container will automatically launch and connect to the wallet-server container on port 6565.
To manage the wallet-client Spring Shell session (to configure and execute test workloads) attach to the docker
 container with the following command:
 ```
docker attach wallet-client
```  
and run the "help" command in the provided command prompt to get further instructions.
For more details, see "Using Wallet Client application" section below.


### Using Wallet Client application
After you launched Wallet Client application (either plain Java or dockerized) 
as it described in the "Deployment and execution" section above,
you have interactive command prompt (thanks to of Spring Shell functionality) 
and can use the following commands to configure and run different test workloads:

- help - shows all available commands, divided on several sections:
- help <command name> - shows a command parameters types and constraints. 

#### Section "1. Wallet operations batch setup"
- current-config: Show current configuration parameters, eg, default config:
```shell script
shell:>current-config

### Output:
Executor:
 - core pool size: 16
 - max pool size: 16
Batch:
 - users: 1
 - threads per user: 1
 - rounds per thread: 1
```
- users: Set the number of concurrent users emulated, eg:
```shell script
shell:>users 1000
```     
- threads-per-user: Set the number of concurrent requests a user will make, eg:
```shell script
shell:>threads-per-user 2
```
- rounds-per-thread: Sets the number of rounds each thread executes, eg:
```shell script
shell:>rounds-per-thread 25
```
- setup-executor: Configure Task Executor thread pool "core" and "max" size to regulate the bulk workload parallelism, eg:
```shell script
shell:>setup-executor 8 16
```     

#### Section "2. Wallet workload execution"
These commands run bulk workloads:
- run-prepared: Run the batch after all parameters (see Section "1.") customization
```shell script
# configure all desired settings
shell:> users 100
shell:> threads-per-user 2
shell:> rounds-per-thread 10
shell:> setup-executor 50 50

# run the configured workload (for 100 users * 2 threads  * 10 rounds = 2000 rounds)
# run it by the executor with 50 concurrent threads in parallel)  
shell:> run-prepared

### Output:
BATCH: Starting for 100 users * 2 threads * 10 rounds
...
BATCH: all 13365 operations in 200 roundsets completed in 28847ms, avg: 463ops/s
```
- run-from-scratch: Run the workload composed in one command from scratch
```shell script
# Batch 1: run the workload with 5 users, 3 threads-per-user, 15 rounds-per-thread
shell:>run-from-scratch 5 3 15 

### Output: 
BATCH: Starting for 5 users * 3 threads * 15 rounds
...
BATCH: all 1536 operations in 15 roundsets completed in 4811ms, avg: 319ops/s

# Batch 2: run the workload with 60 users, 3 threads-per-user, 5 rounds-per-thread
shell>run-from-scratch 60 3 5

### Output:
BATCH: Starting for 60 users * 3 threads * 5 rounds
...
BATCH: all 6000 operations in 180 roundsets completed in 16032ms, avg: 374ops/s
```      
#### Section "3. Single wallet operations"
- run-balance: Run single balance request for user, eg:
```shell script
# Request balance for user 123
shell:>run-balance 123

### Output:
Balance: {GBP=0, USD=0, EUR=500}
```
- run-deposit: Run single deposit for user and amount, eg:
```shell script
shell:>run-deposit 123 1000 USD

### Output
OK

shell:>run-deposit 123 100 RUR

### Output
Unknown currency
```

- run-withdraw: Run single withdraw for user and amount, eg:
```shell script
shell:>run-withdraw 123 1000 USD

### Output
OK

shell:>run-withdraw 123 1000000 USD

### Output
Insufficient funds
```

#### Section "Built-In Commands"
- clear: Clear the shell screen.
- exit, quit: Exit the shell.
- help: Display help about available commands.
- history: Display or save the history of previously run commands
- script: Read and execute commands from a file.
- stacktrace: Display the full stacktrace of the last error.


 
   
 