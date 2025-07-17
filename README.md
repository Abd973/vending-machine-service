#  Vending Machine Service
 ## Requirements
- REST API should be implemented consuming and producing “application/json”.
- Implement product model with amountAvailable, cost, productName and sellerId fields.
- Implement user model with username, password, deposit and role fields.
- Implement CRUD for users (POST shouldn’t require authentication).
- Implement CRUD for a product model (GET can be called by anyone, while POST, PUT and DELETE can be called only by the seller user who created the product).
- Implement /deposit endpoint so users with a “buyer” role can deposit 5, 10, 20, 50 and 100 cent coins into their vending machine account. 
- Implement /buy endpoint (accepts productId, amount of products) so users with a “buyer” role can buy products with the money they’ve deposited. API should return total they’ve spent, products they’ve purchased and their change if there’s any (in 5, 10, 20, 50 and 100 cent coins).
- Implement /reset endpoint so users with a “buyer” role can reset their deposit.

## Tech stack and conclusion
- Designed and implemented a secure, Dockerized Spring Boot backend service for a vending machine system with role-based access (buyer/seller), product purchase workflows, and JWT authentication. 
- Used Spring Security, Spring Data JPA, and MySQL with schema auto-initialization via Docker Compose.
- Built RESTful APIs with global exception handling and aspect-oriented ownership checking.
- Developed comprehensive unit and integration tests using Mockito, MockMvc, and Spring Boot Test.

#  Howt to Run Vending Machine App with Docker
##  Prerequisites
- Docker and Docker Compose installed.
- add the *.env* file in the root directory of the project, this will contain the DB and app security credentials.
- both **8080** and **3306** ports are free as they will be consumed by the app.

## Build & Run
I have added MAKE file to make it easy for you to spin up the project.

#### All you need is to change directory to the project root and run the following command: -
```bash
 make first_time
```

- This will build docker to install the necessary JVM version, build the project (without testcases) and start the services.

Afterward, you can run the project with the following command
```bash
make run 
```

## Additional commands and notes
- make first_time (# Build the docker image and start the services for the first time, after that you can use make run)
- make run (# Start the services, you can use this after the first time)
- make docker_down  (# Stop the services and delete the container)
- make docker_down_delete_db (# Just as docker_down but also delete the database)
- integration with the DB and JWT secret should be used through .env file since these are sensitive data and should not be committed to the repository.


## Thanks
