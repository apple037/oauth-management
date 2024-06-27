# 第一階段：構建 Maven 項目
FROM maven:3.9.7-sapmachine-22 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

# 第二階段：運行 Spring Boot 應用
FROM openjdk:22-jdk
WORKDIR /app
COPY --from=build /app/target/OauthDashboard-0.0.1-SNAPSHOT.jar oauthDashboard.jar
EXPOSE 10002
ENTRYPOINT ["java", "-jar", "oauthDashboard.jar"]
