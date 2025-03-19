FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the built JAR file
COPY target/HomeAutomation-0.0.1-SNAPSHOT.jar /app/HomeAutomation.jar

EXPOSE 8080

CMD ["java", "-jar", "HomeAutomation.jar"]
