# conductor

A Conductor workflow definition (`order-workflow.json`), an offline validator (`validate.py`) and a registration script (`run.sh`).

## Goal

Define the order workflow as Conductor JSON with SIMPLE, WAIT and HTTP tasks, and show how to register and start it on a local server.

## Run it

```
python3 conductor/validate.py
```

Expected output: `conductor ok: 3 tasks`.

Live run (needs Docker and `curl`): `bash conductor/run.sh` starts `conductoross/conductor-standalone:3.15.0`, registers the `charge_payment` task definition and the workflow, then starts one execution. Stop with `docker compose down -v` in that folder.

Not run end to end: the server and `run.sh` were not started here; only `validate.py` was executed.

## What it proves

- `order-workflow.json` has `name`, `version`, `ownerEmail` and `tasks`, and the three task reference names (`charge`, `approval`, `ship`) are unique.
- `charge_payment` is registered by `run.sh` with 3 retries and exponential backoff.
- The WAIT task `approval` pauses the flow until it is completed externally.

## Trade-offs

- JSON is easy to generate but has no compile-time checks; the validator only covers a few fields.
- The SIMPLE task needs a worker that polls for `charge_payment`; none is included, so a live execution stops at the first task.
- The `ship` HTTP task calls the server's own health endpoint as a stand-in for a shipping service.

## When not to use it

- Logic-heavy flows that are clumsy in JSON; prefer a code-first engine.
