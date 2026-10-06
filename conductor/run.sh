#!/usr/bin/env bash
# Starts a local Conductor, registers the task + workflow, starts one execution.
set -euo pipefail
cd "$(dirname "$0")"
API=http://localhost:8080/api
docker compose up -d
until curl -sf http://localhost:8080/health >/dev/null; do sleep 3; done
curl -sf -X POST "$API/metadata/taskdefs" -H 'Content-Type: application/json' \
  -d '[{"name":"charge_payment","retryCount":3,"retryLogic":"EXPONENTIAL_BACKOFF","retryDelaySeconds":1,"timeoutSeconds":60,"responseTimeoutSeconds":30,"ownerEmail":"lab@example.org"}]'
curl -sf -X POST "$API/metadata/workflow" -H 'Content-Type: application/json' -d @order-workflow.json
curl -sf -X POST "$API/workflow/order_workflow" -H 'Content-Type: application/json' -d '{"orderId":"o-1"}'
echo
# destroy: docker compose down -v
