package tech.tinkmaster.fluent.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.core.scheduler.ExecutionScheduler;
import tech.tinkmaster.fluent.core.scheduler.SchedulerStatus;
import tech.tinkmaster.fluent.service.execution.ExecutionService;
import tech.tinkmaster.fluent.service.variable.VariableService;

@Component
public class PipelineSchedulerService implements ApplicationContextAware {
  private static final Logger LOG = LoggerFactory.getLogger(PipelineSchedulerService.class);
  private ExecutorService executorService;
  private List<ExecutionScheduler> schedulerList;
  private List<Execution> graphsWaitingList;

  @Autowired private ExecutionService executionService;
  @Autowired private VariableService variableService;
  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  private PipelineSchedulerService() {
    int cpuCores = Runtime.getRuntime().availableProcessors();
    this.executorService = Executors.newFixedThreadPool(1);
    this.schedulerList = new ArrayList<>(cpuCores);
    this.graphsWaitingList = new LinkedList<>();
    for (int i = 0; i < cpuCores; i++) {
      ExecutionScheduler scheduler = new ExecutionScheduler(i);
      this.schedulerList.add(i, scheduler);
      this.executorService.submit(scheduler);
    }
  }

  private static PipelineSchedulerService schedulerService;

  public static synchronized void submit(Execution execution) {
    if (schedulerService == null) {
      schedulerService = applicationContext.getBean(PipelineSchedulerService.class);
    }

    for (ExecutionScheduler scheduler : schedulerService.schedulerList) {
      if (schedulerService
          .schedulerList
          .stream()
          .noneMatch(
              sche ->
                  sche.getExecution() != null
                      && execution.getName().equals(sche.getExecution().getName()))) {
        if (scheduler.getSchdulerStatus().equals(SchedulerStatus.IDLE)) {
          scheduler.assign(execution);
          LOG.info(
              "Execution execution {} is assigned to scheduler-{}.",
              execution.getName(),
              scheduler.getScheduleId());
          return;
        }
      }
    }

    schedulerService.graphsWaitingList.add(execution);
  }

  public static void updateGraphStatus(Execution graph) {
    try {
      schedulerService.executionService.updateOrCreate(graph);
    } catch (IOException e) {
      // ignored
    }
  }

  public static VariableService getVariableService() {
    return schedulerService.variableService;
  }
}
