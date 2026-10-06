# step-functions

An Amazon States Language definition (`order.asl.json`), an offline validator (`validate.py`) and a LocalStack deploy script (`deploy-localstack.sh`).

## Goal

Express the order saga as a state machine with retry and a catch that routes to a compensation step.

## Run it

```
python3 step-functions/validate.py
```

Expected output: `asl ok: 5 states`.

Optional LocalStack deploy (needs Docker and `awscli`; uses dummy credentials and never contacts real AWS): `bash step-functions/deploy-localstack.sh`, then `docker compose down -v` to clean up. Cost: none on LocalStack; real AWS Standard workflows bill per state transition.

Not run end to end: LocalStack and the deploy script were not started here, and the Lambda functions named in the ARNs are not implemented.

## What it proves

- `ReserveStock` retries twice with backoff and sends any error to `Failed`.
- If `ChargePayment` fails, its `Catch` goes to `ReleaseStock`, which then ends in `Failed`: the compensation path.
- The validator confirms every `Next`/`Catch` target exists, all 5 states are reachable and a terminal state exists.

## Trade-offs

- ASL is declarative and AWS-managed, but ties you to AWS and has limited local testing.
- The validator checks graph structure only, not ARNs, input paths or IAM.
- `ChargePayment` has no retry, only a catch.

## When not to use it

- Multi-cloud or on-prem requirements.
- Very high-frequency short flows where per-transition pricing dominates.
