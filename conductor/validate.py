"""Offline check of order-workflow.json: required fields and unique task reference names."""
import json
import pathlib

wf = json.loads((pathlib.Path(__file__).parent / "order-workflow.json").read_text())
for key in ("name", "version", "tasks", "ownerEmail"):
    assert key in wf, f"missing {key}"
refs = [t["taskReferenceName"] for t in wf["tasks"]]
assert len(refs) == len(set(refs)), "duplicate taskReferenceName"
assert all("type" in t for t in wf["tasks"])
print("conductor ok:", len(refs), "tasks")
