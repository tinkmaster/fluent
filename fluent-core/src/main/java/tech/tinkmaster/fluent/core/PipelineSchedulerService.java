package tech.tinkmaster.fluent.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.core.scheduler.ExecutionScheduler;
import tech.tinkmaster.fluent.core.scheduler.SchedulerStatus;
import tech.tinkmaster.fluent.service.execution.ExecutionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PipelineSchedulerService implements ApplicationContextAware {
  private static final Logger LOG = LoggerFactory.getLogger(PipelineSchedulerService.class);
  private ExecutorService executorService;
  private List<ExecutionScheduler> schedulerList;
  private List<ExecutionDiagram> diagramsWaitingList;

  @Autowired private ExecutionService executionService;
  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  private PipelineSchedulerService() {
    int cpuCores = 1;
    this.executorService = Executors.newFixedThreadPool(cpuCores);
    this.schedulerList = new ArrayList<>(cpuCores);
    this.diagramsWaitingList = new LinkedList<>();
    for (int i = 0; i < cpuCores; i++) {
      ExecutionScheduler scheduler = new ExecutionScheduler(i);
      this.schedulerList.add(i, scheduler);
      this.executorService.submit(scheduler);
    }
  }

  private static PipelineSchedulerService schedulerService;

  public static synchronized void submit(ExecutionDiagram diagram) {
    if (schedulerService == null) {
      schedulerService = applicationContext.getBean(PipelineSchedulerService.class);
    }

    for (ExecutionScheduler scheduler : schedulerService.schedulerList) {
      if (scheduler.getStatus().equals(SchedulerStatus.IDLE)) {
        scheduler.assign(diagram);
        LOG.info(
            "Execution diagram {} is assigned to scheduler-{}.",
            diagram.getName(),
            scheduler.getScheduleId());
        return;
      }
    }

    schedulerService.diagramsWaitingList.add(diagram);
  }

  public static void updateDiagramStatus(ExecutionDiagram diagram) {
    try {
      schedulerService.executionService.updateOrCreate(diagram);
    } catch (IOException e) {
      // ignored
    }
  }
}
