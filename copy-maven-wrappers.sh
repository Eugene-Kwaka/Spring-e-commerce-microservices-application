#!/bin/bash
# Copies the Maven wrapper from one service to the rest

# Source service with working Maven wrapper
SOURCE_SERVICE="services/api-gateway"

# Target services that need the wrapper
TARGET_SERVICES=(
  "services/config-server"
  "services/discovery-service"
  "services/customer-service"
  "services/product-service"
  "services/order-service"
  "services/payment-service"
  "services/notification-service"
)

# Copy Maven wrapper from source to each target service
for service in "${TARGET_SERVICES[@]}"; do
  echo "Copying Maven wrapper to $service"
  
  # Create .mvn/wrapper directory if it doesn't exist
  mkdir -p "$service/.mvn/wrapper"
  
  # Copy wrapper files
  cp "$SOURCE_SERVICE/.mvn/wrapper/maven-wrapper.properties" "$service/.mvn/wrapper/"
  cp "$SOURCE_SERVICE/.mvn/wrapper/maven-wrapper.jar" "$service/.mvn/wrapper/"
  cp "$SOURCE_SERVICE/mvnw" "$service/"
  cp "$SOURCE_SERVICE/mvnw.cmd" "$service/"
  
  # Make wrapper executable
  chmod +x "$service/mvnw"
  
  echo "Maven wrapper copied to $service"
done

echo "Maven wrappers copied to all services"