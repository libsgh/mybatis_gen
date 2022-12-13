FROM maven:3.8.6-openjdk-18
RUN ls -n
WORKDIR /code
COPY src/ /code/src/
COPY pom.xml /code/pom.xml
RUN mvn clean install
RUN ls -n target
WORKDIR /app
RUN ls -n /root/.m2/repository/com/libsgh
COPY /root/.m2/repository/com/libsgh/mybatis_gen/1.0/mybatis_gen-1.0.jar /app
ENTRYPOINT ["java","-jar","/app/mybatis_gen.jar"]