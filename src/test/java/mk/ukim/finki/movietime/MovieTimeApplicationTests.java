package mk.ukim.finki.movietime;

import mk.ukim.finki.movietime.model.Genre;
import mk.ukim.finki.movietime.model.Movie;
import mk.ukim.finki.movietime.service.impl.GenreServiceImpl;
import mk.ukim.finki.movietime.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieTimeApplicationTests {

  @InjectMocks
  private GenreServiceImpl genreService;

  @InjectMocks
  private MovieServiceImpl movieService;

  @Test
  public void testGenre() {
    Genre genre = genreService.getGenre("Action_film");
    System.out.println("test");
  }

  @Test
  public void testMovie() {
    Movie movie = movieService.getMovieDetails("The_Island_(2005_film)");
    System.out.println("test");
  }
}
