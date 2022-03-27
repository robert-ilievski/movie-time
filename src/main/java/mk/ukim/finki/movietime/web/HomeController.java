package mk.ukim.finki.movietime.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = {"/", "/home"})
@RequiredArgsConstructor
public class HomeController {

  @GetMapping
  public String getHomePage() {
    return "home";
  }
}
