package lab;

import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/** Runs the workflow on the in-process time-skipping test server (no Temporal cluster needed). */
public class Main {
    public static void main(String[] args) {
        AtomicInteger calls = new AtomicInteger();
        OrderWorkflow.Activities flaky = orderId -> {
            if (calls.incrementAndGet() < 3) {
                throw new IllegalStateException("payment gateway down");
            }
            return "CHARGED:" + orderId;
        };
        TestWorkflowEnvironment env = TestWorkflowEnvironment.newInstance();
        Worker worker = env.newWorker("orders");
        worker.registerWorkflowImplementationTypes(OrderWorkflow.Impl.class);
        worker.registerActivitiesImplementations(flaky);
        env.start();

        OrderWorkflow wf = env.getWorkflowClient().newWorkflowStub(OrderWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue("orders").build());
        io.temporal.client.WorkflowClient.start(wf::process, "o-1");
        env.sleep(Duration.ofHours(1)); // skipped instantly by the test server
        wf.approve();
        String result = io.temporal.client.WorkflowStub.fromTyped(wf).getResult(String.class);
        if (!result.equals("CHARGED:o-1") || calls.get() != 3) {
            throw new AssertionError(result + " after " + calls.get() + " calls");
        }
        System.out.println("temporal ok: " + result + " after " + calls.get() + " attempts");
        env.close();
    }
}
