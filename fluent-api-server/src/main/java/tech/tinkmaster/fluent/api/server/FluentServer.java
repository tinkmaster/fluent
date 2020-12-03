package tech.tinkmaster.fluent.api.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

// import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
// @EnableMongoRepositories("tech.tinkmaster.fluent.persistence")
@ComponentScan("tech.tinkmaster.fluent")
@EnableScheduling
public class FluentServer {
  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(FluentServer.class, args);
  }
}
