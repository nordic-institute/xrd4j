# Base java:11
FROM eclipse-temurin:11-jre

# Add Rest Gateway jar to container
ADD target/example-adapter-*.war example-adapter.war

# Entry in json format
ENTRYPOINT ["java", "-Xms64m", "-Xmx256m", "-jar", "/example-adapter.war"]

# Expose Tomcat
EXPOSE 8080
