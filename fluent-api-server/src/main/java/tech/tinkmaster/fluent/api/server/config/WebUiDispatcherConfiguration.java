package tech.tinkmaster.fluent.api.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebUiDispatcherConfiguration implements WebMvcConfigurer {

  /**
   * This method is used for proxy web ui resources
   *
   * @param registry
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/app/**")
        .addResourceLocations("classpath:/fluent-web/")
        .resourceChain(false);
  }
}
