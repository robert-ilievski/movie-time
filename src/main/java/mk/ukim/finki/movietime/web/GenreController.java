package mk.ukim.finki.movietime.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.movietime.model.Genre;
import mk.ukim.finki.movietime.service.GenreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/genre")
@RequiredArgsConstructor
public class GenreController {

  private final GenreService genreService;

  @GetMapping("/{genreName}")
  public String getGenre(Model model, @PathVariable String genreName) {
    Genre genre = genreService.getGenre(genreName);
    model.addAttribute("genre", genre);
    return "genrePage";
  }
}
