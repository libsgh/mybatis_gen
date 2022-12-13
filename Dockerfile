FROM maven:3.8.6-openjdk-18
WORKDIR /code
COPY src/ /code/src/
COPY pom.xml /code/pom.xml
RUN mvn clean install
WORKDIR /app
COPY /code/target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]