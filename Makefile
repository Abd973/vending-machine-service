# build and run
run:
	docker-compose up --build -d

# Stop and remove containers
stop:
	docker-compose down

# Stops and removes containers + DB volumes
reset:
	docker-compose down -v