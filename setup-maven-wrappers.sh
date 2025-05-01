#!/bin/bash

# List of service directories
services=(
  "services/config-server"
  "services/discovery-service"
#   "services/api-gateway"
  "services/customer-service"
  "services/product-service"
  "services/order-service"
  "services/payment-service"
  "services/notification-service"
)

# Setup Maven wrapper for each service
for service in "${services[@]}"; do
  echo "Setting up Maven wrapper for $service"
  cd "$service" || continue
  
  # Create directory structure
  mkdir -p .mvn/wrapper
  
  # Create properties file
  echo "distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.8.6/apache-maven-3.8.6-bin.zip" > .mvn/wrapper/maven-wrapper.properties
  
  # Download wrapper JAR and scripts
  curl -o .mvn/wrapper/maven-wrapper.jar https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.1/maven-wrapper-3.1.1.jar
  curl -o mvnw https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw
  curl -o mvnw.cmd https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw.cmd
  
  # Make wrapper executable
  chmod +x mvnw
  
  cd - || exit
done

echo "Maven wrappers set up for all services"