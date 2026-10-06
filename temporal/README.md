# temporal

A Temporal order workflow (`OrderWorkflow.java`) and a runner (`Main.java`) that executes it on the in-process test server.

## Goal

Show a Temporal workflow that retries a flaky activity, waits for a human approval signal and uses a durable timer, without needing a cluster to try it.

## Run it

```
cd temporal
mvn -q compile exec:java
```

Expected last line: `temporal ok: CHARGED:o-1 after 3 attempts`. Needs Java 21 and Maven; the SDK is pinned to 1.25.1 in `pom.xml`.

Optional real dev server: `docker compose up -d` (UI on http://localhost:8233), stop with `docker compose down -v`. `Main` does not connect to it.

## What it proves

- The `charge` activity in `Main.java` fails twice with "payment gateway down" and succeeds on the third attempt, matching `setMaximumAttempts(3)` in `OrderWorkflow.java`.
- The workflow waits on `Workflow.await(Duration.ofDays(1), ...)` until the `approve` signal arrives; the test environment skips the time, so the run takes seconds.
- If the signal never came, the workflow would return `EXPIRED` after one day.

## Trade-offs

- Workflow code must be deterministic: no random numbers, clocks or I/O outside activities.
- The test server is not a real cluster, so persistence and worker restarts are not exercised here.
- Running Temporal for real means operating a server plus workers.

## When not to use it

- Simple request/response flows with no long waits, retries or human steps; a plain queue consumer is cheaper to run.
