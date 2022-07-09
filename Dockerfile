FROM maven:3.6.3 AS maven

WORKDIR /usr/src/app
COPY . /usr/src/app
# Compile and package the application to an executable JAR
RUN mvn package

FROM adoptopenjdk/openjdk15-openj9:alpine-jre

WORKDIR /opt/app

COPY --from=maven /usr/src/app/target/grid-network-simulator*.jar /opt/app/grid-network-simulator.jar

ENTRYPOINT ["java","-jar","grid-network-simulator.jar"]