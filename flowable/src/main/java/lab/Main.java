package lab;

import java.util.Map;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

/** Embedded Flowable on in-memory H2: deploy BPMN, run service task, complete user task. */
public class Main {
    public static void main(String[] args) {
        ProcessEngine engine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
                .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
                .buildProcessEngine();
        engine.getRepositoryService().createDeployment().addClasspathResource("order.bpmn20.xml").deploy();

        ProcessInstance pi = engine.getRuntimeService().startProcessInstanceByKey("order", Map.of("orderId", "o-1"));
        Task task = engine.getTaskService().createTaskQuery().processInstanceId(pi.getId()).singleResult();
        Object charged = engine.getRuntimeService().getVariable(pi.getId(), "charged");
        engine.getTaskService().complete(task.getId());
        long running = engine.getRuntimeService().createProcessInstanceQuery().count();
        if (!Boolean.TRUE.equals(charged) || running != 0) {
            throw new AssertionError("charged=" + charged + " running=" + running);
        }
        System.out.println("flowable ok: charged, approved, instance finished");
        engine.close();
    }
}
