package tech.tinkmaster.fluent.api.server.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AppController {

  private final RedirectView rv = new RedirectView("/app/index.html", true, false);

  @RequestMapping("/")
  public RedirectView home() {
    System.out.println("aaaaaaaa");
    return this.rv;
  }

  @RequestMapping("/app")
  public RedirectView home2() {
    return this.rv;
  }

  @RequestMapping("/app/")
  public RedirectView home3() {
    return this.rv;
  }
}
