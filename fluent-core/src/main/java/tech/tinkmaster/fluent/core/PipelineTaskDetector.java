package tech.tinkmaster.fluent.core;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.tinkmaster.fluent.common.entity.execution.Execution;
import tech.tinkmaster.fluent.common.entity.execution.ExecutionStatus;
import tech.tinkmaster.fluent.service.execution.ExecutionService;
import tech.tinkmaster.fluent.service.pipeline.PipelineService;

@Component
public class PipelineTaskDetector {
  private static final Logger LOG = LoggerFactory.getLogger(PipelineTaskDetector.class);

  @Autowired ExecutionService service;
  @Autowired PipelineService pipelineService;

  @Scheduled(fixedDelay = 10000)
  public void detectExecutionGraph() {
    List<String> pipelines = this.pipelineService.list();

    pipelines
        .stream()
        .forEach(
            pipeline -> {
              try {
                List<Execution> graphs = this.service.list(pipeline);
                graphs
                    .stream()
                    .forEach(
                        dia -> {
                          try {
                            Execution execution = this.service.get(pipeline, dia.getName());
                            if (ExecutionStatus.needToRun(execution.getStatus())) {
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
