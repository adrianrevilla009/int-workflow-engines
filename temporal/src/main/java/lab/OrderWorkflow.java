package lab;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.time.Duration;

/** Orders workflow: activity with retries, a human-approval signal and a durable timer. */
@WorkflowInterface
public interface OrderWorkflow {
    @WorkflowMethod
    String process(String orderId);

    @SignalMethod
    void approve();

    @ActivityInterface
    interface Activities {
        String charge(String orderId);
    }

    class Impl implements OrderWorkflow {
        private boolean approved;
        private final Activities act = Workflow.newActivityStub(Activities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(5))
                        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
                        .build());

        @Override
        public String process(String orderId) {
            // durable timer: wait up to 1 day for approval, survives worker restarts
            if (!Workflow.await(Duration.ofDays(1), () -> approved)) {
                return "EXPIRED";
            }
            return act.charge(orderId);
        }

        @Override
        public void approve() {
            approved = true;
        }
    }
}
