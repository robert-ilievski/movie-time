package mk.ukim.finki.movietime.web;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.movietime.model.Movie;
import mk.ukim.finki.movietime.service.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

  private final MovieService movieService;

  @GetMapping("/{movieName}")
  public String getMovieDetails(Model model, @PathVariable String movieName) {
    if (movieName != null) {
      Movie movie = movieService.getMovieDetails(movieName);
      model.addAttribute("movie", movie);
      return "movieDetails";
    }
    return "home";
  }
}
