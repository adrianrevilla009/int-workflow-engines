"""Offline check of order.bpmn: well-formed XML, one executable process, every service task
has a Zeebe job type, and every sequence flow endpoint exists."""
import pathlib
import xml.etree.ElementTree as ET

B = "{http://www.omg.org/spec/BPMN/20100524/MODEL}"
Z = "{http://camunda.org/schema/zeebe/1.0}"
root = ET.parse(pathlib.Path(__file__).parent / "order.bpmn").getroot()
proc = root.find(B + "process")
assert proc is not None and proc.get("isExecutable") == "true"
ids = {e.get("id") for e in proc if e.get("id")}
for t in proc.findall(B + "serviceTask"):
    assert t.find(f"{B}extensionElements/{Z}taskDefinition").get("type"), t.get("id")
for f in proc.findall(B + "sequenceFlow"):
    assert f.get("sourceRef") in ids and f.get("targetRef") in ids, f.get("id")
print("bpmn ok:", proc.get("id"))
