FROM maven:3.8.6-openjdk-18
WORKDIR /code
COPY src/ /code/src/
COPY pom.xml /code/pom.xml
RUN mvn clean install
EXPOSE 8080
ENTRYPOINT ["java","-jar","target/mybatis_gen.jar"]