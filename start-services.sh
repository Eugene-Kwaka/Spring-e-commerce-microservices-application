#!/bin/bash
set -e

# -------------------------------
# Configuration
# -------------------------------
declare -A infra_containers=(
  [postgres]="ecommerce_pg_sql"
  [pgadmin]="ecommerce_pgadmin"
  [zipkin]="ecommerce_zipkin"
  [zookeeper]="ecommerce_zookeeper"
  [kafka]="ecommerce_kafka"
  [mongodb]="ecommerce_mongodb"
  [mongo-express]="ecommerce_mongo_express"
  [mail-dev]="ecommerce_mail_dev"
  [keycloak]="ecommerce_keycloak"
)

# Only containers with healthchecks (right now just postgres)
declare -a services_with_healthchecks=(
  "ecommerce_pg_sql"
)

declare -A microservice_containers=(
  [config-server]="ecommerce_config-server"
  [discovery-service]="ecommerce_discovery-service"
  [api-gateway]="ecommerce_api-gateway"
  [customer-service]="ecommerce_customer-service"
  [product-service]="ecommerce_product-service"
  [order-service]="ecommerce_order-service"
  [payment-service]="ecommerce_payment-service"
  [notification-service]="ecommerce_notification-service"
)

MAX_ATTEMPTS=60
WAIT_SECONDS=10

# -------------------------------
# Utility Functions
# -------------------------------
wait_for_container() {
  local container=$1
  local attempt=1

  if [[ " ${services_with_healthchecks[*]} " =~ " ${container} " ]]; then
    echo "⏳ Waiting for $container to be healthy..."
    while [ "$attempt" -le "$MAX_ATTEMPTS" ]; do
      if [ "$(docker inspect -f '{{.State.Running}}' "$container")" != "true" ]; then
        echo "   🔄 Waiting for container to start..."
      elif [ "$(docker inspect -f '{{.State.Health.Status}}' "$container")" == "healthy" ]; then
        echo "✅ $container is healthy."
        return 0
      else
        echo "   🔄 Health: $(docker inspect -f '{{.State.Health.Status}}' "$container") (attempt $attempt/$MAX_ATTEMPTS)"
      fi
      sleep "$WAIT_SECONDS"
      attempt=$((attempt + 1))
    done
    echo "❌ $container did not become healthy in time."
    docker logs "$container" | tail -n 30
    exit 1
  else
    echo "⏳ Waiting for $container to be running..."
    while [ "$attempt" -le "$MAX_ATTEMPTS" ]; do
      if [ "$(docker inspect -f '{{.State.Running}}' "$container")" == "true" ]; then
        echo "✅ $container is running."
        sleep 3
        return 0
      fi
      echo "   🔄 Still waiting... (attempt $attempt/$MAX_ATTEMPTS)"
      sleep "$WAIT_SECONDS"
      attempt=$((attempt + 1))
    done
    echo "❌ $container failed to start in time."
    docker logs "$container" | tail -n 30
    exit 1
  fi
}

wait_for_eureka() {
  echo "⏳ Waiting for Eureka registry to be ready..."
  local max_attempts=30
  local wait_seconds=10

  for attempt in $(seq 1 $max_attempts); do
    if curl -fs http://localhost:8761/eureka/apps > /dev/null; then
      echo "✅ Eureka registry is ready to accept registrations."
      return 0
    fi
    echo "   🔄 Still waiting for Eureka... (attempt $attempt/$max_attempts)"
    sleep $wait_seconds
  done

  echo "❌ Eureka registry did not become ready in time."
  exit 1
}

# -------------------------------
# Main Startup Sequence
# -------------------------------
echo "🧼 Stopping any running containers..."
docker compose down --remove-orphans

echo "🐳 Starting infrastructure containers..."
docker compose up -d "${!infra_containers[@]}"

for key in "${!infra_containers[@]}"; do
  wait_for_container "${infra_containers[$key]}"
done

echo "🚀 Starting Config Server and Discovery Service..."
for svc in config-server discovery-service; do
  docker compose up -d "$svc"
  wait_for_container "${microservice_containers[$svc]}"
done

wait_for_eureka

echo "🔁 Starting API Gateway..."
docker compose up -d api-gateway
wait_for_container "${microservice_containers[api-gateway]}"

echo "📦 Starting business microservices..."
for svc in customer-service product-service order-service payment-service notification-service; do
  docker compose up -d "$svc"
  wait_for_container "${microservice_containers[$svc]}"
done

echo "🎉 All services started and healthy!"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"