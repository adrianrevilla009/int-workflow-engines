"""Offline structural check of the ASL file: every Next/Catch target exists,
all states are reachable and a terminal state exists. No AWS needed."""
import json
import pathlib

sm = json.loads((pathlib.Path(__file__).parent / "order.asl.json").read_text())
states = sm["States"]
assert sm["StartAt"] in states, "StartAt missing"


def targets(s):
    if "Next" in s:
        yield s["Next"]
    for c in s.get("Catch", []):
        yield c["Next"]


seen, todo = set(), [sm["StartAt"]]
while todo:
    n = todo.pop()
    if n in seen:
        continue
    assert n in states, f"dangling target {n}"
    seen.add(n)
    todo.extend(targets(states[n]))
assert seen == set(states), f"unreachable: {set(states) - seen}"
assert any(s["Type"] in ("Succeed", "Fail") for s in states.values()), "no terminal state"
print("asl ok:", len(states), "states")
