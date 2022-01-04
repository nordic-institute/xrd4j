# Base java:11
FROM eclipse-temurin:11-jre

# Add Rest Gateway jar to container
ADD target/example-adapter-*.jar example-adapter.jar

# Entry in json format
ENTRYPOINT ["java", "-jar", "/example-adapter.jar"]

# Expose Tomcat
EXPOSE 8080
