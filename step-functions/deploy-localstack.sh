#!/usr/bin/env bash
# Needs docker + awscli. Talks to LocalStack only (dummy credentials), never to real AWS.
set -euo pipefail
cd "$(dirname "$0")"
export AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test AWS_DEFAULT_REGION=us-east-1
docker compose up -d
aws --endpoint-url http://localhost:4566 stepfunctions create-state-machine \
  --name orders --definition file://order.asl.json \
  --role-arn arn:aws:iam::000000000000:role/sfn
# destroy: docker compose down -v
