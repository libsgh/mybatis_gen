FROM maven:3.8.6-openjdk-18
WORKDIR /app
COPY * ./
RUN mvn clean install
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]