FROM maven:3.8.6-openjdk-18
WORKDIR /code
COPY src/ /code/src/
COPY pom.xml /code/pom.xml
RUN mvn clean install
ARG JAR_FILE=target/*.jar
WORKDIR /app
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]