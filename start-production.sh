docker stop $(docker ps -q);
docker rm $(docker ps -a -q);
sudo bash mvnw clean -Pnative spring-boot:build-image -DskipTests
docker-compose down;
docker-compose up -d;
docker-compose ps;
docker-compose logs -f orders_app;
