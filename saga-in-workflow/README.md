# saga-in-workflow

A pure-Python saga (`saga.py`) for reserve stock, charge payment and create shipment, with reverse-order compensation.

## Goal

Show the saga pattern, doing steps and undoing completed ones in reverse when a later step fails, independent of any workflow engine.

## Run it

```
python3 saga-in-workflow/saga.py
```

Expected output: `saga ok`. Python 3 standard library only.

## What it proves

- On the happy path `run_saga` returns `COMPLETED` and the log is `do:reserve`, `do:charge`, `do:ship`.
- When `ship` fails, it returns `COMPENSATED` and the log is `do:reserve`, `do:charge`, `error:ship failed`, `undo:charge`, `undo:reserve`.
- The step list is data (name, action, compensation), so the same shape maps onto a Temporal workflow, a BPMN compensation scope or the `Catch` states in `../step-functions/order.asl.json`.

## Trade-offs

- State lives in memory: a crash mid-saga loses track of what was done. An engine adds the durability.
- Compensations must be idempotent; here they only append to a log, and a failing compensation is not handled.
- Only `RuntimeError` triggers compensation.

## When not to use it

- When a single local database transaction can cover all the steps.
