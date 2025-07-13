#  Run Vending Machine App with Docker

##  Prerequisites
- Docker and Docker Compose installed.
- add the *.env* file in the root directory of the project, this will contain the DB and app security credentials.
- both 8080 and 3306 ports are free as they will be consumed by the app.

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

additional commands and notes
- make first_time (# Build the docker image and start the services for the first time, after that you can use make run)
- make run (# Start the services, you can use this after the first time)
- make docker_down  (# Stop the services and delete the container)
- make docker_down_delete_db (# Just as docker_down but also delete the database)
- integration with the DB and JWT secret should be used through .env file since these are sensitive data and should not be committed to the repository.


## Thanks