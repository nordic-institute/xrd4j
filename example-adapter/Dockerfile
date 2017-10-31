# Base java:8
FROM java:8

# Add Rest Gateway jar to container
ADD target/example-adapter-*.jar example-adapter.jar

# Entry in json format
ENTRYPOINT ["java", "-jar", "/example-adapter.jar"]

# Expose Tomcat
EXPOSE 8080