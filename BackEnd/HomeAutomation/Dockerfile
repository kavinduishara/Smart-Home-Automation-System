FROM openjdk:17-jre

# Set the working directory
WORKDIR /app

# Copy the JAR file from the target directory (adjust if necessary)
COPY target/HomeAutomation-0.0.1-SNAPSHOT.jar /app/HomeAutomation.jar

# Expose the port that your Spring Boot application will run on
EXPOSE 8080

# Command to run the JAR file
CMD ["java", "-jar", "HomeAutomation.jar"]
