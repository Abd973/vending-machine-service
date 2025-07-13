#  Run Vending Machine App with Docker

##  Prerequisites

- Docker and Docker Compose installed

## Build & Run
I have added MAKE file to make it easy for you to spin up the project

The First time you run the project you need to run the following command:-
```bash
 make first_time
```

- This will build docker to install the needed JVM, build the project (without testcases) and start the services.

Afterward, you can run the project with the following command
```bash
make run 
```


additional commands 
- make docker_down  (# Stop the services and delete the container)
- make docker_down_delete_db (# Just as docker_down but also delete the database)


integration with the DB and using jwt_secret will be done through .env file since these are sensitive data and should not be committed to the repository.
