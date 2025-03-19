FROM openjdk:17-slim

WORKDIR /app

# Correct path based on repo structure
COPY BackEnd/HomeAutomation/target/HomeAutomation-0.0.1-SNAPSHOT.jar /app/HomeAutomation.jar

EXPOSE 8080

CMD ["java", "-jar", "HomeAutomation.jar"]
