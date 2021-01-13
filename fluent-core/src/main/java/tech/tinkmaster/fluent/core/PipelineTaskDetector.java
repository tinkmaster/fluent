package tech.tinkmaster.fluent.core;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionDiagram;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionStatus;
import tech.tinkmaster.fluent.service.execution.ExecutionService;
import tech.tinkmaster.fluent.service.pipeline.PipelineService;

@Component
public class PipelineTaskDetector {
  private static final Logger LOG = LoggerFactory.getLogger(PipelineTaskDetector.class);

  @Autowired ExecutionService service;
  @Autowired PipelineService pipelineService;

  @Scheduled(fixedDelay = 10000)
  public void detectExecutionDiagram() {
    List<String> pipelines = this.pipelineService.list();

    pipelines
        .stream()
        .forEach(
            pipeline -> {
              try {
                List<ExecutionDiagram> diagrams = this.service.list(pipeline);
                diagrams
                    .stream()
                    .forEach(
                        dia -> {
                          try {
                            ExecutionDiagram executionDiagram =
                                this.service.get(pipeline, dia.getName());
                            if (executionDiagram.getStatus() == ExecutionStatus.CREATED
                                || executionDiagram.getStatus()
                                    == ExecutionStatus.WAITING_TO_BE_SCHEDULED
                                || executionDiagram.getStatus() == ExecutionStatus.RUNNING) {
                              LOG.info("Submit diagram {} to scheduler service.", dia.getName());
                              PipelineSchedulerService.submit(executionDiagram);
                            }

                          } catch (IOException e) {
                            LOG.error("Fail to list execution diagrams.", e);
                          }
                        });

              } catch (IOException e) {
                LOG.error("Fail to list pipelines.", e);
              }
            });
  }
}
