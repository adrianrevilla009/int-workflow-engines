"""Saga for the Orders domain: reserve stock -> charge payment -> create shipment.
On failure, completed steps are compensated in reverse order. Engine-agnostic: the
same shape maps to a Temporal workflow, a BPMN compensation scope or a Conductor fork."""

log = []


def step(name, fail=False):
    def do(order):
        if fail:
            raise RuntimeError(f"{name} failed")
        log.append(f"do:{name}")
    return do


def undo(name):
    return lambda order: log.append(f"undo:{name}")


def run_saga(order, steps):
    done = []
    try:
        for name, action, compensation in steps:
            action(order)
            done.append((name, compensation))
        return "COMPLETED"
    except RuntimeError as e:
        log.append(f"error:{e}")
        for name, compensation in reversed(done):
            compensation(order)
        return "COMPENSATED"


def steps(fail_at=None):
    return [(n, step(n, n == fail_at), undo(n)) for n in ("reserve", "charge", "ship")]


if __name__ == "__main__":
    order = {"id": "o-1"}
    assert run_saga(order, steps()) == "COMPLETED" and log == ["do:reserve", "do:charge", "do:ship"]
    log.clear()
    assert run_saga(order, steps("ship")) == "COMPENSATED"
    assert log == ["do:reserve", "do:charge", "error:ship failed", "undo:charge", "undo:reserve"], log
    print("saga ok")
