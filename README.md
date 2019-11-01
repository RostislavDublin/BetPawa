# WALLET
## Purpose
This is a demo implementation of the "Wallet" functionality created by Rostislav Dublin on the functional requirements specification of BetPawa company. See the specification in the SPEC.md document.

This implementation produces two main services Server and Client and allows to run them directly from their JARs or
build Docker images and install on your local Docker host and run them (and also MySql) dockerized. 

## Repository structure
The root gradle project named "Wallet". 
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
- REM: It uses ./Docker/docker-compose.yml file for the composition definition.
- REM2: The 'wallet-client' container is not yet useful in the current version of the solution. 
It deploys and runs well, but there is no access to the running Spring Shell application shell to emit your commands from the command line. 
It is the limitation of the current version.


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
- run Server service application:
```
java -Xms1g -Xmx1g -jar ./Server/build/libs/Server-1.0-SNAPSHOT.jar
```
- run Client service application:
```
java -jar ./Client/build/libs/Client-1.0-SNAPSHOT.jar
```
This deployment approach assumes you have a MySql server service somehow available on the localhost:3306.
If you have it available on the different address:port then use the following Spring Boot parameters to configure the
 client:
 

#### Dockerized
Use the command shared above in the "Deploy and manage whole the solution..." 
to deploy all 3 containers on the local Docker host. You'll get 3 running containers:
- wallet-server
- wallet-mysql
- wallet-client

Wallet-server container will automatically connect and create DB on the wallet-mysql.
It's gRPC service port will be mapped to the localhost:6565.  

Wallet-client container is not managed yet. It is working, but not ready for use due to the limitations of the
 current version. Please, use plain not-dockerized wallet client Java application (as defined in the previous section). 


### Execution   
     



 
   
 