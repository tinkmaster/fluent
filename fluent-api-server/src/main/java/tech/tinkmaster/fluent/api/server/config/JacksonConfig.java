package tech.tinkmaster.fluent.api.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.tinkmaster.fluent.common.FluentObjectMappers;

@Configuration
@EnableWebMvc
public class JacksonConfig implements WebMvcConfigurer {

  @Bean
  public ObjectMapper objectMapper() {
    return FluentObjectMappers.getNewConfiguredJsonMapper();
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins("http://localhost:3000")
        .allowCredentials(true)
        .allowedMethods("GET", "POST", "PUT", "DELETE");
  }
}
