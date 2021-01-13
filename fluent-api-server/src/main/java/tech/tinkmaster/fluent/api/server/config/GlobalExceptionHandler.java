package tech.tinkmaster.fluent.api.server.config;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.tinkmaster.fluent.api.server.responses.ResponseEntity;
import tech.tinkmaster.fluent.common.exceptions.FluentNotFoundException;
import tech.tinkmaster.fluent.common.exceptions.FluentPipelineCircleDetectedException;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler
  @ResponseBody
  public ResponseEntity process(HttpServletResponse response, Exception e) {
    LOG.error("In Global Exception Handler.", e);
    response.setStatus(400);

    if (FluentNotFoundException.class == e.getClass()) {
      response.setStatus(404);
      return ResponseEntity.builder().code(404).build();
    } else if (FluentPipelineCircleDetectedException.class == e.getClass()) {
      return ResponseEntity.builder().code(10001).message(e.getMessage()).data(null).build();
    }
    return ResponseEntity.builder().code(400).message(e.getMessage()).build();
  }
}
