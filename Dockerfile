FROM maven:3.8.6-openjdk-18
WORKDIR /code
COPY src/ /code/src/
COPY pom.xml /code/pom.xml
RUN mvn clean install
WORKDIR /app
COPY /root/.m2/repository/com/libsgh/mybatis_gen/1.0/mybatis_gen-1.0.jar /app/mybatis_gen.jar
ENTRYPOINT ["java","-jar","/app/mybatis_gen.jar"]