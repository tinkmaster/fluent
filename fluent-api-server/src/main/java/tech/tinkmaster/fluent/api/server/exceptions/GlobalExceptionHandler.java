package tech.tinkmaster.fluent.api.server.exceptions;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler
  public void process(HttpServletResponse response, Exception e) {
    LOG.error("In Global Exception Handler.", e);

    if (FluentNotFoundException.class == e.getClass()) {
      response.setStatus(404);
    }
  }
}
