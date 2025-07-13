# Run the app (clean build, docker down, then up with build)
run: build_project docker_down docker_up

# Clean and package the Spring Boot app (skip tests for speed)
build_project:
	mvn clean package -DskipTests

# Clean and package the Spring Boot app (with running tests)
build_project_test:
	mvn clean package

# Build the Docker image manually
build_docker:
	docker build -t vending-machine:latest .

# Remove Docker containers
docker_down:
	docker-compose down

# Remove containers AND volumes (if DB schema changes)
docker_down_delete_db:
	docker-compose down -v

# Run docker-compose (no rebuild, use if no changes to Dockerfile)
docker_up:
	docker-compose up -d

# Run docker-compose with rebuild (when project changes)
docker_up_build:
	docker-compose up --build -d

# Full first-time setup: build project, clean Docker, then rebuild containers
first_time: build_project docker_down_vol docker_up_build

# Clean the Maven project
clean:
	mvn clean

# Run unit tests
test:
	mvn test

# Apply code formatting (spotless plugin)
format:
	mvn spotless:apply