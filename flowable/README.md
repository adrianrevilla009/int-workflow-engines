# flowable

An embedded Flowable BPMN engine started from `Main.java`, with `order.bpmn20.xml` and a Java delegate in `ChargeDelegate.java`.

## Goal

Show how to run a BPMN process engine inside a plain Java process on in-memory H2, with no server or container.

## Run it

```
cd flowable
mvn -q compile exec:java
```

Expected last line: `flowable ok: charged, approved, instance finished` (Liquibase log lines appear before it). Needs Java 21 and Maven; Flowable 7.0.1 and H2 2.2.224 are pinned in `pom.xml`.

## What it proves

- `order.bpmn20.xml` deploys and starts: start event, service task `charge`, user task `approve`, end event.
- The service task runs `ChargeDelegate`, which sets the process variable `charged` to `true`; `Main` reads it while the instance waits at the user task.
- Completing the `Approve order` user task finishes the instance, and the running-instance count drops to 0.

## Trade-offs

- The engine shares your JVM and database: simple and transactional, but scaling and upgrades are tied to the application.
- H2 is in-memory, so all process state is lost when the program exits.
- The process has no error handling or timers; it is the smallest happy path.

## When not to use it

- When several services need one shared orchestrator; use a standalone engine such as Zeebe or Temporal.
