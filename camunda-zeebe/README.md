# camunda-zeebe

A BPMN process (`order.bpmn`) for Camunda 8 / Zeebe, an offline validator (`validate.py`), and a script (`run.sh`) for a local broker.

## Goal

Model the order process as BPMN with a Zeebe job type and retries, and check the model without needing a broker.

## Run it

```
python3 camunda-zeebe/validate.py
```

Expected output: `bpmn ok: order-process`.

Live run (needs Docker and `zbctl`): `bash camunda-zeebe/run.sh` starts `camunda/zeebe:8.5.9`, deploys the process, creates an instance and attaches a worker for 5 seconds. Stop with `docker compose down -v` in that folder.

Not run end to end: the broker and `run.sh` were not started here; only `validate.py` was executed.

## What it proves

- `order.bpmn` is well-formed XML with one executable process, `order-process`.
- The `charge` service task has the Zeebe task definition `charge-payment` with `retries="3"`.
- Every sequence flow connects existing elements (start, charge, end).

## Trade-offs

- The validator checks structure only; it does not check Zeebe deployment rules.
- `run.sh` uses `cat` as the worker handler, so jobs are not completed; a real worker client is needed.
- The `sleep 20` wait for the broker is a guess, not a health check.

## When not to use it

- Teams with no need for business-visible process models; a code-first engine is lighter.
- Single-service flows where a broker cluster is more infrastructure than the problem needs.
