package tech.tinkmaster.fluent.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionStatus;
import tech.tinkmaster.fluent.service.execution.ExecutionService;
import tech.tinkmaster.fluent.service.pipeline.PipelineService;

import java.io.IOException;
import java.util.List;

@Component
public class PipelineTaskDetector {
  private static final Logger LOG = LoggerFactory.getLogger(PipelineTaskDetector.class);

  @Autowired ExecutionService service;
  @Autowired PipelineService pipelineService;

  @Scheduled(fixedDelay = 10000)
  public void detectExecutionDiagram() {
    List<String> pipelines = this.pipelineService.list();

    pipelines.stream()
        .forEach(
            pipeline -> {
              try {
                List<Execution> diagrams = this.service.list(pipeline);
                diagrams.stream()
                    .forEach(
                        dia -> {
                          try {
                            Execution execution = this.service.get(pipeline, dia.getName());
                            if (execution.getStatus() == ExecutionStatus.CREATED
                                || execution.getStatus() == ExecutionStatus.WAITING_TO_BE_SCHEDULED
                                || execution.getStatus() == ExecutionStatus.RUNNING) {
                              LOG.info("Submit graph {} to scheduler service.", dia.getName());
                              PipelineSchedulerService.submit(execution);
                            }

                          } catch (IOException e) {
                            LOG.error("Fail to list execution graph.", e);
                          }
                        });

              } catch (IOException e) {
                LOG.error("Fail to list pipelines.", e);
              }
            });
  }
}
