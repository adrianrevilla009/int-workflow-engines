# int-workflow-engines

Six small implementations of the same Orders process (charge, approve, ship, compensate) on different workflow engines, so you can compare how each one expresses retries, waits and rollback.

## What is inside

| Folder | What it shows | Run |
| --- | --- | --- |
| [`temporal`](./temporal) | Workflow with activity retries, an approval signal and a durable timer, on the in-process test server | `cd temporal && mvn -q compile exec:java` |
| [`camunda-zeebe`](./camunda-zeebe) | BPMN with a Zeebe service task (job type, 3 retries) and an offline validator | `python3 camunda-zeebe/validate.py` |
| [`flowable`](./flowable) | Embedded Flowable engine on in-memory H2 with a Java delegate and a user task | `cd flowable && mvn -q compile exec:java` |
| [`conductor`](./conductor) | Conductor workflow JSON (SIMPLE, WAIT, HTTP tasks) and a registration script | `python3 conductor/validate.py` |
| [`step-functions`](./step-functions) | Amazon States Language saga with Retry and Catch to a compensation state | `python3 step-functions/validate.py` |
| [`saga-in-workflow`](./saga-in-workflow) | Engine-independent saga: undo completed steps in reverse order | `python3 saga-in-workflow/saga.py` |

## Prerequisites

- Java 21 and Maven (`temporal`, `flowable`)
- Python 3 (all validators and the saga)
- Docker, `zbctl`, `curl` and `awscli` only for the optional live runs in `camunda-zeebe`, `conductor` and `step-functions`

## How to read it

Start with `saga-in-workflow` for the pattern, then `temporal` and `flowable` for code-first engines, and finish with the declarative ones (`camunda-zeebe`, `conductor`, `step-functions`). All of them use the small Orders domain.
