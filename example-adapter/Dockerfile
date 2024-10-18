# Base java:17
FROM eclipse-temurin:17-jre

# Copy Example Adapter WAR to container
COPY build/libs/example-adapter-*.*.*-boot.war example-adapter.war

# Entry in json format
ENTRYPOINT ["java", "-Xms64m", "-Xmx256m", "-jar", "/example-adapter.war"]

# Expose Tomcat
EXPOSE 8080
