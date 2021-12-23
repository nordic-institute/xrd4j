# Base java:11
FROM adoptopenjdk/openjdk11:jre-11.0.11_9

# Add Rest Gateway jar to container
ADD target/example-adapter-*.jar example-adapter.jar

# Entry in json format
ENTRYPOINT ["java", "-jar", "/example-adapter.jar"]

# Expose Tomcat
EXPOSE 8080
