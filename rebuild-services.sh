#!/bin/bash

# Define service directories and image tags
declare -A services=(
  ["api-gateway"]="api-gateway"
  ["config-server"]="config-server"
  ["customer-service"]="customer-service"
  ["discovery-service"]="discovery-service"
  ["notification-service"]="notification-service"
  ["order-service"]="order-service"
  ["payment-service"]="payment-service"
  ["product-service"]="product-service"
)

echo "🧹 Cleaning old containers and rebuilding services..."
docker compose down --remove-orphans

for folder in "${!services[@]}"; do
  service="${services[$folder]}"
  service_path="services/$folder"

  echo "📦 Building $folder → eugenekwaka/ecommerce_${service}:v1"

  if [ -d "$service_path" ]; then
    cd "$service_path" || exit 1

    echo "🧪 Running Maven clean package..."
    ./mvnw clean package -DskipTests || { echo "❌ Maven build failed in $folder"; exit 1; }

    echo "🐳 Building Docker image..."
    ./mvnw spring-boot:build-image \
      -Dspring-boot.build-image.imageName=eugenekwaka/ecommerce_${service}:v1 \
      -DskipTests || { echo "❌ Docker image build failed in $folder"; exit 1; }

    cd - >/dev/null || exit 1
  else
    echo "⚠️ Skipping $folder (directory not found)"
  fi
done

echo "✅ All services rebuilt successfully!"

