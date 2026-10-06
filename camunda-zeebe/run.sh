#!/usr/bin/env bash
# Deploys order.bpmn to a local Zeebe via zbctl, starts an instance and runs one job worker.
# Needs docker + zbctl (https://github.com/camunda-community-hub/zeebe-client-go). Local broker only.
set -euo pipefail
cd "$(dirname "$0")"
docker compose up -d
sleep 20
zbctl --insecure deploy order.bpmn
zbctl --insecure create instance order-process --variables '{"orderId":"o-1"}'
zbctl --insecure create worker charge-payment --handler "cat" --maxJobsActive 1 &
sleep 5; kill %1
# destroy: docker compose down -v
